package com.foundersc.ifte.invest.adviser.web.common.serializer;

/**
 * 格式化小数点位数，保留4位
 */
public class DataFormatSerializer4Scale extends DataFormatSerializer {
    @Override
    protected String getPattern() {
        return "#0.0000";
    }
}
