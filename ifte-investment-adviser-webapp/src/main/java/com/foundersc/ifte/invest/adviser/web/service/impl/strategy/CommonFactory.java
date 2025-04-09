package com.foundersc.ifte.invest.adviser.web.service.impl.strategy;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble.CombAssemble;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble.NormalCombAssemble;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble.TargetCombAssemble;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase.NormalPurchaseStrategy;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase.PurchaseStrategy;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase.TargetPurchaseStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class CommonFactory {
    private static ApplicationContext context;

    public static CombAssemble getAssembleTemplate(CombProfitTypeEnum profitType) {
        switch (profitType) {
            case COMMON_COMB:
                return context.getBean("normalCombAssemble", NormalCombAssemble.class);
            case TARGET_COMB:
                return context.getBean("targetCombAssemble", TargetCombAssemble.class);
            default:
                return null;
        }
    }

    public static PurchaseStrategy getPurchaseTemplate(CombProfitTypeEnum profitType) {
        switch (profitType) {
            case COMMON_COMB:
                return context.getBean("normalPurchaseStrategy", NormalPurchaseStrategy.class);
            case TARGET_COMB:
                return context.getBean("targetPurchaseStrategy", TargetPurchaseStrategy.class);
            default:
                return null;
        }
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        CommonFactory.context = context;
    }

}
