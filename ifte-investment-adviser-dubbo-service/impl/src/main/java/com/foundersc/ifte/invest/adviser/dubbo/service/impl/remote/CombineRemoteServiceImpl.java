package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.common.util.DateUtils;
import com.foundersc.ifc.portfolio.t2.enums.v2.FlagEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CopywritingTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.ProdInvestTypeEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.CombInfoDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.CombPositionDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.CopywritingDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.HisCombpriceExtDTO;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.*;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.*;
import com.foundersc.ifc.portfolio.t2.service.v2.CombineService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombTrendRangeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy.StrategyFactory;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy.TradeRuleStrategy;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.support.IncomeRatioCalculator;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.support.TradeTimeline;
import com.foundersc.ifte.invest.adviser.dubbo.util.*;
import com.foundersc.itc.product.model.TradeCalendar;
import com.foundersc.itc.product.service.TradeCalendarRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.*;
import static com.foundersc.ifte.invest.adviser.dubbo.util.CacheConstants.COMB_INFO;
import static com.foundersc.ifte.invest.adviser.dubbo.util.CacheConstants.COMB_POS;

/**
 * @author wangfuwei
 * @date 2022/9/26
 */
@Service("combineRemoteService")
@Slf4j
public class CombineRemoteServiceImpl implements CombineRemoteService {

    @Autowired
    private CombineService combineService;

    @Autowired
    private TradeCalendarRemoteService tradeCalendarRemoteService;

    @Autowired
    private TradeTimeline tradeTimeline;

    /**
     * 组合成份最多显示数量
     */
    private final int MAX_POSITION_TYPE_NUM = 5;

    /**
     * 历史行情最早成立日期，用来查询所有历史行情
     */
    private final int HIS_PRICE_BEGIN_DATE = 20000101;

    /**
     * 组合成分类型在prod_tag_json中的key
     */
    private final static String PROD_TYPE_NAME_KEY = "prodType1Name";

    @Override
    @Cacheable(cacheNames = COMB_INFO, key = "#combineCode", unless = "#result == null")
    public CombineInfo getCombInfo(SimpleAccount simpleAccount, String combineCode) {
        if (StringUtils.isBlank(combineCode)) {
            log.warn("[getCombInfo] param error, combineCode is blank");
            throw new BusinessException(ErrorCodeEnum.COMB_CODE_ERROR);
        }
        CombInfoDTO combInfoDTO = queryCombInfo(simpleAccount, combineCode);
        CombineInfo result = new CombineInfo();
        BeanCopyUtil.copyUnderlineProperties(combInfoDTO, result);
        result.setEndPeriod(FlagEnum.Y.getCode().equalsIgnoreCase(combInfoDTO.getIs_end_period()));
        Pair<Integer, String> pair = getSetUpDateFromHisPrice(simpleAccount, combineCode);
        boolean isSetUpMoreThanOneYear = isSetUpMoreThanOneYear(pair.getLeft());
        log.info("setUpDate={}, isSetUpMoreThanOneYear={}", pair.getLeft(), isSetUpMoreThanOneYear);
        result.setSetUpDate(pair.getLeft());
        result.setSetUpMoreThanOneYear(isSetUpMoreThanOneYear);
        result.setTodayIncomeRatio(pair.getRight());
        return result;
    }

