package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.*;

public class Controller {
    File currentDirectory = null;
    File defaultDirectory;
    String projectName;

    String colorModeOption;
    boolean ocrOption;
    boolean txtOutputOption;
    String layoutOption;
    String orientationOption;
    String marginOption;

    String layout = "0";
    String layoutDirection = "lr";
    String orientation = "left";
    String rotate = "0.0";
    String deskew = "auto";
    String contentDirection = "normal";
    String margins = "2";
    String alignment = "center";
    String dpi = "300";
    String outputDpi = "1200";
    String colorMode = "black_and_white";
    String whiteMargins = "false";
    String threshold = "0";
    String despeckle = "normal";
    String dewarping = "off";
    String depthPerception = "2.0";
    String startFilter = "4";
    String endFilter = "6";


    //Easy Tab
    @FXML
    Button easyCreate;
    @FXML
    Button easyFileBrowser;
    @FXML
    Label easyFilePath;
    @FXML
    ProgressBar easyLoading;
    @FXML
    TextArea easyLog;
    @FXML
    TextField easyProjectName;

    //Medium Tab
    @FXML
    Button normalFileBrowser;
    @FXML
    Label normalFilePath;
    @FXML
    Button mediumCreatePDF;
    @FXML
    ChoiceBox mediumColorMode = new ChoiceBox();
    @FXML
    TextArea normalLog;

    //Hard Tab
    @FXML
    Button hardFileBrowser;
    @FXML
    Label hardFilePath;
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
    Spinner hardMargins;
    @FXML
    ChoiceBox hardLayoutOption = new ChoiceBox();
    @FXML
    ChoiceBox hardOrientation = new ChoiceBox();
    @FXML
    ChoiceBox hardColorMode = new ChoiceBox();

    @FXML File locateFile(ActionEvent event) {
        //Create a Directory chooser object
        DirectoryChooser dirChooser = new DirectoryChooser();
        //Set the title (shown at the top of the window) of this directory browser window
        dirChooser.setTitle("Open Resource File");
        //Show the directory chooser window and wait for user to choose directory
        File f = dirChooser.showDialog(null);
        //Return the directory the user chose
        return f;
    }

    //Initializes the project
    public void initialize() {
        setText();
        easyTab();
        mediumTab();
        hardTab();
    }

    private void setText(){
        //Adds options to medium tab's color mode
        mediumColorMode.getItems().addAll("Text and Line Drawings Only", "Text and Photographs", "Full Photographs");
        //Set default value of medium tab color mode options
        mediumColorMode.setValue("Text and Line Drawings Only");
        //Disable two buttons in the hard tab
        hardRunScanTailor.setDisable(true);
        hardCreatePDF.setDisable(true);
        //Adds options to hard tab's layout options
        hardLayoutOption.getItems().addAll("Auto Detect", "One Page", "Two Page");
        //Set default layout option
        hardLayoutOption.setValue("Auto Detect");
        //Adds options to hard tab's orientation options
        hardOrientation.getItems().addAll("Left", "Right", "Upsidedown");
        //Set default orientation options
        hardOrientation.setValue("Left");
        //Sets default margin in hard tab
        hardMargins.getEditor().setPromptText("0.0 mm");
        //Sets color mode options in hard tab
        hardColorMode.getItems().addAll("Text and Line Drawings Only", "Text and Photographs", "Full Photographs");
        //Sets default value in hard tab color options
        hardColorMode.setValue("Text and Line Drawings Only");
    }

    void appendLog(String s){
        easyLog.appendText(s);
        normalLog.appendText(s);
    }

