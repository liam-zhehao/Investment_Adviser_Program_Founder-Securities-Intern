package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangfuwei
 * @date 2023/8/29
 */
@Data
@ApiModel(value = "ClientTargetCombModeVO", description = "用户目标盈续期方式信息")
@NoArgsConstructor
public class ClientTargetCombModeVO {
    /**
     * 当前选中的续期方式
     */
    private String currentMode;

    /**
     * 提示
     */
    private String tip;

    /**
     * 续期方式选项
     */
    private List<ModeItemVO> modeItems;

    public ClientTargetCombModeVO(String currentMode) {
        this.currentMode = currentMode;
        this.tip = "除止盈或到期当天，其余时间均可修改并实时生效";
        this.modeItems = new ArrayList<>();
        this.modeItems.add(new ModeItemVO("0", "自动转出",
                "达到止盈目标或策略到期，本金和收益将自动转出至普通交易账户"));
        this.modeItems.add(new ModeItemVO("1", "自动续期",
                "达到止盈目标或策略到期，本金和收益将自动续投至下一期。如无下一期，资金将转出至普通交易账户"));
    }

    public ClientTargetCombModeVO(String currentMode, String tip) {
        this(currentMode);
        this.tip = tip;
    }

    /**
     * 续期方式项
     */
    @Data
    @AllArgsConstructor
    static class ModeItemVO {
        /**
         * 续期方式id：续期方式 1-到期&止盈续期 0-到期&止盈转出
         */
        private String modeId;
        /**
         * 自动转出；自动续期
         */
        private String modeName;
        /**
         * 描述
         */
        private String modeDesc;
    }
}