    /**
     * 通过查询历史行情的方式，确定组合成立日期
     * 历史行情中的第一条的净值日期作为组合成立日期
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    private Pair<Integer, String> getSetUpDateFromHisPrice(SimpleAccount simpleAccount, String combineCode) {
        QueryCombHisPriceReq queryCombHisPriceReq = new QueryCombHisPriceReq();
        queryCombHisPriceReq.setBeginDate(HIS_PRICE_BEGIN_DATE);
        queryCombHisPriceReq.setEndDate(DateUtil.dateToInt(new Date()));
        queryCombHisPriceReq.setCombineCode(combineCode);
        BaseResult<QueryCombHisPriceResp> baseResult = combineService.queryCombHisPrice(simpleAccount, queryCombHisPriceReq);
        QueryCombHisPriceResp queryCombHisPriceResp = getQueryCombHisPriceResp(baseResult);

        if (CollectionUtils.isEmpty(queryCombHisPriceResp.getRows())) {
            log.warn("queryCombHisPrice rows is empty");
            // 未查询到历史行情，则返回今天（包括）的下一个交易日
            return Pair.of(tradeCalendarRemoteService.queryNextTradeDate(new Date()), null);
        }
        // 第一条作为净值日期
        List<HisCombpriceExtDTO> hisCombpriceExtDTOS = queryCombHisPriceResp.getRows();
        return Pair.of(hisCombpriceExtDTOS.get(0).getCom_net_date(), hisCombpriceExtDTOS.get(hisCombpriceExtDTOS.size() - 1).getCom_today_income_ratio());
    }

    /**
     * 判断组合是否成立满一年
     *
     * @param setupDate
     * @return
     */
    public boolean isSetUpMoreThanOneYear(Integer setupDate) {
        if (setupDate == null) {
            return false;
        }
        // 成立日期
        LocalDate setupLocalDate = DateUtil.intToLocalDate(setupDate);
        return setupLocalDate.isBefore(DateUtil.lastYearLocalDate());
    }

