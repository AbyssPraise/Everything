package task.impl;

import app.bean.FileMeta;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import task.FileSave;
import util.DBUtil;

import java.io.File;
import java.sql.*;
import java.util.HashSet;


@Slf4j
public class FileSave2DB implements FileSave {

    Logger logger = LoggerFactory.getILoggerFactory().getLogger("FileSave2DB");

    /**
     * 将dir下的一级目录内的所有文件（或文件夹）保存到数据库
     *
     * @param dir 目录
     */
    @Override
    public void callback(File dir) {
        if (dir == null) {
            return;
        }
        System.out.println("保存" + dir.getPath() + " 下的子节点文件");
        HashSet<FileMeta> localFileSet = new HashSet<>();
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
        HashSet<FileMeta> remoteFileSet = queryFromDB(dir);
//        原则：根据local,更改remote：Local有&&remote没有，添加到remote;
        for (FileMeta fileMeta : localFileSet) {
            if (!remoteFileSet.contains(fileMeta)) {
                insert(fileMeta);
            }
        }
//        local没有&&remote有，从remote删除
        for (FileMeta fileMeta : remoteFileSet) {
            if (!localFileSet.contains(fileMeta)) {
                delete(fileMeta);
            }
        }

    }

    private void insert(FileMeta fileMeta) {
        String sql = "insert into file_meta values (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = DBUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fileMeta.getName());
            preparedStatement.setString(2, fileMeta.getPath());
            preparedStatement.setLong(3, fileMeta.getSize());
            preparedStatement.setDate(4, fileMeta.getLastModifiedTime());
            preparedStatement.setBoolean(5, fileMeta.isDirectory());
            preparedStatement.setString(6, null);
            preparedStatement.setString(7, null);
            int rows = preparedStatement.executeUpdate();
            System.out.println("插入 " + rows + "行数据");
        } catch (SQLException e) {
            System.out.println("FileSave2DB#insert()失败，e.getMessage() = " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void delete(FileMeta fileMeta) {
        String sql = "delete from file_meta where name = ? and path = ?";
        Connection connection = DBUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, fileMeta.getName());
            preparedStatement.setString(2, fileMeta.getPath());
            int rows = preparedStatement.executeUpdate();
            System.out.println("删除 " + rows + "行数据");
        } catch (SQLException e) {
            System.out.println("FileSave2DB#delete()失败，e.getMessage() = " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 只查询path为dir的文件记录
     */
    public HashSet<FileMeta> queryFromDB(File dir) {
        HashSet<FileMeta> remoteFileSet = new HashSet<>();
        String sql = "select * from file_meta where path = ?";
        Connection connection = DBUtil.getConnection();
//        try resource 写法
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, dir.getPath());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FileMeta fileMeta = new FileMeta();
                    fileMeta.setName(resultSet.getString(1));
                    fileMeta.setPath(resultSet.getString(2));
                    fileMeta.setSize((resultSet.getLong(3)));
                    fileMeta.setLastModifiedTime(resultSet.getDate(4));
                    fileMeta.setDirectory(resultSet.getBoolean(5));
                    remoteFileSet.add(fileMeta);
                }
            }
        } catch (SQLException e) {
            System.out.println("FileSave2DB#queryFromDB(dir)失败，e.getMessage() = " + e.getMessage());
            throw new RuntimeException(e);
        }
        return remoteFileSet;
    }

    public HashSet<FileMeta> queryFromDB() {
        HashSet<FileMeta> remoteFileSet = new HashSet<>();
        String sql = "select * from file_meta";
        Connection connection = DBUtil.getConnection();
//        try resource 写法
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FileMeta fileMeta = new FileMeta();
                    fileMeta.setName(resultSet.getString(1));
                    fileMeta.setPath(resultSet.getString(2));
                    fileMeta.setSize((resultSet.getLong(3)));
                    fileMeta.setLastModifiedTime(resultSet.getDate(4));
                    fileMeta.setDirectory(resultSet.getBoolean(5));
                    remoteFileSet.add(fileMeta);
                }
            }
        } catch (SQLException e) {
            System.out.println("FileSave2DB#queryFromDB()失败，e.getMessage() = " + e.getMessage());
            throw new RuntimeException(e);
        }
        return remoteFileSet;
    }
}
