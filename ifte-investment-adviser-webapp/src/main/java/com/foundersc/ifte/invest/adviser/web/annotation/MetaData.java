package com.foundersc.ifte.invest.adviser.web.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.foundersc.ifte.invest.adviser.web.common.serializer.MetaDataSerializer;
import com.foundersc.ifte.invest.adviser.web.enums.MetaDataTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangfuwei
 * @date 2022/10/19
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(
        using = MetaDataSerializer.class
)
public @interface MetaData {
    String value() default "";

    Class<?> clazz() default Object.class;

    MetaDataTypeEnum type() default MetaDataTypeEnum.ENUM_TYPE;

    String seperator() default "，";
}
