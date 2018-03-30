package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML
    Button hardDelete;
    @FXML
    Button hardScan;
    @FXML
    Spinner hardRotate;


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
                hardImport.setText("Importing!");
                String[] env = {"PATH=/bin:/usr/bin/"};
                String cmd = "you complete shell command";
                try {
                    Process process = Runtime.getRuntime().exec(cmd, env);
                    Process pr = Runtime.getRuntime().exec(cmd);
                }
                catch (IOException e){System.out.println("Nope");}
            }
        });
        ChoiceBox hardOrientation = new ChoiceBox();
        hardOrientation.getItems().addAll("item1", "item2", "item3");

        hardDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardDelete.setText("Deleting!");
            }
        });
        hardScan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardScan.setText("Scanning!");
            }
        });
        hardRotate.setPromptText("0.0");

    }




}
