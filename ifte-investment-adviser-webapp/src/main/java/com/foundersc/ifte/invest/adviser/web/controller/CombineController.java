package com.foundersc.ifte.invest.adviser.web.controller;

import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombinePositionVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.IncomeRatioChartVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.TradeRuleVO;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 策略详情
 */
@RestController
@RequestMapping("/api/investAdviser/clientAuth/combine")
@Validated
@Api(tags = {"策略详情"})
@Slf4j
public class CombineController {

    @Autowired
    private CombInfoService combInfoService;

    @Autowired
    private TradeService tradeService;

    @Value("${url.agreement}")
    private String agreement;

    @Value("${url.epaper}")
    private String epaper;

    @GetMapping("/info")
    @ApiOperation("组合信息")
    public Response<CombineInfoVO> info(@ApiParam(value = "组合代码", required = true) @RequestParam("combineCode") String combineCode) {
        CombineInfoVO combineInfo = combInfoService.info(combineCode, DateUtil.intDate());
        combineInfo.setNeedSign(!tradeService.hasSignedComb(combineCode));
        if (combineInfo.isTargetComb()) {
            combineInfo.setCanBuy(tradeService.canTargetCombPurchase(combineInfo));
        }
        String epaperUrl = null;
        try {
            epaperUrl = URLEncoder.encode(String.format(epaper, combineCode), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("combineCode {} jump url encode error", combineCode);
        }
        combineInfo.setAgreementUrl(String.format(agreement, combineCode, combineInfo.getCombProfitType(), epaperUrl == null ? "" : epaperUrl));
        return Response.ok(combineInfo);
    }


    @GetMapping("/trend")
    @ApiOperation("组合行情走势图")
    public Response<IncomeRatioChartVO> trend(@ApiParam(value = "组合代码", required = true) @RequestParam("combineCode") String combineCode,
                                              @ApiParam(value = "时间区间", defaultValue = "1", allowableValues = "range[1, 5]") @RequestParam(required = false, defaultValue = "1") Integer rangeId) {
        return Response.ok(combInfoService.trend(combineCode, rangeId, DateUtil.intDate()));
    }

    @GetMapping("/position")
    @ApiOperation("持仓详情")
    public Response<CombinePositionVO> position(@ApiParam(value = "组合代码", required = true) @RequestParam("combineCode") String combineCode) {
        return Response.ok(combInfoService.position(combineCode, DateUtil.intDate()));
    }

    @GetMapping("/tradeRule")
    @ApiOperation("交易规则")
    public Response<TradeRuleVO> tradeRule(@ApiParam(value = "组合代码", required = true) @RequestParam("combineCode") String combineCode) {
        return Response.ok(combInfoService.tradeRule(combineCode, DateUtil.intDate()));
    }
}
