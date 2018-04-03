package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {

    //Easy Tab
    @FXML
    Button easyCreate;
    @FXML
    Button easyFileBrowser;
    @FXML
    Label easyFilePath;
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

    @FXML File locateFile(ActionEvent event) {



        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("/Users/paul/Documents/Programming/VML/Scanner-Praxis/ScannerGUI/src/sample");
        chooser.setInitialDirectory(defaultDirectory);
//        FileChooser chooser = new FileChooser();
//        chooser.setTitle("Open File");
//        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
//        chooser.setSelectedExtensionFilter(FileChooser.;


        File selectedFile = chooser.showDialog(new Stage());
        return selectedFile;
    }


    public void initialize() {
        setText();

        easyTab();
        mediumTab();
        hardTab();
    }

    public void setText(){
        mediumColorMode.getItems().addAll("Text and Line Drawings Only", "Text and Photographs", "Full Photographs");
        mediumColorMode.setValue("Text and Line Drawings Only");

        hardRunScanTailor.setDisable(true);
        hardCreatePDF.setDisable(true);
        hardLayoutOption.getItems().addAll("Auto Detect", "One Page", "Two Page");
        hardLayoutOption.setValue("Auto Detect");
        hardOrientation.getItems().addAll("Left", "Right", "Upsidedown");
        hardOrientation.setValue("Left");
        hardRotate.setPromptText("0.0 mm");
        hardColorMode.getItems().addAll("Text and Line Drawings Only", "Text and Photographs", "Full Photographs");
        hardColorMode.setValue("Text and Line Drawings Only");

    }

    void easyTab(){

        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                easyCreate.setText("!!!");
            }
        });
        easyFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File results = locateFile(new ActionEvent());
                easyFilePath.setText(results.toString());
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
