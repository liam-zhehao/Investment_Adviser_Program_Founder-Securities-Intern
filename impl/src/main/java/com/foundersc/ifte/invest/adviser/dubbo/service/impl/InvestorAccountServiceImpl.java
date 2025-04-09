package com.foundersc.ifte.invest.adviser.dubbo.service.impl;

import com.foundersc.ifc.portfolio.t2.response.invest.InvestorAccountDTO;
import com.foundersc.ifc.portfolio.t2.response.invest.InvestorAccountResp;
import com.foundersc.ifc.portfolio.t2.service.InvestAccountService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.entity.InvestorAccount;
import com.foundersc.ifte.invest.adviser.dubbo.service.InvestorAccountService;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_INVESTOR_ACCOUNT_ERROR;

@Service("investorAccountService")
@Slf4j
public class InvestorAccountServiceImpl implements InvestorAccountService {

    @Autowired
    private InvestAccountService investAccountService;

    @Override
    public List<InvestorAccount> queryClientInvestorAccount(SimpleAccount simpleAccount) {
        try {
            BaseResult<InvestorAccountResp> invAccResult = investAccountService.queryInvestorAccount(simpleAccount);
            if (!invAccResult.isSuccess()) {
                log.warn(QUERY_INVESTOR_ACCOUNT_ERROR + "[query investorAccount] client {} query InvestorAccount error {}", simpleAccount.getClientId(), invAccResult.getErrorMsg());
                return null;
            }

            if (invAccResult.getData() == null || CollectionUtils.isEmpty(invAccResult.getData().getRows())) {
                // log.info("[query investorAccount] client {} does not have investor account", simpleAccount.getClientId());
                return null;
            }
            List<InvestorAccountDTO> investorAccountDTOs = invAccResult.getData().getRows();
            List<InvestorAccount> investorAccounts = new ArrayList<>(investorAccountDTOs.size());
            ObjectCopyUtil.copyInvestorAccount(investorAccountDTOs, investorAccounts);
            // log.info("[query investorAccount] client {} query investor account success size {}", simpleAccount.getClientId(), investorAccounts.size());
            return investorAccounts;
        } catch (Exception e) {
            log.error("client {} queryClientInvestorAccount error:", simpleAccount.getClientId(), e);
            return null;
        }
    }
}
