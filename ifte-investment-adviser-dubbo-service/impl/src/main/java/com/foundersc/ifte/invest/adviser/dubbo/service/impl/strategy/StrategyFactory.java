package com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class StrategyFactory {
    private static ApplicationContext context;

    public static TradeRuleStrategy getTradeRuleStrategy(CombProfitTypeEnum profitType) {
        switch (profitType) {
            case COMMON_COMB:
                return context.getBean("normalTradeRuleStrategy", NormalTradeRuleStrategy.class);
            case TARGET_COMB:
                return context.getBean("targetTradeRuleStrategy", TargetTradeRuleStrategy.class);
            default:
                return null;
        }
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        StrategyFactory.context = context;
    }

}
