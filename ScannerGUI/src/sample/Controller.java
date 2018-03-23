package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

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

    public void initialize() {
        easyTab();
    }

    void easyTab(){

        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                easyCreate.setText("working!");
            }
        });

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b2.setText("working!");
            }
        });




    }




}
