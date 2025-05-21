package com.kg.other;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.kg.component.utils.MyRSAUtils;
import com.kg.component.utils.TimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * @author ziro
 * @date 2024/8/6 17:32
 */
@SpringBootTest
public class OtherTest {

    @Test
    public void testMyRSA() throws UnsupportedEncodingException {
        String aa = MyRSAUtils.encryptPublic("测试！");
        System.out.println(aa);
        System.out.println(MyRSAUtils.decryptPrivate(aa));
    }


    @Test
    public void test() throws IOException {
        // 读取压缩文件内容
        List<String> list = ZipUtil.listFileNames(new ZipFile("D:\\Users\\Administrator\\Pictures\\P020231214534241794139.zip"), null);
        for (String s : list) {
            System.out.println(s);
        }
    }


    /**
     * Hutool的 - 线程级定时任务 - 测试
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start(true);

        for (int i = 0; i < 10; i++) {
            String runtimeStr = TimeUtils.now().addSecond(10).toFormat("s m H d M *");
            // 定时任务
            int finalI = i;
            System.out.println(i + "=" + runtimeStr);
            CronUtil.schedule(runtimeStr, (Task) () -> {
                System.out.println("abcdefg" + finalI + "=======" + runtimeStr);
                System.out.println("当前线程ID：" + Thread.currentThread().getId());
            });
            Thread.sleep(1000);
        }

        System.out.println("11");
        System.out.println("当前线程ID：" + Thread.currentThread().getId());

        Thread.sleep(15000);

        System.out.println("2");
        System.out.println("当前线程ID：" + Thread.currentThread().getId());
    }


}
