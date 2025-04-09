package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.foundersc.ifte.invest.adviser.web.model.homepage.HomePageVO;
import com.foundersc.ifte.invest.adviser.web.service.HomePageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class HomePageImpl implements HomePageService {
    
    @Autowired
    private HomePageVO homePageVO;

    @Override
    public HomePageVO getHomePageInfo() {
        return homePageVO;
    }
}
