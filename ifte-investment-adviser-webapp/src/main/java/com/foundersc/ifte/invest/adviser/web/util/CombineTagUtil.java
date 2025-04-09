package com.foundersc.ifte.invest.adviser.web.util;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifte.invest.adviser.web.model.combine.TagVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangfuwei
 * @date 2022/10/27
 */
@Slf4j
public class CombineTagUtil {
    private CombineTagUtil() {
    }

    /**
     * 组合标签最大展示数量
     */
    private static final int COMB_TAG_MAX_NUM = 2;

    /**
     * 组合运营标签类型
     */
    private static final String COMB_OPE_TAG_TYPE = "1";

    /**
     * 获取运营标签
     *
     * @param tagJson
     * @return
     */
    public static List<String> getOpeTags(String tagJson) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(tagJson)) {
            return result;
        }
        try {
            List<TagVO> tags = JSONObject.parseArray(tagJson, TagVO.class);
            if (CollectionUtils.isEmpty(tags)) {
                return result;
            }
            return tags.stream()
                    .sorted(Comparator.comparing(TagVO::getPriorityLevel).reversed())
                    .filter(x -> COMB_OPE_TAG_TYPE.equals(x.getTagType()))
                    .map(x -> x.getTagName())
                    .limit(COMB_TAG_MAX_NUM)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("parse tagJson error, tagJson={}", tagJson, e);
            return result;
        }
    }
}
