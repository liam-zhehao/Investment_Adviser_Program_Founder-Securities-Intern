package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble;

import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCopywritingExtResp;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.web.constants.TipConstants;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.StrategyTraitVO;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.calculate.PeriodCalculator;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.MathUtil;
import com.foundersc.ifte.invest.adviser.web.util.TargetDateUtil;
import com.foundersc.itc.product.service.TradeCalendarRemoteService;
import com.foundersc.itc.product.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.DECIMAL_PRECISION;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.ONE_HUNDRED;
import static com.foundersc.ifte.invest.adviser.web.util.DateUtil.formatDate;

@Component("targetCombAssemble")
@Slf4j
public class TargetCombAssemble extends CombAssemble {

    @Autowired
    private List<PeriodCalculator> periodCalculators;

    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    @Autowired
    private TradeCalendarRemoteService tradeCalendarRemoteService;

    private Map<String, PeriodCalculator> periodCalculatorMap;

    public static final String DISPLAY_DESC = "目标止盈年化收益率";


    @PostConstruct
    public void init() {
        periodCalculatorMap = periodCalculators.stream().collect(Collectors.toMap(PeriodCalculator::getKey, Function.identity()));
    }

    @Override
    protected void assembleBaseInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 目标年化收益率
        resultVO.setDisplayValue(transferProfitRatio(combInfo.getTargetProfitRatio()));
        resultVO.setDisplayDesc(DISPLAY_DESC);
        resultVO.setCombineName(combInfo.getTargetCombFullName());
        // 处理状态
        resultVO.setDealStatus(combInfo.getDealStatus());
        resultVO.setDealStatusDesc(CombDealStatusEnum.parseByStatusCode(combInfo.getDealStatus()).getDisplay());
        // 运作周期
        PeriodCalculator calculator = periodCalculatorMap.get(CombDealStatusEnum.parseByStatusCode(combInfo.getDealStatus()).getCalculatorKey());
        resultVO.setOperationPeriod(calculator.calculatePeriod(combInfo));
        resultVO.setOpeTags(null);
    }

    @Override
    protected void assembleMarketInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 策略特点
        QueryCopywritingExtResp resp = t2ServiceAdapter.queryStrategyTrait(ContextHolder.getSimpleAccount(), combInfo.getCombineCode());
        if (resp == null || CollectionUtils.isEmpty((resp.getRows()))) {
            return;
        }
        StrategyTraitVO strategyTraitVO = new StrategyTraitVO();
        strategyTraitVO.setTitle(resp.getRows().get(0).getCopywriting_title());
        strategyTraitVO.setDetail(resp.getRows().get(0).getCopywriting_desc());
        resultVO.setStrategyTrait(strategyTraitVO);
    }

    @Override
    protected void assembleTradeInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 运作流程
        transferOperationDate(resultVO, combInfo);
    }

    @Override
    protected void assembleStatementInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        resultVO.getStatements().add(TipConstants.TARGET_LEGAL_STATEMENT1);
        resultVO.getStatements().add(TipConstants.TARGET_LEGAL_STATEMENT2);
        resultVO.getStatements().add(String.format(TipConstants.STATEMENT_FROM, resultVO.getInvestOrganName()));
    }

    @Override
    public String getCombType() {
        return CombProfitTypeEnum.TARGET_COMB.getTypeId();
    }

    private String transferProfitRatio(String profitRatio) {
        return MathUtil.formatAmount(new BigDecimal(profitRatio).multiply(ONE_HUNDRED).doubleValue(), DECIMAL_PRECISION);
    }

    private void transferOperationDate(CombineInfoVO resultVO, CombineInfo combInfo) {
        resultVO.setCurrentDate(DateUtils.formatDate(DateUtils.getCurrentDateOfZero(), DateUtils.YYYY_MM_DD));
        resultVO.setBuyStartDate(formatDate(combInfo.getBuyStartDate()));
        resultVO.setBuyEndDate(formatDate(combInfo.getBuyEndDate()));
        resultVO.setOperationalStartDate(formatDate(combInfo.getOperationalStartDate()));
        resultVO.setStopProfitDate(formatDate(combInfo.getStopProfitDate()));
        resultVO.setStopProfitBeginDate(formatDate(getStopProfitBeginDate(combInfo)));
        resultVO.setProfitValidDate(formatDate(combInfo.getProfitValidDate()));
        resultVO.setCombEndDate(formatDate(combInfo.getCombEndDate()));
        if (combInfo.getBuyStartTime() != null) {
            String startTime = TargetDateUtil.formatTime(combInfo.getBuyStartTime().toString());
            resultVO.setBuyStartTime(combInfo.getBuyStartDate().toString().concat(startTime));
        }
        if (combInfo.getBuyEndTime() != null) {
            String endTime = TargetDateUtil.formatTime(combInfo.getBuyEndTime().toString());
            resultVO.setBuyEndTime(combInfo.getBuyEndDate().toString().concat(endTime));
        }
    }

    private Integer getStopProfitBeginDate(CombineInfo combInfo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.getDate(combInfo.getStopProfitDate()));
        calendar.add(Calendar.DATE, 1);
        return DateUtils.getIntegerDate(calendar.getTime());
    }
}
