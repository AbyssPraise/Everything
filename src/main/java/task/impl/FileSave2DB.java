package task.impl;

import app.bean.FileMeta;
import task.FileSave;

import java.io.File;
import java.sql.Date;
import java.util.HashSet;

public class FileSave2DB implements FileSave {
    /**
     * 将dir下的一级目录内的所有文件（或文件夹）保存到数据库
     * @param dir 目录
     */
    @Override
    public void callback(File dir) {
        if (dir == null) {
            return;
        }
        HashSet<FileMeta> localFileSet = new HashSet<FileMeta>();
        for (File file : dir.listFiles()) {
            FileMeta fileMeta = new FileMeta();
            fileMeta.setName(file.getName());
            fileMeta.setPath(file.getParent());
            fileMeta.setLastModifiedTime(new Date(file.lastModified()));
            if (!fileMeta.isDirectory()) {
                // TODO 优化--带单位
                fileMeta.setSize(file.length());
                fileMeta.setDirectory(file.isDirectory());
            }
            localFileSet.add(fileMeta);
        }
//        HashSet<FileMeta> remoteFileSet = 从数据库中查（dir）;
        // 原则：根据local,更改remote：Local有&&remote没有，添加到remote; local没有&&remote有，从remote删除


    }
}
