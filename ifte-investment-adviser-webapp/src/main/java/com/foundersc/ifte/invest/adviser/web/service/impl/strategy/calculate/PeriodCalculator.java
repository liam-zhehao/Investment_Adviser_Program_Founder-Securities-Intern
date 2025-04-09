package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.calculate;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;

public interface PeriodCalculator {
    String calculatePeriod(CombineInfo combineInfo);

    String getKey();
}
