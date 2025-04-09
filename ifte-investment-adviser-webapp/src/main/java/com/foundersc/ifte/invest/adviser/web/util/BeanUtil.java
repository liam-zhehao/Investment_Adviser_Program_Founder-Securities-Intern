package com.foundersc.ifte.invest.adviser.web.util;

import cn.hutool.core.bean.copier.CopyOptions;

import java.util.Collection;
import java.util.List;

/**
 * @author wangfuwei
 * @date 2022/9/28
 */
public class BeanUtil {
    /**
     * 属性拷贝
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        cn.hutool.core.bean.BeanUtil.copyProperties(source, target, CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true));
    }

    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
        return cn.hutool.core.bean.BeanUtil.copyToList(collection, targetType,CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true));
    }
}
