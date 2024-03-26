package task;

import com.sun.istack.internal.NotNull;
import task.impl.FileSave2DB;
import util.ThreadUtil;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScan {

    private AtomicInteger threadCount = new AtomicInteger();
    private CountDownLatch latch = new CountDownLatch(1);


    /**
     * @param dir 应该是一个目录
     */
    public void scan(File dir) {
        if (dir == null) {
            return;
        }
        scanInternal(dir);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 扫描任务结束
    }


    private void scanInternal(@NotNull File dir /*只接受文件夹类型*/) {
        if (!dir.isDirectory()) {
            return;
        }
        threadCount.incrementAndGet();
        ThreadUtil.POOL.submit(() -> {
            // 开闭原则：对扩展开放、对修改关闭
            FileSave fileSave = new FileSave2DB();
            fileSave.callback(dir);
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    scanInternal(file);
                } else {
                    // file是文件类型
                }
            }
            threadCount.decrementAndGet();
            if (threadCount.get() == 0) {
                latch.countDown();
            }
        });

    }

}
