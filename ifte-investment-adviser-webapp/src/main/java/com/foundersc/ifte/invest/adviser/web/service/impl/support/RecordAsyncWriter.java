package com.foundersc.ifte.invest.adviser.web.service.impl.support;

import cn.hutool.core.thread.NamedThreadFactory;
import com.foundersc.ifte.invest.adviser.web.common.async.TraceableRunnable;
import com.foundersc.ifte.invest.adviser.web.entity.CombRequestRecordEntity;
import com.foundersc.ifte.invest.adviser.web.service.dao.CombRequestRecordDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.foundersc.ifte.invest.adviser.web.enums.AlarmErrorEnum.SAVE_COMB_REQUEST_RECORD_ERROR;

/**
 * 用于异步保存数据库
 *
 * @author wangfuwei
 * @date 2022/11/1
 */
@Component
@Slf4j
public class RecordAsyncWriter {
    @Autowired
    private CombRequestRecordDaoService combRequestRecordDaoService;

    /**
     * 异步保存数据库的线程池
     */
    private static final ExecutorService WRITER_EXECUTOR = new ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(1000), new NamedThreadFactory("Record-Writer", true), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 异步入库
     *
     * @param entity
     */
    public void asyncSaveCombRequestRecord(CombRequestRecordEntity entity) {
        WRITER_EXECUTOR.submit(new SaveCombRequestRecordTask(combRequestRecordDaoService, entity));
    }

    /**
     * 保存组合申请记录的任务
     */
    @Slf4j
    static class SaveCombRequestRecordTask extends TraceableRunnable {
        private CombRequestRecordDaoService combRequestRecordDaoService;
        private CombRequestRecordEntity entity;

        SaveCombRequestRecordTask(CombRequestRecordDaoService combRequestRecordDaoService, CombRequestRecordEntity entity) {
            this.combRequestRecordDaoService = combRequestRecordDaoService;
            this.entity = entity;
        }

        @Override
        protected void doRun() {
            try {
                boolean saved = combRequestRecordDaoService.save(entity);
                if (saved) {
                    log.info("save combRequestRecord success, entity={}", entity);
                } else {
                    log.error("{} save combRequestRecord fail, entity={}", SAVE_COMB_REQUEST_RECORD_ERROR.getAlarmInfo(), entity);
                }
            } catch (Exception e) {
                log.error("{} save combRequestRecord error, entity={}", SAVE_COMB_REQUEST_RECORD_ERROR.getAlarmInfo(), entity, e);
            }
        }
    }
}
