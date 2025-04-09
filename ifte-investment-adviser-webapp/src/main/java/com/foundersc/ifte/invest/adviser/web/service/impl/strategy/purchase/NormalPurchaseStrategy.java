package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase;

import com.foundersc.ifc.portfolio.t2.response.v2.trade.AddInvestResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.SignAgreementResp;
import com.foundersc.ifte.invest.adviser.web.entity.CombRequestRecordEntity;
import com.foundersc.ifte.invest.adviser.web.enums.BoolEnum;
import com.foundersc.ifte.invest.adviser.web.enums.CombRequestEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseReq;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseResp;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseResultVO;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("normalPurchaseStrategy")
@Slf4j
public class NormalPurchaseStrategy extends PurchaseStrategy {


    @Override
    protected PurchaseResultVO doPurchase(PurchaseReq purchaseReq, String combineName, boolean hasSignedComb) {
        CombRequestRecordEntity combRequestRecord;
        PurchaseResp resp;
        if (!hasSignedComb) {
            log.info("hasSignedComb is false, signAgreement");
            SignAgreementResp signAgreementResp = t2ServiceAdapter.signAgreement(ContextHolder.getSimpleAccount(), purchaseReq.getCombineCode(), purchaseReq.getEntrustAmount(), null);
            resp = getPurchaseResp(signAgreementResp);
            combRequestRecord = build(purchaseReq, combineName, resp, CombRequestEnum.SIGN);
            // 记录数据库
            recordAsyncWriter.asyncSaveCombRequestRecord(combRequestRecord);
            if (!signAgreementResp.isSuccess()) {
                throw new BizException(BizErrorCodeEnum.SIGN_AGREEMENT_ERROR, signAgreementResp.getError_info());
            }
        } else {
            log.info("hasSignedComb is true, addInvest");
            AddInvestResp addInvestResp = t2ServiceAdapter.addInvest(ContextHolder.getSimpleAccount(), purchaseReq.getCombineCode(), purchaseReq.getEntrustAmount());
            resp = getPurchaseResp(addInvestResp);
            combRequestRecord = build(purchaseReq, combineName, resp, CombRequestEnum.ADD_INVEST);
            // 记录数据库
            recordAsyncWriter.asyncSaveCombRequestRecord(combRequestRecord);
            if (!addInvestResp.isSuccess()) {
                throw new BizException(BizErrorCodeEnum.ADD_INVEST_ERROR, addInvestResp.getError_info());
            }
        }

        return getPurchaseResultVO(purchaseReq, resp);
    }


    @Override
    protected void checkContinueMode(PurchaseReq purchaseReq) {
    }
}