    /**
     * 历史行情响应
     *
     * @param baseResult
     * @return
     */
    private QueryCombHisPriceResp getQueryCombHisPriceResp(BaseResult<QueryCombHisPriceResp> baseResult) {
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMB_HIS_PRICE_ERROR + "queryCombHisPrice failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_HIS_PRICE_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COMB_HIS_PRICE_ERROR + "queryCombHisPrice failed, data is null");
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_HIS_PRICE_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COMB_HIS_PRICE_ERROR + "queryCombHisPrice failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_HIS_PRICE_ERROR);
        }
        return baseResult.getData();
    }


    /**
     * 从BOP查询组合适当性信息
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    @Deprecated
    private QueryCombEligInfoResp queryCombEligInfo(SimpleAccount simpleAccount, String combineCode) {
        BaseResult<QueryCombEligInfoResp> baseResult = combineService.queryCombEligInfo(simpleAccount, combineCode);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMB_ELIG_ERROR + "queryCombEligInfo failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_ELIG_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COMB_ELIG_ERROR + "queryCombEligInfo failed, data is null");
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_ELIG_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COMB_ELIG_ERROR + "queryCombEligInfo failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_ELIG_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 调用t2查询组合信息
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    private CombInfoDTO queryCombInfo(SimpleAccount simpleAccount, String combineCode) {
        QueryCombInfoReq queryCombInfoReq = new QueryCombInfoReq();
        queryCombInfoReq.setCombineCode(combineCode);
        BaseResult<QueryCombInfoResp> baseResult = combineService.queryCombInfo(simpleAccount, queryCombInfoReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_CODE_INFO_ERROR + "queryCombInfo failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(QUERY_CODE_INFO_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_CODE_INFO_ERROR + "queryCombInfo failed, data is null");
            throw new BusinessException(QUERY_CODE_INFO_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_CODE_INFO_ERROR + "queryCombInfo failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(QUERY_CODE_INFO_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error(QUERY_CODE_INFO_ERROR + "queryCombInfo failed, combInfos is empty");
            throw new BusinessException(QUERY_CODE_INFO_ERROR);
        }
        if (baseResult.getData().getRows().size() > 1) {
            log.warn("queryCombInfo get multi combInfos");
        }
        return baseResult.getData().getRows().get(0);
    }

    @Override
    public IncomeRatioChart getIncomeRationChart(SimpleAccount simpleAccount, Integer rangeId, String combineCode) {
        CombTrendRangeEnum combTrendRangeEnum = CombTrendRangeEnum.parseByRangeId(rangeId);
        if (combTrendRangeEnum == null) {
            log.error("getIncomeRationChart param error, rangeId={}", rangeId);
            throw new BusinessException(ErrorCodeEnum.RANGE_ID_ERROR);
        }
        CombineInfo combInfo = getCombInfo(simpleAccount, combineCode);
        Integer setUpDate = combInfo.getSetUpDate();
        if (setUpDate == null) {
            log.error(QUERY_COMB_HIS_PRICE_ERROR + "combInfo beginValidDate is null");
            throw new BusinessException(ErrorCodeEnum.SETUP_DATE_ERROR);
        }
        Date[] showRange = combTrendRangeEnum.getShowRange(DateUtil.intToDate(setUpDate), getLastTradeDate());
        BaseResult<QueryCombHisPriceResp> baseResult = combineService.queryCombHisPrice(simpleAccount, buildQueryCombHisPriceReq(combineCode, showRange[0]));
        QueryCombHisPriceResp queryCombHisPriceResp = getQueryCombHisPriceResp(baseResult);
        if (CollectionUtils.isEmpty(queryCombHisPriceResp.getRows())) {
            log.warn("queryCombHisPrice rows is empty");
            return new IncomeRatioChart(combTrendRangeEnum);
        }
        IncomeRatioChart incomeRatioChart = getIncomeRatioChart(baseResult.getData().getRows(), combTrendRangeEnum);
        incomeRatioChart.setRangeId(combTrendRangeEnum.getRangeId());
        incomeRatioChart.setRangeDesc(combTrendRangeEnum.getRangeDesc());
        return incomeRatioChart;
    }

    private QueryCombHisPriceReq buildQueryCombHisPriceReq(String combineCode, Date beginDate) {
        QueryCombHisPriceReq queryCombHisPriceReq = new QueryCombHisPriceReq();
        queryCombHisPriceReq.setCombineCode(combineCode);
        queryCombHisPriceReq.setBeginDate(Integer.parseInt(DateUtils.formatDate(beginDate, CommonConstants.DEFAULT_DATE_FORMAT)));
        return queryCombHisPriceReq;
    }

    /**
     * 获取当前日期的上一个交易日
     *
     * @return
     */
    private Date getLastTradeDate() {
        TradeCalendar tradeCalendar = tradeCalendarRemoteService.queryTradeDate(new Date());
        return tradeCalendar.getLastTradeDate();
    }

    /**
     * 行情数据转为趋势图
     *
     * @param hisCombpriceExts
     * @param combTrendRangeEnum
     * @return
     */
    private IncomeRatioChart getIncomeRatioChart(List<HisCombpriceExtDTO> hisCombpriceExts, CombTrendRangeEnum combTrendRangeEnum) {
        if (CollectionUtils.isEmpty(hisCombpriceExts)) {
            log.warn("hisCombpriceExts is empty");
            return new IncomeRatioChart(combTrendRangeEnum);
        }
        List<HisCombpriceExtDTO> sortedHisCombpriceList = hisCombpriceExts.stream().sorted(Comparator.comparing(HisCombpriceExtDTO::getCom_net_date)).collect(Collectors.toList());
        IncomeRatioChart result = new IncomeRatioChart(combTrendRangeEnum);
        // 区间中组合第一天的累计收益率
        String firstComSumIncomeRatio = sortedHisCombpriceList.get(0).getCom_sum_income_ratio();
        // 区间中组合最后一天的累计收益率
        String lastComSumIncomeRatio = sortedHisCombpriceList.get(sortedHisCombpriceList.size() - 1).getCom_sum_income_ratio();
        // 使用组合累计收益率计算组合收益率
        BigDecimal incomeRatio = IncomeRatioCalculator.calcIncomeRatioByStrSumIncomeRatio(firstComSumIncomeRatio, lastComSumIncomeRatio);
        result.getCombineLine().setIncomeRatio(incomeRatio);
        result.getCombineLine().setIncomeRatioDesc(IncomeRatioCalculator.formatCalIncomeRatio(incomeRatio));
        // 区间中组合基准第一天的累计收益率
        String firstComSumBenchmarkRatio = sortedHisCombpriceList.get(0).getCom_sum_benchmark_ratio();
        // 区间中组合基准最后一天的累计收益率
        String lastComSumBenchmarkRatio = sortedHisCombpriceList.get(sortedHisCombpriceList.size() - 1).getCom_sum_benchmark_ratio();
        // 使用组合基准累计收益率计算基准收益率
        BigDecimal benchmarkIncomeRatio = IncomeRatioCalculator.calcIncomeRatioByStrSumIncomeRatio(firstComSumBenchmarkRatio, lastComSumBenchmarkRatio);
        result.getBenchmarkLine().setIncomeRatio(benchmarkIncomeRatio);
        result.getBenchmarkLine().setIncomeRatioDesc(IncomeRatioCalculator.formatCalIncomeRatio(benchmarkIncomeRatio));

        // 折线图
        for (HisCombpriceExtDTO hisCombpriceExtDTO : sortedHisCombpriceList) {
            // 组合收益率
            IncomeRatioChart.IncomeRatio eachIncomeRatio = new IncomeRatioChart.IncomeRatio();
            eachIncomeRatio.setDate(hisCombpriceExtDTO.getCom_net_date());
            eachIncomeRatio.setFormatDate(DateUtil.intToFormatDate(eachIncomeRatio.getDate()));
            // 计算折线图中每个点的组合累计收益率（使用区间中第一个点的组合累计收益率跟当前点的组合累计收益率计算）
            eachIncomeRatio.setRatio(IncomeRatioCalculator.calcIncomeRatioByStrSumIncomeRatio(firstComSumIncomeRatio, hisCombpriceExtDTO.getCom_sum_income_ratio()));
            eachIncomeRatio.setSumNetValue(DataTypeUtil.strToBigDecimal(hisCombpriceExtDTO.getCom_sum_net_value()));
            eachIncomeRatio.setRatioDesc(IncomeRatioCalculator.formatCalIncomeRatio(eachIncomeRatio.getRatio()));
            eachIncomeRatio.setTodayIncomeRatio(DataTypeUtil.strToBigDecimal(hisCombpriceExtDTO.getCom_today_income_ratio()));
            eachIncomeRatio.setTodayIncomeRatioDesc(IncomeRatioCalculator.formatCalIncomeRatio(eachIncomeRatio.getTodayIncomeRatio()));
            result.getCombineLine().getIncomeRatios().add(eachIncomeRatio);
            // 基准收益率
            IncomeRatioChart.IncomeRatio eachBenchmarkIncomeRatio = new IncomeRatioChart.IncomeRatio();
            eachBenchmarkIncomeRatio.setDate(hisCombpriceExtDTO.getCom_net_date());
            eachBenchmarkIncomeRatio.setFormatDate(DateUtil.intToFormatDate(eachBenchmarkIncomeRatio.getDate()));
            eachBenchmarkIncomeRatio.setSumNetValue(DataTypeUtil.strToBigDecimal(hisCombpriceExtDTO.getBenchmark_comb_sum_net_value()));
            // 计算折线图中每个点的组合基准累计收益率（使用区间中第一个点的组合基准累计收益率跟当前点的组合基准累计收益率计算）
            eachBenchmarkIncomeRatio.setRatio(IncomeRatioCalculator.calcIncomeRatioByStrSumIncomeRatio(firstComSumBenchmarkRatio, hisCombpriceExtDTO.getCom_sum_benchmark_ratio()));
            eachBenchmarkIncomeRatio.setRatioDesc(IncomeRatioCalculator.formatCalIncomeRatio(eachBenchmarkIncomeRatio.getRatio()));
            result.getBenchmarkLine().getIncomeRatios().add(eachBenchmarkIncomeRatio);
        }
        return result;
    }

    @Override
    public List<CombineInfo> getCombinfoItemByKyc(SimpleAccount simpleAccount, String orderDirection, String investOrganNo, String enTagType) {
        QueryCombInfoByKycReq queryCombInfoByKycReq = new QueryCombInfoByKycReq();
        queryCombInfoByKycReq.setEnTagType(enTagType);
        queryCombInfoByKycReq.setInvestOrganNo(investOrganNo);
        queryCombInfoByKycReq.setOrderDirection(orderDirection);
        BaseResult<QueryCombInfoByKycResp> baseResult = combineService.getCombinfoItemByKyc(simpleAccount, queryCombInfoByKycReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMBINFOS_BY_KYC_ERROR + "getCombinfoItemByKyc failed, clientId = {}, investOrganNo = {}, enTagType = {}", simpleAccount.getClientId(), investOrganNo, enTagType);
            throw new BusinessException(ErrorCodeEnum.QUERY_COMBINFOS_BY_KYC_ERROR);
        }
        if (baseResult.getData() == null || CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.info("getCombinfoItemByKyc failed, combineInfo list is empty. clientId = {}, investOrganNo = {}, enTagType = {}", simpleAccount.getClientId(), investOrganNo, enTagType);
            return new ArrayList<CombineInfo>();
        }
        List<CombInfoDTO> combInfoDTOS = baseResult.getData().getRows();
        List<CombineInfo> combineInfos = new ArrayList<>(combInfoDTOS.size());
        ObjectCopyUtil.copyCombInfoItems(combInfoDTOS, combineInfos);
        combineInfos = combineInfos.stream().filter(combineInfo -> filterTarget(combineInfo.getCombineCode(), simpleAccount)).collect(Collectors.toList());
        return combineInfos;
    }

    private boolean filterTarget(String combineCode, SimpleAccount simpleAccount) {
        CombineInfo combineInfo = getCombInfo(simpleAccount, combineCode);
        return !combineInfo.isTargetComb();
    }

    @Override
    @Cacheable(cacheNames = COMB_POS, key = "#combineCode", unless = "#result == null")
    public CombinePosition getCombinePosition(SimpleAccount simpleAccount, String combineCode) {
        QueryCombPositionReq queryCombPositionReq = new QueryCombPositionReq();
        queryCombPositionReq.setCombineCode(combineCode);
        BaseResult<QueryCombPositionResp> baseResult = combineService.queryCombPosition(simpleAccount, queryCombPositionReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMB_POS_ERROR + "queryCombPosition t2 failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_POS_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COMB_POS_ERROR + "queryCombPosition failed data is null");
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_POS_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COMB_POS_ERROR + "queryCombPosition failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_POS_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.warn("queryCombPosition rows empty");
            return new CombinePosition();
        }
        List<CombPositionDTO> combPositions = baseResult.getData().getRows();
        // 更新时间取上一个交易日
        int updateDate = DateUtil.dateToInt(getLastTradeDate());
        // Integer updateDate = combPositions.stream().collect(Collectors.maxBy(Comparator.comparing(CombPositionDTO::getInit_date))).get().getInit_date();
        List<CombinePosition.PositionTypeRatio> positionTypeRatios = aggTypeRatios(combPositions);
        List<CombinePosition.PositionProdRatio> positionProdRatio = getPositionProdRatio(combPositions);
        return new CombinePosition(updateDate, positionTypeRatios, positionProdRatio);
    }

    /**
     * 聚合计算持仓类型分布
     *
     * @param combPositions
     * @return
     */
    private List<CombinePosition.PositionTypeRatio> aggTypeRatios(List<CombPositionDTO> combPositions) {
        // 根据组合成分类别进行聚合分组
        Map<String, List<CombPositionDTO>> investTypeGroups = combPositions.stream().collect(Collectors.groupingBy(x ->
                getProdType1NameFromTagJson(x.getProd_tag_json())
        ));
        List<CombinePosition.PositionTypeRatio> result = new ArrayList<>();
        for (Map.Entry<String, List<CombPositionDTO>> entry : investTypeGroups.entrySet()) {
            String prodInvestType = entry.getKey();
            BigDecimal ratio = BigDecimal.ZERO;
            for (CombPositionDTO combPositionDTO : entry.getValue()) {
                ratio = ratio.add(DataTypeUtil.strToBigDecimalDefaultZero(combPositionDTO.getProduct_ratio()));
            }
            // 此处即处理为4位小数，否则后面assert100Percent不准
            ratio = ratio.setScale(4, RoundingMode.HALF_UP);
            // 为了减少代码修改及前端兼容性，此处仍然使用prodInvestType作为成分类别
            result.add(new CombinePosition.PositionTypeRatio(prodInvestType, prodInvestType, ratio, RatioUtil.formatPercent(ratio)));
        }
        // 最多显示5条，超过5条时，展示前4个，剩余的归到“其他”中
        result = postHandle(result);
        return assert100Percent(result);
    }

    /**
     * 从prod_tag_json中解析成分类型
     *
     * @param strProdTagJson
     * @return
     */
    private String getProdType1NameFromTagJson(String strProdTagJson) {
        if (StringUtils.isBlank(strProdTagJson)) {
            return ProdInvestTypeEnum.DEFAULT.getDesc();
        }
        JSONObject prodTagJson = JSONObject.parseObject(strProdTagJson);
        if (!prodTagJson.containsKey(PROD_TYPE_NAME_KEY)) {
            return ProdInvestTypeEnum.DEFAULT.getDesc();
        }
        String prodType = prodTagJson.getString(PROD_TYPE_NAME_KEY);
        if (StringUtils.isBlank(prodType)) {
            return ProdInvestTypeEnum.DEFAULT.getDesc();
        }
        return prodType;
    }

    /**
     * 最多显示5条，超过5条时，展示前4个，剩余的归到“其他”中，排序返回
     *
     * @param typeRatios
     * @return
     */
    private List<CombinePosition.PositionTypeRatio> postHandle(List<CombinePosition.PositionTypeRatio> typeRatios) {
        // 先按照持仓占比倒序排
        typeRatios = typeRatios.stream()
                .sorted(Comparator.comparing(CombinePosition.PositionTypeRatio::getRatio).reversed())
                .collect(Collectors.toList());
        if (typeRatios.size() <= MAX_POSITION_TYPE_NUM) {
            return typeRatios;
        }
        // 其他
        CombinePosition.PositionTypeRatio otherPosType = new CombinePosition.PositionTypeRatio(ProdInvestTypeEnum.DEFAULT.getDesc(), ProdInvestTypeEnum.DEFAULT.getDesc(),
                BigDecimal.ZERO, RatioUtil.formatPercent(BigDecimal.ZERO));
        List<CombinePosition.PositionTypeRatio> result = new ArrayList<>();
        // 是否需要添加到其他
        boolean addToOther = false;
        for (CombinePosition.PositionTypeRatio typeRatio : typeRatios) {
            if (addToOther) {
                // 将该类型添加到"其他"
                otherPosType.setRatio(otherPosType.getRatio().add(typeRatio.getRatio()));
                continue;
            }
            if (!ProdInvestTypeEnum.DEFAULT.getDesc().equals(typeRatio.getProdInvestType())) {
                result.add(typeRatio);
            } else {
                otherPosType = typeRatio;
            }
            if (result.size() >= MAX_POSITION_TYPE_NUM - 1) {
                addToOther = true;
            }
        }
        // 百分比格式赋值
        otherPosType.setRatioDesc(RatioUtil.formatPercent(otherPosType.getRatio()));
        result.add(otherPosType);
        return result;
    }

    /**
     * 确保百分比之和为100%，最后一个的百分比是1-其他
     *
     * @param positionTypeRatios
     * @return
     */
    private List<CombinePosition.PositionTypeRatio> assert100Percent(List<CombinePosition.PositionTypeRatio> positionTypeRatios) {
        if (CollectionUtils.isEmpty(positionTypeRatios) || positionTypeRatios.size() < 2) {
            return positionTypeRatios;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < positionTypeRatios.size() - 1; i++) {
            sum = sum.add(positionTypeRatios.get(i).getRatio());
        }
        BigDecimal lastRatio = BigDecimal.ONE.subtract(sum);
        CombinePosition.PositionTypeRatio last = positionTypeRatios.get(positionTypeRatios.size() - 1);
        last.setRatio(lastRatio);
        last.setRatioDesc(RatioUtil.formatPercent(lastRatio));
        return positionTypeRatios;
    }

    /**
     * 持仓明细，按照持仓比例倒序
     *
     * @param combPositions
     * @return
     */
    private List<CombinePosition.PositionProdRatio> getPositionProdRatio(List<CombPositionDTO> combPositions) {
        List<CombinePosition.PositionProdRatio> result = new ArrayList<>();
        for (CombPositionDTO combPosition : combPositions) {
            CombinePosition.PositionProdRatio positionProdRatio = new CombinePosition.PositionProdRatio();
            BeanCopyUtil.copyUnderlineProperties(combPosition, positionProdRatio);
            positionProdRatio.setProductRatio(positionProdRatio.getProductRatio().setScale(4, RoundingMode.HALF_UP));
            positionProdRatio.setProductRatioDesc(RatioUtil.formatPercent(positionProdRatio.getProductRatio(), 2));
            String prodType1Name = getProdType1NameFromTagJson(combPosition.getProd_tag_json());
            // 为了代码兼容及一致性，仍然使用这两个字段
            positionProdRatio.setProdInvestType(prodType1Name);
            positionProdRatio.setProdInvestTypeName(prodType1Name);
            result.add(positionProdRatio);
        }
        return result.stream()
                .sorted(Comparator.comparing(CombinePosition.PositionProdRatio::getProductRatio).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public TradeRule getTradeRule(SimpleAccount simpleAccount, String combineCode) {
        CombineInfo combInfo = getCombInfo(simpleAccount, combineCode);
        TradeRuleStrategy tradeRuleStrategy = StrategyFactory.getTradeRuleStrategy(CombProfitTypeEnum.parseByTypeId(combInfo.getCombProfitType()));
        if (tradeRuleStrategy == null) {
            log.error(COMB_PROFIT_TYPE_MISS_ERROR + "[query trade rule] clientId {} query {} trade rule error {}", simpleAccount.getClientId(), combineCode);
            throw new BusinessException(COMB_PROFIT_TYPE_MISS_ERROR);

        }
        return tradeRuleStrategy.assembleTradeRule(simpleAccount, combInfo);
    }

    /**
     * 组合服务费参数查询
     *
     * @param simpleAccount
     * @param combChargeNo
     * @return
     */
    private QueryCombFareArgResp queryCombFareArg(SimpleAccount simpleAccount, Integer combChargeNo) {
        QueryCombFareArgReq queryCombFareArgReq = new QueryCombFareArgReq();
        queryCombFareArgReq.setCombChargeNo(String.valueOf(combChargeNo));
        BaseResult<QueryCombFareArgResp> baseResult = combineService.queryCombFareArg(simpleAccount, queryCombFareArgReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg data is null");
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg combFareArgs is empty");
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 获取文案内容
     *
     * @param type2Copywriting
     * @param copywritingTypeEnum
     * @return
     */
    private String getCopywritingContent(Map<String, CopywritingDTO> type2Copywriting, CopywritingTypeEnum copywritingTypeEnum) {
        if (!type2Copywriting.containsKey(copywritingTypeEnum.getCode())) {
            return StringUtils.EMPTY;
        }
        return type2Copywriting.get(copywritingTypeEnum.getCode()).getCopywriting_desc();
    }

    /**
     * 费用说明
     *
     * @param type2Copywriting
     * @return
     */
    private String getFeeDesc(Map<String, CopywritingDTO> type2Copywriting) {
        StringBuilder sb = new StringBuilder();
        // todo: 联调样式
        sb.append(getCopywritingContent(type2Copywriting, CopywritingTypeEnum.PURCHASE_FEE_DESC))
                .append("\n");
        sb.append(getCopywritingContent(type2Copywriting, CopywritingTypeEnum.REDEEM_FEE_DESC));
        return sb.toString();
    }
}
