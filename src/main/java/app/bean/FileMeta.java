package app.bean;

import lombok.Data;

import java.sql.Date;

@Data
public class FileMeta {
    private String name;
    private String path;
    private long size;
    private Date lastModifiedTime;
    private boolean isDirectory;
}
