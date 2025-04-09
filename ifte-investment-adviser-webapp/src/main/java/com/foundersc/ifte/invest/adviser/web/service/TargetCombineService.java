package com.foundersc.ifte.invest.adviser.web.service;

import com.foundersc.ifte.invest.adviser.web.model.combine.CombineStrategyVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.TargetKycInfo;

import java.util.List;

public interface TargetCombineService {

    List<CombineStrategyVO> getOnSaleCombineList(int initDate);

    List<CombineStrategyVO> getHisCombineList(int initDate);

    TargetKycInfo queryTargetKycInfo(String combineCode);

    boolean commitTargetKyc(String combineCode);
}
