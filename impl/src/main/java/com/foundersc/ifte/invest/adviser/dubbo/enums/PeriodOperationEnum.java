package com.foundersc.ifte.invest.adviser.dubbo.enums;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombPositionDetail;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangfuwei
 * @date 2023/9/4
 */
@Getter
public enum PeriodOperationEnum {
    OPE1("资金转入/撤单"),
    OPE2("自主转出"),
    OPE3("自动止盈");

    private String desc;

    PeriodOperationEnum(String desc) {
        this.desc = desc;
    }

    /**
     * 参与期操作
     *
     * @return
     */
    public static List<CombPositionDetail.Operation> getParticipationOperations() {
        return Arrays.asList(new CombPositionDetail.Operation(OPE1.getDesc(), true),
                new CombPositionDetail.Operation(OPE2.getDesc(), false),
                new CombPositionDetail.Operation(OPE3.getDesc(), false));
    }

    /**
     * 观察期操作
     *
     * @return
     */
    public static List<CombPositionDetail.Operation> getObservationOperations() {
        return Arrays.asList(new CombPositionDetail.Operation(OPE1.getDesc(), false),
                new CombPositionDetail.Operation(OPE2.getDesc(), true),
                new CombPositionDetail.Operation(OPE3.getDesc(), false));
    }

    /**
     * 止盈期操作
     *
     * @return
     */
    public static List<CombPositionDetail.Operation> getProfitOperations() {
        return Arrays.asList(new CombPositionDetail.Operation(OPE1.getDesc(), false),
                new CombPositionDetail.Operation(OPE2.getDesc(), true),
                new CombPositionDetail.Operation(OPE3.getDesc(), true));
    }
}
