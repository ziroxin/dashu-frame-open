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
        String aa = MyRSAUtils.encryptPublic("您正在使用阿里云短信测试服务，体验验证码是：${code}，如非本人操作，请忽略本短信！");
        System.out.println(aa);
        System.out.println(MyRSAUtils.decryptPrivate(aa));
        String bb = MyRSAUtils.decryptPrivate("kEUl6hEmvtKd4rTJEoq2w0fXgWqhZe2BBMy1Hm9ap3A2IDTywrrGYVKChnZ4xl8OGznooZxmwpZA/ifgkuF5d0KwSd5GD/rQr70vMKOgOHUAsWrzYnKX9SkOJLGVJxw+SeYRp+tMMXaHnPkLSng74FZTfbK292S6J4gClgupdd8=@dashu@j59KAMTuoxkImooW4V+HQjUOzJHLd+0HPk9aNFstUkEI/oFBTmy+/jci/6V2o5WB01a/NJYa2LJgo59tgIM6XytTDASqPJuyDJGKdlqLQpuGn/hSW+MlvdMLsdutpxV8scMMjinF6+/3HTt+iX+xJRkTb4Et8AN6JJK0Df9Am0E=@dashu@mcPs53eCdOXgXZx+16Eqxs5Xlw7nwAqhAAE8iuKYFdS7P261gUBLk8eny1j8gdubmc1uOokY8HGZmCzVj+U0bf8R09i+TuH++VYsl43ab5Ac0h47UBqIQXsyaHTjf9LTX0nqiPSSM3+uzAu3DQAv/ettYGo6m/hu84/YYkzefDA=@dashu@WkhKlKJLUFHGbHoUv9Vxa1vEavAPTiAbquNGMU/se7EeirqBw/fU4jZWLcYTxgyUJbsB5xnxt6KfjcqF6vQnCehfhVkpdzUPFAUTqDdgVKsGJHLJZiE/MU1UvhetVrtvPq9XLjLVDthhtYbl0RFvt91LDJabiJ/8WrGxJT1gZos=");
        System.out.println(bb);
        System.out.println(URLDecoder.decode(bb, "utf-8"));
        System.out.println(MyRSAUtils.decryptPrivate(""));
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