    // For the easy tab of the GUI
    void easyTab(){
        // Handles event of "Download Location" button click on the easy tab of the GUI
        easyFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Try to locate the file
                try{
                    currentDirectory = locateFile(new ActionEvent());
                    easyFilePath.setText(currentDirectory.toString());
                    normalFilePath.setText(currentDirectory.toString());
                    hardFilePath.setText(currentDirectory.toString());
                }
                catch (Exception e){
                    System.out.println("No directory selected.");
                }
            }
        });

        //Handles event of "Download and Create PDF" button
        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //If the current directory has been set, do the scanning; otherwise, show error message
                if(!(currentDirectory == null)){
                    easyCreate.setText("Processing...");
                    appendLog("Processing...\n");
                    scanTail();
                    imageMagick();
                    tesseract();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please chose a place to scan to.", ButtonType.OK);
                    alert.showAndWait();
                }
                easyCreate.setText("Download and Create PDF");
            }
        });

    }

    // Does the scanner, using the auto scan script
    void scanTail(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString() + "/";
            //System.out.println(fileOfDirectory);
            //System.out.println(System.getProperty("user.dir") + "/auto_scan.sh");
            System.out.println("Starting ScanTailor...");
            appendLog("Starting ScanTailor...");
            String[] command = {"scantailor-cli",
                "--layout=" + layout,
                "--layout-direction=" + layoutDirection,
                "--orientation=" + orientation,
                "--rotate=" + rotate,
                "--deskew=" + deskew,
                "--content-direction=" + contentDirection,
                "--margins=" + margins,
                "--alignment=" + alignment,
                "--dpi=" + dpi,
                "--output-dpi=" + outputDpi,
                "--color-mode=" + colorMode,
                "--white-margins=" + whiteMargins,
                "--threshold=" + threshold,
                "--despeckle=" + despeckle,
                "--dewarping=" + dewarping,
                "--depth-perception=" + depthPerception,
                "--start-filter=" + startFilter,
                "--end-filter=" + endFilter,
                "--output-project=" + fileOfDirectory + "/Project.ScanTailor",
                fileOfDirectory,
                fileOfDirectory
            };
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();

            System.out.println("Finished ScanTailor");
            appendLog("Finished ScanTailor\n");
        }
        catch (Exception e){
            appendLog("Failed\n");
            appendLog(e.getMessage());
            System.out.println("Failed");
        }
    }

    void imageMagick(){
        try {
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString() + "/";

            System.out.println("Starting Imagemagick...");
            appendLog("Starting Imagemagick...");

            String[] command = new String[]{"convert", fileOfDirectory + "*.tif", fileOfDirectory + "output.tiff"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();
            System.out.println("Finished Imagemagick");
            appendLog("Finished Imagemagick\n");
        }
        catch (Exception e){
            appendLog("Failed Imagemagick\n");
            appendLog(e.getMessage());
            System.out.println("Failed Imagemagick");
        }
    }

    void tesseract(){
        try {
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString() + "/";
            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Starting Tesseract...");
            appendLog("Starting Tesseract...");

            String[] command = new String[]{"tesseract", fileOfDirectory + "output.tiff", fileOfDirectory + "outputOCR", "-l", "eng", "pdf"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();
            System.out.println("Finished Tesseract");
            appendLog("Finished Tesseract\n");
        }
        catch (Exception e){
            appendLog("Failed tesseract\n");
            appendLog(e.getMessage());
            System.out.println("Failed tesseract");
        }
    }

    // For the medium tab of the GUI
    void mediumTab(){
        normalFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentDirectory = locateFile(new ActionEvent());
                easyFilePath.setText(currentDirectory.toString());
                normalFilePath.setText(currentDirectory.toString());
                hardFilePath.setText(currentDirectory.toString());
            }
        });
        mediumColorMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(mediumColorMode.getValue());
                if(mediumColorMode.getValue().equals("Text and Photographs")){
                    mediumCreatePDF.setDisable(true);
                }
                else{
                    mediumCreatePDF.setDisable(false);
                }
                //TODO: Handle change of options in 'backend'
            }
        });
        mediumCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediumCreatePDF.setText("Processing!");
                //TODO: Do the processing
            }
        });
    }

    // For the hard tab of the GUI
    void hardTab() {
        hardFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentDirectory = locateFile(new ActionEvent());
                easyFilePath.setText(currentDirectory.toString());
                normalFilePath.setText(currentDirectory.toString());
                hardFilePath.setText(currentDirectory.toString());
            }
        });

        hardImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardImport.setText("Importing!");
                //TODO: Do the importing
                hardDelete.setDisable(true);
                try {
                    ProcessBuilder pb = new ProcessBuilder("sh test");
                    pb.redirectErrorStream(true);
                    pb.start();
                }
                catch (IOException e){e.printStackTrace();}
            }
        });

        hardDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete contents on camera??", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    hardDelete.setText("Deleting!");
                    //TODO: Make the delete happen
                }
            }
        });

        hardScan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardScan.setText("Processing!");
                //TODO: Do the processing
                hardRunScanTailor.setDisable(false);
                hardCreatePDF.setDisable(false);
            }
        });

        hardRunScanTailor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardRunScanTailor.setText("Opening!");
                //TODO: DO the opening
            }
        });

        hardCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardCreatePDF.setText("Creating!");
                //TODO: DO the creating
            }
        });
    }
}
