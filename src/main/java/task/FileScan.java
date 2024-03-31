package task;

import com.sun.istack.internal.NotNull;
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
    public void scan(File dir, FileSave fileSave) {
        if (dir == null) {
            return;
        }
        System.out.println("扫描任务开始");
        long startTime = System.nanoTime();
        scanInternal(dir, fileSave);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.nanoTime();
        System.out.println("扫描任务结束，总耗时为：" + (endTime - startTime)/1000/1000 + "ms");
    }


    private void scanInternal(@NotNull File dir /*只接受文件夹类型*/, FileSave fileSave) {
        if (!dir.isDirectory()) {
            return;
        }
        threadCount.incrementAndGet();
        ThreadUtil.POOL.submit(() -> {
            // 开闭原则：对扩展开放、对修改关闭
            fileSave.callback(dir);
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    scanInternal(file, fileSave);
                } else {
                    // file是文件类型
                }
            }
            threadCount.decrementAndGet();
            System.out.println(Thread.currentThread().getName() + "结束");
            if (threadCount.get() == 0) {
                latch.countDown();
                System.out.println("所有子线程结束");
            }
        });

    }

}
