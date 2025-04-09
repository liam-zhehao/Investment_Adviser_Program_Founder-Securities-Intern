package com.foundersc.ifte.invest.adviser.web.util;

import cn.hutool.core.util.CharUtil;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CorpRiskLevelEnum;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangfuwei
 * @date 2022/10/25
 */
@Slf4j
public class MetaDataUtil {
    private final static String PARSE_BY_CODE_METHOD = "parseByCode";

    private final static String GET_DESC = "getDesc";

    /**
     * 基于枚举获取元数据描述
     *
     * @param clazz         枚举类，例如 {@link CorpRiskLevelEnum}
     * @param metaFieldCode 枚举值中的code，例如 {@link CorpRiskLevelEnum#getCode()}
     * @param separator     描述字段有多个值时的分隔符
     * @return
     */
    public static String getMetaDataDesc(Class clazz, String metaFieldCode, String separator) {
        if (clazz == null || StringUtils.isBlank(metaFieldCode)) {
            return StringUtils.EMPTY;
        }
        List<String> descList = new ArrayList<>();
        String[] codes = metaFieldCode.split(String.valueOf(CharUtil.COMMA));
        for (String code : codes) {
            try {
                Method parseByCodeMethod = clazz.getMethod(PARSE_BY_CODE_METHOD, String.class);
                Object metaEnum = parseByCodeMethod.invoke(null, code);
                Method getDescMethod = clazz.getMethod(GET_DESC);
                descList.add((String) getDescMethod.invoke(metaEnum));
            } catch (Exception e) {
                log.warn("getMetaFieldDescByEnum failed, metaFieldCode={}, clazz={} ", code, clazz, e);
            }
        }
        if (CollectionUtils.isEmpty(descList)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.join(descList, separator);
    }

    /**
     * 基于枚举获取元数据描述
     *
     * @param clazz
     * @param metaFieldCode
     * @return
     */
    public static String getMetaDataDesc(Class clazz, String metaFieldCode) {
        return getMetaDataDesc(clazz, metaFieldCode, CommonConstants.CHINESE_COMMA);
    }
}
