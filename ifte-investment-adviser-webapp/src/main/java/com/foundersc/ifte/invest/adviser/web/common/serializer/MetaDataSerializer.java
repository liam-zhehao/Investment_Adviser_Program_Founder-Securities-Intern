package com.foundersc.ifte.invest.adviser.web.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.foundersc.ifte.invest.adviser.web.annotation.MetaData;
import com.foundersc.ifte.invest.adviser.web.enums.MetaDataTypeEnum;
import com.foundersc.ifte.invest.adviser.web.util.MetaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author wangfuwei
 * @date 2022/10/19
 */
@Slf4j
public class MetaDataSerializer extends StdSerializer<Object> implements ContextualSerializer {
    private static final long serialVersionUID = 7919243976717090006L;
    /**
     * 元数据key
     */
    private String key;
    /**
     * 元数据类型
     */
    private MetaDataTypeEnum type;
    /**
     * 元数据使用的枚举
     */
    private Class<?> clazz;
    /**
     * 分隔符
     */
    private String separator;

    private final static String PARSE_BY_CODE_METHOD = "parseByCode";

    private final static String GET_DESC = "getDesc";

    private final static String DESC = "Desc";

    public MetaDataSerializer() {
        super(Object.class);
    }

    public MetaDataSerializer(MetaDataTypeEnum metaDataType, String key, Class<?> clazz, String separator) {
        super(Object.class);
        this.key = key;
        this.type = metaDataType;
        this.clazz = clazz;
        this.separator = separator;
    }

    @Override
    public void serialize(Object metaFieldCode, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (metaFieldCode == null) {
            metaFieldCode = StringUtils.EMPTY;
        }
        jsonGenerator.writeObject(metaFieldCode);
        String filedName = jsonGenerator.getOutputContext().getCurrentName();
        jsonGenerator.writeStringField(getMetaFieldDescName(filedName), getMetaFieldDesc(metaFieldCode.toString()));
    }

    private String getMetaFieldDescName(String fieldName) {
        return fieldName + DESC;
    }

    /**
     * 元数据字段描述
     *
     * @param metaFieldCode
     * @return
     */
    private String getMetaFieldDesc(String metaFieldCode) {
        if (StringUtils.isBlank(metaFieldCode)) {
            return StringUtils.EMPTY;
        }
        switch (type) {
            case ENUM_TYPE:
                return getMetaFieldDescByEnum(metaFieldCode);
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 基于枚举解析
     *
     * @param metaFieldCode
     * @return
     */
    private String getMetaFieldDescByEnum(String metaFieldCode) {
        return MetaDataUtil.getMetaDataDesc(clazz, metaFieldCode, separator);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        String key = null;
        MetaDataTypeEnum type = null;
        Class clazz = null;
        String sep = null;
        MetaData ann = null;
        if (property != null) {
            ann = property.getAnnotation(MetaData.class);
        }
        if (ann != null) {
            key = ann.value();
            type = ann.type();
            clazz = ann.clazz();
            sep = ann.seperator();
        }
        return new MetaDataSerializer(type, key, clazz, sep);
    }
}
