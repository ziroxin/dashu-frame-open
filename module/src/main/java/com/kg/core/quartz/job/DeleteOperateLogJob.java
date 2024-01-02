package com.kg.core.quartz.job;

import cn.hutool.log.StaticLog;
import com.kg.component.utils.TimeUtils;
import com.kg.core.zlog.entity.ZOperateLog;
import com.kg.core.zlog.service.ZOperateLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * 定时删除用户操作日志
 *
 * @author ziro
 * @date 2023/11/8 17:32
 */
public class DeleteOperateLogJob implements Job {
    @Value("${com.kg.auto-operate-log:180}")
    private Integer autoOperateLogDay;
    @Resource
    private ZOperateLogService logService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 删除操作日志，默认保存180天（在com.kg.auto-operate-log里配置）
        StaticLog.warn("---delete operate log---(Delete " + autoOperateLogDay + " days ago!!!)");
        String deleteDate = TimeUtils.now().addDay(-autoOperateLogDay).toFormat("yyyy-MM-dd 00:00:00");
        logService.lambdaUpdate().lt(ZOperateLog::getCreateTime, deleteDate).remove();
    }
}
