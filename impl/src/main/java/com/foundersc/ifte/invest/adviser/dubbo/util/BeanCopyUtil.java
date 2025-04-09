package com.foundersc.ifte.invest.adviser.dubbo.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean属性拷贝
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
public class BeanCopyUtil {
    /**
     * 字段转换
     */
    private static Map<String, Map<String, String>> fieldMappings = new ConcurrentHashMap<>();

    /**
     * 初始化字段映射表
     *
     * @param clazz
     */
    private static synchronized void initFieldMappingFor(Class clazz) {
        if (fieldMappings.containsKey(clazz.getName())) {
            return;
        }
        Map<String, String> fieldMapping = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (fieldName.contains("_")) {
                // 下划线转驼峰
                fieldMapping.put(fieldName, StrUtil.toCamelCase(fieldName));
            } else {
                // 驼峰转下划线
                fieldMapping.put(fieldName, StrUtil.toUnderlineCase(fieldName));
            }
        }
        fieldMappings.put(clazz.getName(), fieldMapping);
    }

    /**
     * 类的属性映射表
     *
     * @param clazz
     * @return
     */
    private static Map<String, String> getFieldMapping(Class clazz) {
        if (fieldMappings.containsKey(clazz.getName())) {
            return fieldMappings.get(clazz.getName());
        }
        initFieldMappingFor(clazz);
        return fieldMappings.get(clazz.getName());
    }

    /**
     * 属性拷贝（驼峰和下划线互相转换）
     *
     * @param source
     * @param target
     */
    public static void copyUnderlineProperties(Object source, Object target) {
        BeanUtil.copyProperties(source, target, CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true)
                .setFieldMapping(getFieldMapping(source.getClass())));
    }

    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
        return cn.hutool.core.bean.BeanUtil.copyToList(collection, targetType, CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreError(true));
    }
}
