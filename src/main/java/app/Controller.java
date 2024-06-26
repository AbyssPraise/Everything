package app;

import app.bean.FileMeta;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import task.FileScan;
import task.impl.FileSave2DB;
import util.DBUtil;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<FileMeta> fileTable;

    @FXML
    private Label srcDirectory;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 程序启动时，创建文件信息表 file_meta
        DBUtil.initTable();
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {


            }
        });
    }

    public void choose(MouseEvent mouseEvent) {
        // 选择文件目录
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Window window = rootPane.getScene().getWindow();
        File rootFile = directoryChooser.showDialog(window);
        String rootPath = rootFile.getPath();
        // 把根路径在页面上显示出来
        srcDirectory.setText(rootPath);
        // 根据根路径获取文件信息
        FileScan fileScan = new FileScan();
        FileSave2DB fileSave2DB = new FileSave2DB();
        fileScan.scan(rootFile, fileSave2DB);
        freshTable(rootFile, fileSave2DB);
    }

    private void freshTable(File rootPath, FileSave2DB fileSave2DB) {
        HashSet<FileMeta> fileMetas = fileSave2DB.queryFromDB();
        fileTable.getItems().addAll(fileMetas);
    }
}
