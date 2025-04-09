package com.foundersc.ifte.invest.adviser.web.service;

import com.foundersc.ifte.invest.adviser.web.model.trade.ClientTargetCombModeVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.FlagAndTipVO;

/**
 * 目标盈续期方式
 *
 * @author wangfuwei
 * @date 2023/8/29
 */
public interface TargetCombineModeService {
    /**
     * 获取指定目标盈组合的续期方式
     *
     * @param combineCode
     * @return
     */
    ClientTargetCombModeVO queryModeInfo(String combineCode);

    ClientTargetCombModeVO queryModeInfoByInvestOrganNo(String combineCode, String investOrganNo);


    /**
     * 修改续期方式
     *
     * @param combineMode
     * @param newMode
     * @return
     */
    FlagAndTipVO modifyMode(String combineMode, String newMode);
}
