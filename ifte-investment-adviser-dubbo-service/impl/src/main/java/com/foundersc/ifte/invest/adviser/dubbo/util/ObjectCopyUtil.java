package com.foundersc.ifte.invest.adviser.dubbo.util;

import com.foundersc.ifc.portfolio.t2.model.v2.comb.CombInfoDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.kyc.AnswerDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.kyc.QuestionDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.kyc.TestjourExtDTO;
import com.foundersc.ifc.portfolio.t2.response.aim.TargetCombInfoDTO;
import com.foundersc.ifc.portfolio.t2.response.invest.*;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.QueryAssetFetchExtResp;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.CombAgreement;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombStrategyInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.AnswerInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.QuestionInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.*;
import com.foundersc.ifte.invest.adviser.dubbo.entity.InvestorAccount;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ObjectCopyUtil {

    private ObjectCopyUtil() {

    }

    /**
     * 组合协议转换
     *
     * @param combAgreementDTOs
     * @param combAgreements
     */
    public static void copyCombAgreement(List<CombAgreementDTO> combAgreementDTOs, List<CombAgreement> combAgreements) {
        combAgreementDTOs.stream().forEach(agreement -> {
            CombAgreement combAgreement = new CombAgreement();
            combAgreement.setAgreementStatus(agreement.getAgreement_status());
            combAgreement.setClientId(agreement.getClient_id());
            combAgreement.setCombineCode(agreement.getCombine_code());
            combAgreement.setSignDate(agreement.getSign_date());
            combAgreements.add(combAgreement);
        });
    }

    /**
     * 总资产转换
     *
     * @param totalAssetResp
     * @param clientTotalAsset
     */
    public static void copyTotalAsset(TotalAssetResp totalAssetResp, ClientTotalAsset clientTotalAsset) {
        clientTotalAsset.setCombAsset(totalAssetResp.getCombi_asset());
        clientTotalAsset.setCombTodayIncome(totalAssetResp.getCombi_today_income());
        clientTotalAsset.setCombSumIncome(totalAssetResp.getCombi_sum_income());
        clientTotalAsset.setLastDayIncome(totalAssetResp.getLast_day_income());
        clientTotalAsset.setLastIncomeDate(totalAssetResp.getLast_income_date());
        clientTotalAsset.setClientSumIncome(totalAssetResp.getClient_sum_income());
    }

    /**
     * 组合资产转换
     *
     * @param combineAssetDTOs
     * @param clientCombAssets
     */
    public static void copyCombAsset(List<CombineAssetDTO> combineAssetDTOs, List<ClientCombAsset> clientCombAssets) {
        combineAssetDTOs.stream().forEach(asset -> {
            ClientCombAsset clientCombAsset = new ClientCombAsset();
            clientCombAsset.setCombineName(asset.getCombine_name());
            clientCombAsset.setCombMarketVal(asset.getCombi_market());
            clientCombAsset.setCombineAsset(asset.getCombi_asset());
            clientCombAsset.setCombSumIncome(asset.getCombi_sum_income());
            clientCombAsset.setLastDayIncome(asset.getLast_day_income());
            clientCombAsset.setLastIncomeDate(asset.getLast_income_date());
            clientCombAsset.setCombineCode(asset.getCombine_code());
            clientCombAsset.setAssetUnit(asset.getAsset_unit());
            clientCombAsset.setInvestorAccount(asset.getInvestor_account());
            clientCombAsset.setCombHoldIncomeRatio(asset.getHold_comb_income_ratio());
            clientCombAsset.setIncomePredictFlag(asset.getIncome_predict_flag());
            clientCombAsset.setIncomePredictDate(asset.getIncome_predict_date());
            clientCombAsset.setCombHoldYearIncomeRatio(asset.getComb_hold_year_income_ratio());
            clientCombAssets.add(clientCombAsset);
        });
    }

    /**
     * 投顾账户转换
     *
     * @param investorAccountDTOs
     * @param investorAccounts
     */
    public static void copyInvestorAccount(List<InvestorAccountDTO> investorAccountDTOs, List<InvestorAccount> investorAccounts) {
        investorAccountDTOs.stream().forEach(invAcc -> {
            InvestorAccount investorAccount = new InvestorAccount();
            investorAccount.setAccountName(invAcc.getInvestor_account());
            investorAccount.setClientId(invAcc.getClient_id());
            investorAccount.setInvestorAccount(invAcc.getInvestor_account());
            investorAccount.setInvestorAccountType(invAcc.getInvestor_account_type());
            investorAccount.setInvestorAccountStatus(invAcc.getInvestor_account_status());
            investorAccount.setOpenDate(invAcc.getOpen_date());
            investorAccount.setEndDate(invAcc.getEnd_date());
            investorAccount.setOpenTime(invAcc.getOpen_time());
            investorAccount.setEndTime(invAcc.getEnd_time());
            investorAccounts.add(investorAccount);
        });
    }

    /**
     * 组合成分信息转换
     *
     * @param combShareDTOs
     * @param shareItems
     */
    public static void copyCombShareItems(List<CombShareDTO> combShareDTOs, List<CombShareItem> shareItems) {
        combShareDTOs.stream().forEach(combShare -> {
            CombShareItem combShareItem = new CombShareItem();
            combShareItem.setFundCode(combShare.getProd_code());
            combShareItem.setFundName(combShare.getProd_name());
            if (StringUtils.isEmpty(combShare.getProd_market_value())) {
                combShareItem.setProdMarketValue(0.0);
                combShareItem.setProdMarketValueFormat("0.00");
            } else {
                double marketValue = new BigDecimal(combShare.getProd_market_value()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                combShareItem.setProdMarketValue(marketValue);
                combShareItem.setProdMarketValueFormat(MathUtil.formatAmount(marketValue, 2));
            }
            shareItems.add(combShareItem);
        });
    }

    /**
     * 问卷成分信息转换
     */
    public static void copyKycPaper(List<QuestionDTO> questionDTOS, List<QuestionInfo> questionInfos) {
        questionDTOS.stream().forEach(questionDTO -> {
            QuestionInfo questionInfo = new QuestionInfo();
            questionInfo.setQuestionNo(questionDTO.getQuestion_no());
            questionInfo.setQuestionContent(questionDTO.getQuestion_content());
            questionInfo.setEnabledFlag(String.valueOf(questionDTO.getEnabled_flag()));
            questionInfo.setQuestionKind(String.valueOf(questionDTO.getQuestion_kind()));
            questionInfo.setRemark(questionDTO.getRemark());
            questionInfo.setTradeIndexType(String.valueOf(questionDTO.getTradeindex_type()));
            List<AnswerInfo> answerInfoList = new ArrayList<>();
            List<AnswerDTO> answerDTOS = questionDTO.getAnswer_list();
            answerDTOS.stream().forEach(answerDTO -> {
                AnswerInfo answerInfo = new AnswerInfo();
                answerInfo.setAnswerNo(answerDTO.getAnswer_no());
                answerInfo.setAnswerContent(answerDTO.getAnswer_content());
                answerInfo.setEnabledFlag(String.valueOf(answerDTO.getEnabled_flag()));
                answerInfo.setRemark(answerDTO.getRemark());
                answerInfoList.add(answerInfo);
            });
            questionInfo.setAnswerList(answerInfoList);
            questionInfos.add(questionInfo);
        });
    }

    /**
     * @param combineEntrustDTO
     * @param combineEntrust
     */
    public static void copyCombineEntrust(CombineEntrustDTO combineEntrustDTO, CombineEntrust combineEntrust) {
        combineEntrust.setInitDate(combineEntrustDTO.getInit_date());
        combineEntrust.setCurrDate(combineEntrustDTO.getCurr_date());
        combineEntrust.setCurrTime(combineEntrustDTO.getCurr_time());
        combineEntrust.setAllRedeemFlag(combineEntrustDTO.getAll_redeem_flag());
        combineEntrust.setAppUnConfirmBalance(combineEntrustDTO.getApp_unconfirm_balance());
        combineEntrust.setCombineCode(combineEntrustDTO.getCombine_code());
        combineEntrust.setCombineName(combineEntrustDTO.getCombine_name());
        combineEntrust.setCombBusinessType(combineEntrustDTO.getComb_busin_type());
        combineEntrust.setCombRequestStatus(combineEntrustDTO.getComb_request_status());
        combineEntrust.setEntrustBalance(combineEntrustDTO.getEntrust_balance());
        combineEntrust.setRedeemRatio(combineEntrustDTO.getRedeem_ratio());
        combineEntrust.setCombRequestNo(combineEntrustDTO.getComb_request_no());
        combineEntrust.setOrigCombRequestNo(combineEntrustDTO.getOrig_comb_request_no());
        combineEntrust.setPreIncomeDate(combineEntrustDTO.getPre_income_date());
        combineEntrust.setPreArriveDate(combineEntrustDTO.getPre_arrive_date());
        combineEntrust.setRedeemArriveBalance(combineEntrustDTO.getRedeem_arrive_balance());
        combineEntrust.setAffirmDate(combineEntrustDTO.getAffirm_date());
        combineEntrust.setPreRedeemBalance(combineEntrustDTO.getPre_redeem_balance());
        combineEntrust.setPreAffirmDate(combineEntrustDTO.getPre_affirm_date());
        combineEntrust.setArriveDate(combineEntrustDTO.getArrive_date());
        combineEntrust.setTriggerDate(combineEntrustDTO.getTrigger_date());
        combineEntrust.setRemark(combineEntrustDTO.getRemark());
        combineEntrust.setFundTransStatus(combineEntrustDTO.getFund_trans_status());
        combineEntrust.setSourceId(combineEntrustDTO.getSource_id());
        combineEntrust.setInvestorAccount(combineEntrustDTO.getInvestor_account());
        combineEntrust.setCancelFlag(combineEntrustDTO.getCancel_flag());
        combineEntrust.setExecuteFlag(combineEntrustDTO.getExecute_flag());
        combineEntrust.setConfirmBalance(combineEntrustDTO.getConfirm_balance());
    }


    public static void copyCombineEntrusts(List<CombineEntrustDTO> combineEntrustDTOs, List<CombineEntrust> combineEntrusts) {
        combineEntrustDTOs.stream().forEach(entrustDTO -> {
            CombineEntrust combineEntrust = new CombineEntrust();
            copyCombineEntrust(entrustDTO, combineEntrust);
            combineEntrusts.add(combineEntrust);
        });
    }

    public static void copyTestjourInfos(List<TestjourExtDTO> testjourExtDTOS, List<TestjourInfo> testjourInfos) {
        testjourExtDTOS.stream().forEach(testjourExtDTO -> {
            TestjourInfo testjourInfo = new TestjourInfo();
            BeanCopyUtil.copyUnderlineProperties(testjourExtDTO, testjourInfo);
            testjourInfos.add(testjourInfo);
        });
    }

    public static void copyCombStrategyInfo(List<TargetCombInfoDTO> combInfoDTOs, List<CombStrategyInfo> strategyInfos) {
        combInfoDTOs.stream().forEach(combInfoDTO -> {
            CombStrategyInfo combStrategyInfo = new CombStrategyInfo();
            BeanCopyUtil.copyUnderlineProperties(combInfoDTO, combStrategyInfo);
            strategyInfos.add(combStrategyInfo);
        });
    }

    public static void copyCombInfoItems(List<CombInfoDTO> combInfoDTOS, List<CombineInfo> combineInfos) {
        combInfoDTOS.stream().forEach(combInfoDTO -> {
            CombineInfo combineInfo = new CombineInfo();
            BeanCopyUtil.copyUnderlineProperties(combInfoDTO, combineInfo);
            combineInfos.add(combineInfo);
        });
    }

    /**
     * 组合历史资产转换
     *
     * @param combHisAssetDTOs
     * @param combDailyIncomes
     */
    public static void copyCombHisAsset(List<CombHisAssetDTO> combHisAssetDTOs, List<CombDailyIncome> combDailyIncomes) {
        combHisAssetDTOs.stream().forEach(combHisAsset -> {
            CombDailyIncome combDailyIncome = new CombDailyIncome();
            combDailyIncome.setIncomeAmount(combHisAsset.getCombi_today_income());
            combDailyIncome.setIncomeRatio(combHisAsset.getLast_day_income_ratio());
            if (!StringUtils.isEmpty(combHisAsset.getCombi_income_date())) {
                combDailyIncome.setDate(Integer.valueOf(combHisAsset.getCombi_income_date()));
            }
            combDailyIncomes.add(combDailyIncome);
        });
    }

    /**
     * 可取资产转换
     *
     * @param resp
     * @param combFetchAsset
     */
    public static void copyCombFetchAsset(QueryAssetFetchExtResp resp, CombFetchAsset combFetchAsset) {
        combFetchAsset.setClientId(resp.getClient_id());
        combFetchAsset.setCombineAsset(DataTypeUtil.strToBigDecimal(resp.getCombi_asset()));
        combFetchAsset.setCombineCode(resp.getCombine_code());
        combFetchAsset.setEnRedeemFlag(resp.getEn_redeem_flag());
        combFetchAsset.setFetchMarketValue(DataTypeUtil.strToBigDecimal(resp.getFetch_market_value()));
        combFetchAsset.setMaxFetchBalance(DataTypeUtil.strToBigDecimal(resp.getMax_fetch_balance()));
        combFetchAsset.setMinFetchBalance(DataTypeUtil.strToBigDecimal(resp.getMin_fetch_balance()));
        combFetchAsset.setMaxFetchRatio(DataTypeUtil.strToBigDecimal(resp.getMax_fetch_ratio()));
        combFetchAsset.setMinFetchRatio(DataTypeUtil.strToBigDecimal(resp.getMin_fetch_ratio()));
        combFetchAsset.setAllowAllRedeemFlag(resp.getAllow_all_redeem_flag());
        combFetchAsset.setAllRedeemRatio(DataTypeUtil.strToBigDecimal(resp.getAll_redeem_ratio()));
    }
}
