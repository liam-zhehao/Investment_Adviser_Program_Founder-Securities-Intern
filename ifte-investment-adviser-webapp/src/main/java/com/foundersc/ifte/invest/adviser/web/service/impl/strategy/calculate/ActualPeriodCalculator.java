package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.calculate;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.web.enums.CalculatorTypeEnum;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import com.foundersc.itc.product.utils.DateUtils;
import org.springframework.stereotype.Component;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.DAY;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.ZERO_INT;

/**
 * 实际运作天数计算方式
 */
@Component("actualPeriodCalculator")
public class ActualPeriodCalculator implements PeriodCalculator {
    @Override
    public String calculatePeriod(CombineInfo combineInfo) {
        Integer period = null;
        if (CombDealStatusEnum.TERMINATED.getCode().equals(combineInfo.getDealStatus())) {
            period = DateUtil.diffDateBetween(combineInfo.getOperationalStartDate(), combineInfo.getCombEndDate());
        } else if (combineInfo.getProfitValidDate() == null || ZERO_INT.equals(combineInfo.getProfitValidDate())) {
            period = DateUtil.diffDateBetween(combineInfo.getOperationalStartDate(), DateUtils.getCurrentIntegerDate());
        } else {
            period = DateUtil.diffDateBetween(combineInfo.getOperationalStartDate(), combineInfo.getProfitValidDate());
        }
        period++;
        return period.toString().concat(DAY);
    }

    @Override
    public String getKey() {
        return CalculatorTypeEnum.OPERATION_DAY_CAL.getTypeId();
    }
}
