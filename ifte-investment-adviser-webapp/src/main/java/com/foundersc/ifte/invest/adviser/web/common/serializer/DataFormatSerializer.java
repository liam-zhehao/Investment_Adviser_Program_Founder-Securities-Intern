package com.foundersc.ifte.invest.adviser.web.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 格式化小数点位数，保留2位
 */
@Slf4j
public class DataFormatSerializer extends JsonSerializer {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            try {
                DecimalFormat decimalFormat = new DecimalFormat(getPattern());
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                gen.writeString(decimalFormat.format(value));
            } catch (IOException e) {
                log.error("dataFormat error, value={}", value, e);
            }
        }
    }

    /**
     * 默认保留2位小数
     *
     * @return
     */
    protected String getPattern() {
        return "#0.00";
    }
}
