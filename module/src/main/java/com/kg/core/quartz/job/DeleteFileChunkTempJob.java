package com.kg.core.quartz.job;

import cn.hutool.log.LogFactory;
import cn.hutool.log.StaticLog;
import com.kg.component.file.utils.UploadFileChunksUtils;
import com.kg.component.utils.TimeUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 定时删除分片上传的临时文件
 *
 * @author ziro
 * @date 2022-12-26 15:22:11
 */
public class DeleteFileChunkTempJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        StaticLog.info("定时删除分片上传的临时文件");
        // 默认配置：删除1天前的临时文件（默认检查30天）
        for (int i = 2; i <= 31; i++) {
            // 0点后执行，因此i从2开始，-2天
            String folder = TimeUtils.now().addDay(-i).toFormat("yyyyMMdd");
            // 删除分片临时文件夹
            UploadFileChunksUtils.deleteChunksByQuartz(folder);
        }
    }
}
