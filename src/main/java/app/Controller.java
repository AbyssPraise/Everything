package app;

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
import util.DBUtil;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane rootPane;

    @FXML
    private TextField searchField;

//    @FXML
//    private TableView<> fileTable;

    @FXML
    private Label srcDirectory;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 程序启动时，创建文件信息表 file_meta
        DBUtil.initTable();
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // TODO

            }
        });
    }

    public void choose(MouseEvent mouseEvent) {

        // 选择文件目录
        DirectoryChooser directoryChooser=new DirectoryChooser();
        Window window = rootPane.getScene().getWindow();
        File file = directoryChooser.showDialog(window);
        String rootPath = file.getPath();
        // 把根路径在页面上显示出来
        srcDirectory.setText(rootPath);
        // 根据根路径获取文件信息
        // TODO List<FileMeta> = 遍历rootPath，将文件保存到List中
        // 将文件信息保存到数据库
    }
}
