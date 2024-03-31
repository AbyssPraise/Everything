package util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static volatile SQLiteDataSource DATASOURCE = null;
    private static volatile Connection CONNECTION = null;
    private static String DATE_STRING_FORMAT = "yy:mm:dd hh:mm:ss";

    private static final Logger logger = LogManager.getLogger(DBUtil.class);


    private static DataSource getDataSource() {
        if (DATASOURCE == null) {
            synchronized (DBUtil.class) {
                if (DATASOURCE == null) {
                    SQLiteConfig config = new SQLiteConfig();
                    config.setDateStringFormat(DATE_STRING_FORMAT);
                    DATASOURCE = new SQLiteDataSource(config);
                    DATASOURCE.setUrl(getUrl());
                }
            }
        }
        return DATASOURCE;
    }

    private static String getUrl() {
        // jdbc:sqlite://sqlite数据库的地址（一个.db文件的地址）
        String dbPath = "E:\\IdeaProjects\\Everything\\target";
        String dbName = "everything.db";
        return "jdbc:sqlite://" + dbPath + File.separator + dbName;
    }

    public static Connection getConnection() {
        if (CONNECTION == null) {
            synchronized (DBUtil.class) {
                if (CONNECTION == null) {
                    try {
                        CONNECTION = getDataSource().getConnection();
                    } catch (SQLException e) {
                        logger.error(e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return CONNECTION;
    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public static void close(Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            close(statement);
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public static void initTable() {
        String sqls = readSql();
        String[] sqlArray = sqls.split(";\r\n");
        Statement statement = null;
        for (int i = 0; i < sqlArray.length; i++) {
            String sql = sqlArray[i];
            try {
                statement = getConnection().createStatement();
                statement.executeLargeUpdate(sql);
                // 日志级别 error > warn > info > debug > trace
                logger.info("执行sql: " + sql);
            } catch (SQLException e) {
                logger.error(e);
                throw new RuntimeException(e);
            }
        }
        close(statement);
    }

    private static String readSql() {
        String content = "";
        try {
            // 读取资源文件的内容到字符串
            content = new String(Files.readAllBytes(Paths.get("src/main/resources/init.sql")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
