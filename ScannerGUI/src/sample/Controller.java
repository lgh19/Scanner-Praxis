package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    Button easyCreate;
    @FXML
    Button b2;
    @FXML
    CheckBox easyCheck;
    @FXML
    ProgressBar easyLoading;

    @FXML
    Button hardImport;

    public void initialize() {
        easyTab();
        //mediumTab();
        hardTab();
    }

    void easyTab(){

        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                easyCreate.setText("working!");
                String[] cmd = new String[]{"/bin/sh", "path/to/script.sh"};
            }
        });
    }

    void mediumTab(){
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b2.setText("working!");
            }
        });
    }

    void hardTab() {
        hardImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardImport.setText("Running!");
                String[] env = {"PATH=/bin:/usr/bin/"};
                String cmd = "you complete shell command";
                Process process = Runtime.getRuntime().exec(cmd, env);
                try {
                    Process pr = Runtime.getRuntime().exec(cmd);
                }
                catch (IOException e){System.out.println("Nope");}
            }
        });
    }




}
