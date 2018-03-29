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

    //Easy Tab
    @FXML
    Button easyCreate;
    @FXML
    CheckBox easyCheck;
    @FXML
    ProgressBar easyLoading;

    //Medium Tab
    @FXML
    Button mediumImport;
    @FXML
    Button mediumProcess;

    //Hard Tab
    @FXML
    Button hardImport;

    public void initialize() {
        easyTab();
        mediumTab();
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
        mediumImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediumImport.setText("working!");
                easyCreate.setText("Lol");
            }
        });
        mediumProcess.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediumProcess.setText("working!");
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
                try {
                    Process process = Runtime.getRuntime().exec(cmd, env);
                    Process pr = Runtime.getRuntime().exec(cmd);
                }
                catch (IOException e){System.out.println("Nope");}
            }
        });
    }




}
