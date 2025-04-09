package com.foundersc.ifte.invest.adviser.web.controller;

import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.model.homepage.HomePageVO;
import com.foundersc.ifte.invest.adviser.web.service.HomePageService;
import com.foundersc.ifte.invest.adviser.web.service.impl.OtelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 投顾首页
 */
@RestController
@RequestMapping("/api/investAdviser/noAuth/homePage")
@Api(tags = {"投顾首页"})
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private OtelService otelService;


    @GetMapping
    @ApiOperation("首页")
    public Response<HomePageVO> homePage() {
        try {
            otelService.incrementEverydayCustomerCounter(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok(homePageService.getHomePageInfo());
    }
}
