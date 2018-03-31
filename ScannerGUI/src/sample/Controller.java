package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

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
    Button mediumCreatePDF;
    @FXML
    ChoiceBox mediumColorMode = new ChoiceBox();

    //Hard Tab
    @FXML
    Button hardImport;
    @FXML
    Button hardDelete;
    @FXML
    Button hardScan;
    @FXML
    Button hardRunScanTailor;
    @FXML
    Button hardCreatePDF;
    @FXML
    Spinner hardRotate;
    @FXML
    ChoiceBox hardLayoutOption = new ChoiceBox();
    @FXML
    ChoiceBox hardOrientation = new ChoiceBox();
    @FXML
    ChoiceBox hardColorMode = new ChoiceBox();



    public void initialize() {
        setText();

        easyTab();
        mediumTab();
        hardTab();
    }

    public void setText(){
        mediumColorMode.getItems().addAll("Black and White", "Color Grayscale", "Mixed");
        mediumColorMode.setValue("Black and White");

        hardRunScanTailor.setDisable(true);
        hardCreatePDF.setDisable(true);
        hardLayoutOption.getItems().addAll("Auto Detect", "One Page", "Two Page");
        hardLayoutOption.setValue("Auto Detect");
        hardOrientation.getItems().addAll("Left", "Right", "Upsidedown");
        hardOrientation.setValue("Left");
        hardRotate.setPromptText("0.0");
        hardColorMode.getItems().addAll("Black and White", "Color Grayscale", "Mixed");
        hardColorMode.setValue("Black and White");

    }

    void easyTab(){

        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                easyCreate.setText("Creating!");
                //String[] cmd = new String[]{"/bin/sh", "path/to/script.sh"};
            }
        });
    }

    void mediumTab(){
        mediumCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediumCreatePDF.setText("Processing!");
            }
        });
    }

    void hardTab() {
        hardImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardImport.setText("Importing!");
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh test");
                    pb.redirectErrorStream(true);
                    pb.start();
                    //StringBuilder out = new StringBuilder();
                    //BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                }
                catch (IOException e){System.out.println("Nope");}
            }
        });
        hardDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete contents on camera??", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    hardDelete.setText("Deleting!");
                }
            }
        });
        hardScan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardScan.setText("Processing!");
                hardRunScanTailor.setDisable(false);
                hardCreatePDF.setDisable(false);
            }
        });
        hardRunScanTailor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardRunScanTailor.setText("Opening!");
            }
        });
        hardCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardCreatePDF.setText("Creating!");
            }
        });

    }




}
