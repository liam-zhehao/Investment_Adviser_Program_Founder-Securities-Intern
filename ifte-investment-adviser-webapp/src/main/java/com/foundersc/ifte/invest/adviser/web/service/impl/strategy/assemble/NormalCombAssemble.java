package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble;

import com.foundersc.ifc.common.util.DateUtils;
import com.foundersc.ifc.common.util.StringUtils;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombTrendRangeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.web.constants.TipConstants;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombTrendRangeVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.TradeCalendarRemoteServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.CombineTagUtil;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import com.foundersc.ifte.invest.adviser.web.util.RatioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.*;

/**
 *
 */
@Slf4j
@Component("normalCombAssemble")
public class NormalCombAssemble extends CombAssemble {


    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;


    @Value("${organ.daily-income-ratio}")
    private String dailyIncomeRatioOrgan;
    /**
     * 组合详情页业绩走势图displayPos参数为1
     */
    private static final int COMB_DETAIL_DISPLAY_POS = 1;

    /**
     * 客户持仓页业绩走势图displayPos参数为1
     */
    private static final int CLIENT_POS_DETAIL_DISPLAY_POS = 1;

    @Override
    protected void assembleBaseInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        populateCommonField(resultVO, combInfo);
        resultVO.setOpeTags(CombineTagUtil.getOpeTags(resultVO.getTagJson()));
    }

    @Override
    protected void assembleMarketInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 计算组合成立天数：用当前日期的上一个交易日-成立日期+1
        Date lastTradeDate = tradeCalendarRemoteServiceAdapter.queryTradeDate(new Date()).getLastTradeDate();
        int combSetupDays = DateUtils.getDateDiffDays(DateUtil.intToDate(combInfo.getSetUpDate()), lastTradeDate);
        // 趋势图要展示的区间
        List<CombTrendRangeEnum> enableRanges = CombTrendRangeEnum.getCombTrendEnableRanges(combSetupDays, COMB_DETAIL_DISPLAY_POS);
        List<CombTrendRangeVO> combTrendRanges = enableRanges.stream().map(x -> new CombTrendRangeVO(x)).collect(Collectors.toList());
        resultVO.setCombTrendRanges(combTrendRanges);
        List<CombTrendRangeEnum> defaultTrendRanges = CombTrendRangeEnum.getCombTrendRanges(CLIENT_POS_DETAIL_DISPLAY_POS);
        List<CombTrendRangeVO> defaultCombTrendRanges = defaultTrendRanges.stream().map(x -> new CombTrendRangeVO(x)).collect(Collectors.toList());
        resultVO.setDefaultCombTrendRanges(defaultCombTrendRanges);
    }

    @Override
    protected void assembleTradeInfo(CombineInfoVO resultVO, CombineInfo combInfo) {

    }

    @Override
    protected void assembleStatementInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        resultVO.getStatements().add(TipConstants.LEGAL_STATEMENTS);
        resultVO.getStatements().add(String.format(TipConstants.STATEMENT_FROM, resultVO.getInvestOrganName()));
    }

    private void populateCommonField(CombineInfoVO resultVO, CombineInfo combInfo) {
        if (dailyIncomeRatioOrgan.contains(combInfo.getInvestOrganNo())) {
            resultVO.setDisplayDesc(DAILY_INCOME_RATIO_DESC);
            if (StringUtils.isNotEmpty(combInfo.getTodayIncomeRatio())) {
                resultVO.setDisplayValue(RatioUtil.formatPercentWithPrefix(new BigDecimal(combInfo.getTodayIncomeRatio())));
            } else {
                resultVO.setDisplayValue(BLANK_STRING);
            }
        } else {
            resultVO.setDisplayDesc(YEAR_INCOME_RATIO);
            resultVO.setDisplayValue(resultVO.getComIncomeRatioDesc());
        }
    }

    @Override
    public String getCombType() {
        return CombProfitTypeEnum.COMMON_COMB.getTypeId();
    }
}
