package com.foundersc.ifte.invest.adviser.web.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.kyc.InvestOrganVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InvestOrganConfig {
    public static List<InvestOrganVO> INVEST_ORGAN_LIST;
    public static Map<String, InvestOrganVO> INVEST_ORGAN_MAP = new HashMap<String, InvestOrganVO>();

    @PostConstruct
    public void initInvestOrganInfo() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("invest-organs.json");
            String investOrganStr = IOUtils.toString(classPathResource.getInputStream(), "utf8");
            INVEST_ORGAN_LIST = JSON.parseObject(investOrganStr, new TypeReference<List<InvestOrganVO>>() {
            });
            for (InvestOrganVO investOrganVo : INVEST_ORGAN_LIST) {
                INVEST_ORGAN_MAP.put(investOrganVo.getInvestOrganNo(), investOrganVo);
            }
        } catch (Exception e) {
            log.error("init invest organs error", e);
            throw new BizException(BizErrorCodeEnum.INIT_INVEST_ORGAN_ERROR);
        }
    }
}
