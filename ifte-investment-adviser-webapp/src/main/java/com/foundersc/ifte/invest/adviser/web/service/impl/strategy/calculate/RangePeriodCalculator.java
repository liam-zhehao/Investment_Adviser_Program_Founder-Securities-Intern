package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.calculate;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.web.enums.CalculatorTypeEnum;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.calculate.PeriodCalculator;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import org.springframework.stereotype.Component;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.DAY;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.PERIOD_CONN;

/**
 * 运作周期范围计算方式
 */
@Component("rangePeriodCalculator")
public class RangePeriodCalculator implements PeriodCalculator {

    private static final int DEFAULT_DAY = 1;

    @Override
    public String calculatePeriod(CombineInfo combineInfo) {
        Integer minPeriod = DateUtil.diffDateBetween(combineInfo.getOperationalStartDate(), combineInfo.getStopProfitDate()) + DEFAULT_DAY;
        Integer maxPeriod = DateUtil.diffDateBetween(combineInfo.getOperationalStartDate(), combineInfo.getCombEndDate()) + DEFAULT_DAY;
        return minPeriod.toString().concat(PERIOD_CONN).concat(maxPeriod.toString()).concat(DAY);
    }

    @Override
    public String getKey() {
        return CalculatorTypeEnum.RANGE_CAL.getTypeId();
    }
}
