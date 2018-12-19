package Scanner;

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
import java.util.*;

import java.awt.Graphics2D;
import java.awt.image.*;

//import javax.imageio.ImageIO;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

public class Controller {
    File currentDirectory = null;
    File leftCamDirectory = null;
    File rightCamDirectory = null;
    File defaultDirectory;
    String projectName = "Project";

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

    boolean useOCRMedium = true;
    boolean txtOutputMedium = false;

    boolean useOCRHard = true;
    boolean txtOutputHard = false;

    boolean camerasConnected = false;

    //Easy Tab
    @FXML
    CheckBox usingCameras;
    @FXML
    Button easyCreate;
    @FXML
    Button easyFileBrowser;
    @FXML
    Button easySetLeftCamera;
    @FXML
    Button easySetRightCamera;
    @FXML
    Label easyFilePath;
    @FXML
    Label leftCamFilePath;
    @FXML
    Label rightCamFilePath;
    @FXML
    ProgressBar easyLoading;
    @FXML
    TextArea easyLog;
    @FXML
    TextField easyProjectName;

    //Medium Tab
    @FXML
    Button mediumCreatePDF;
    @FXML
    ChoiceBox mediumColorMode = new ChoiceBox();
    @FXML
    TextArea normalLog;
    @FXML
    CheckBox mediumUseOCR;
    @FXML
    CheckBox mediumTxtOutput;

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
    Spinner hardMargins;
    @FXML
    ChoiceBox hardLayoutOption = new ChoiceBox();
    @FXML
    ChoiceBox hardOrientation = new ChoiceBox();
    @FXML
    ChoiceBox hardColorMode = new ChoiceBox();
    @FXML
    CheckBox hardUseOCR;
    @FXML
    CheckBox hardTxtOutput;
    @FXML
    TextArea expertLog;

    //Initializes the project
    public void initialize() {
        setText();
        configTab();
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
        s = s.trim() + "\n";
        easyLog.appendText(s);
        normalLog.appendText(s);
        expertLog.appendText(s);
    }

    void configTab(){
        // Handles event of "Download Location" button click on the easy tab of the GUI
        easyFileBrowser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Try to locate the file
                try{
                    DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle("Choose destination folder");
                    currentDirectory = chooser.showDialog(new Stage());
                    easyFilePath.setText(currentDirectory.toString());
                }
                catch (Exception e){
                    System.out.println("No directory selected.");
                }
            }
        });

        easySetLeftCamera.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Try to locate the file
                try{
                    DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle("Choose left camera folder");
                    leftCamDirectory = chooser.showDialog(new Stage());
                    leftCamFilePath.setText(leftCamDirectory.toString());
                    easySetLeftCamera.setText("Left Camera Set");
                }
                catch (Exception e){
                    System.out.println("No directory selected.");
                }
            }
        });

        easySetRightCamera.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //Try to locate the file
                try{
                    DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle("Choose right camera folder");
                    rightCamDirectory = chooser.showDialog(new Stage());
                    rightCamFilePath.setText(rightCamDirectory.toString());
                    easySetRightCamera.setText("Right Camera Set");
                }
                catch (Exception e){
                    System.out.println("No directory selected.");
                }
            }
        });

        easySetLeftCamera.setDisable(true);
        easySetRightCamera.setDisable(true);

        usingCameras.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                camerasConnected = usingCameras.isSelected();
                if(camerasConnected){
                    easySetLeftCamera.setDisable(false);
                    easySetRightCamera.setDisable(false);
                }else{
                    easySetLeftCamera.setDisable(true);
                    easySetRightCamera.setDisable(true);
                }
            }
        });
    }

    // For the easy tab of the GUI
    void easyTab(){
        //Handles event of "Download and Create PDF" button
        easyCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //If the current directory has been set, do the scanning; otherwise, show error message
                if(!(currentDirectory == null)){
                    easyCreate.setText("Processing...");
                    appendLog("Processing...\n");
                    stitch();
                    if(camerasConnected){
                        importFromCameras();
                    }
                    scanTail();
                    imageMagick();
                    tesseract();
                    cleanDirectory();
                    if(camerasConnected){
                        //deleteFromCameras();
                    }
                    appendLog("Done\n");
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please chose a place to scan to.", ButtonType.OK);
                    alert.showAndWait();
                }
                easyCreate.setText("Download and Create PDF");
            }
        });
    }

    void stitch(){
        try {
            ArrayList<String> leftNames = new ArrayList<String>();
            ArrayList<String> rightNames = new ArrayList<String>();
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            File leftFolder = new File(fileOfDirectory + "left");
            File rightFolder = new File(fileOfDirectory + "right");

            File[] leftFiles = leftFolder.listFiles();
            File[] rightFiles = rightFolder.listFiles();

            for(int j = 0; j < leftFiles.length; j++){
                if(leftFiles[j].isFile()){
                    leftNames.add(leftFiles[j].getName());
                }
            }

            for(int j = 0; j < rightFiles.length; j++){
                if(rightFiles[j].isFile()){
                    rightNames.add(rightFiles[j].getName());
                }
            }

            BufferedImage[] outImages = new BufferedImage[Math.min(leftNames.size(), rightNames.size())];
            int imCount = 0;

            for(int i = 0; i < Math.min(leftNames.size(), rightNames.size()); i++){
                System.out.println(leftNames.get(i) + " + " + rightNames.get(i));

                String leftPath = fileOfDirectory + "left";
                String rightPath = fileOfDirectory + "right";
                String outFile = fileOfDirectory + "pageBlock" + i + ".jpg";

                if(os.contains("win")){
                    leftPath = leftPath + "\\";
                    rightPath = rightPath + "\\";
                }else{
                    leftPath = leftPath + "/";
                    rightPath = rightPath + "/";
                }

                leftPath += leftNames.get(i);
                rightPath += rightNames.get(i);

                int imagesCount = 4;
                BufferedImage images[] = new BufferedImage[2];
                images[0] = ImageIO.read(new File(leftPath));
                images[1] = ImageIO.read(new File(rightPath));

                int widthTotal = 0;
                for(int j = 0; j < images.length; j++) {
                    widthTotal += images[j].getWidth();
                }

                int widthCurr = 0;
                BufferedImage concatImage = new BufferedImage(widthTotal, Math.max(images[0].getHeight(),images[1].getHeight()), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = concatImage.createGraphics();
                for(int j = 0; j < images.length; j++) {
                    g2d.drawImage(images[j], widthCurr, 0, null);
                    widthCurr += images[j].getWidth();
                }
                g2d.dispose();

                //ImageIO.write(concatImage, "jpg", new File(outFile)); // export concat image
                outImages[imCount++] = concatImage;
            }

            ImageWriter writer = ImageIO.getImageWritersByFormatName("TIFF").next();
            File stream = new File(fileOfDirectory + "raw_pages.tiff");

            try (ImageOutputStream output = ImageIO.createImageOutputStream(stream)) {
                writer.setOutput(output);

                ImageWriteParam params = writer.getDefaultWriteParam();
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                // Compression: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT variants allowed
                // (different plugins may use a different set of compression type names)
                params.setCompressionType("LZW");

                writer.prepareWriteSequence(null);

                for (BufferedImage image : outImages) {
                    writer.writeToSequence(new IIOImage(image, null, null), params);
                }

                // We're done
                writer.endWriteSequence();
            }

            writer.dispose();
        }
        catch (Exception e){
            appendLog("Failed stitching photos with Imagemagick\n");
            appendLog("" + e.getMessage());
            e.printStackTrace();
            System.out.println("Failed Imagemagick");
        }
    }

    // Does the scanner, using the auto scan script
    void scanTail(){
        try{
            if(easyProjectName.getCharacters().toString().length() > 0)
                projectName = easyProjectName.getCharacters().toString();
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

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
                "--output-project=" + fileOfDirectory + projectName + ".ScanTailor",
                fileOfDirectory + "raw_pages.tiff",
                fileOfDirectory
            };

            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if(os.contains("win")){
                command = new String[]{"C:\\Program Files\\Scan Tailor\\scantailor-cli.exe",
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
                        "--output-project=" + fileOfDirectory + projectName + ".ScanTailor",
                        fileOfDirectory + "raw_pages.tiff",
                        fileOfDirectory
                };
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            int outCode = process.waitFor();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
            }

            System.out.println("Finished ScanTailor: " + outCode);
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
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            System.out.println("Starting Imagemagick...");
            appendLog("Starting Imagemagick...");

            String[] command = new String[]{"convert", fileOfDirectory + "*.tif", fileOfDirectory + "output.tiff"};

            if(os.contains("win")){
                command = new String[]{"magick", "convert", fileOfDirectory + "*.tif", fileOfDirectory + "output.tiff"};
                String[] newCommand = {"cmd.exe", "/c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
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

    void convertPDF(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            System.out.println("Starting Imagemagick...");
            appendLog("Starting Imagemagick...");

            String[] command = new String[]{"convert", fileOfDirectory + "output.tiff", fileOfDirectory + projectName + ".pdf"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
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

    void convertTXT(){
        try {
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Starting Tesseract...");
            appendLog("Starting Tesseract...");

            String[] command = new String[]{"tesseract", fileOfDirectory + "output.tiff", fileOfDirectory + projectName, "txt"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
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

    void tesseract(){
        try {
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Starting Tesseract...");
            appendLog("Starting Tesseract...");

            String[] command = new String[]{"tesseract", fileOfDirectory + "output.tiff", fileOfDirectory + projectName, "-l", "eng", "pdf"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
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

    void cleanDirectory(){
        try {
            //Sets file string to current directory
            String fileOfDirectory = currentDirectory.toString() + "/";
            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Cleaning...");
            appendLog("Cleaning...");

            String[] command = new String[]{"rm", fileOfDirectory + "*.tif"};
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

            command = new String[]{"sh", "-c", "rm", fileOfDirectory + "*.tiff"};
            pb = new ProcessBuilder(command);
            process = pb.start();

            //Re-direct output of process to console
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();

            command = new String[]{"rm", "-r", fileOfDirectory + "cache"};
            pb = new ProcessBuilder(command);
            process = pb.start();

            //Re-direct output of process to console
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();

            appendLog("Finished\n");
        }
        catch (Exception e){
            appendLog(e.getMessage());
        }
    }

    // For the medium tab of the GUI
    void mediumTab(){
        mediumColorMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(mediumColorMode.getValue());
                if(mediumColorMode.getValue().equals("Text and Photographs")){
                    //mediumCreatePDF.setDisable(true);
                    colorMode = "mixed";
                    hardColorMode.setValue("Text and Photographs");
                }
                else if(mediumColorMode.getValue().equals("Text and Line Drawings Only")){
                    //mediumCreatePDF.setDisable(false);
                    colorMode = "black_and_white";
                    hardColorMode.setValue("Text and Line Drawings Only");
                }
                else if(mediumColorMode.getValue().equals("Full Photographs")) {
                    //mediumCreatePDF.setDisable(false);
                    colorMode = "color_grayscale";
                    hardColorMode.setValue("Full Photographs");
                }
            }
        });

        mediumUseOCR.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                useOCRMedium = mediumUseOCR.isSelected();
            }
        });

        mediumTxtOutput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                txtOutputMedium = mediumTxtOutput.isSelected();
            }
        });

        mediumCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediumCreatePDF.setDisable(true);
                mediumCreatePDF.setText("Processing!");
                appendLog("Processing...\n");

                if(camerasConnected){
                    importFromCameras();
                    deleteFromCameras();
                }

                scanTail();
                imageMagick();
                if(useOCRMedium){
                    tesseract();
                }else{
                    convertPDF();
                }

                if(txtOutputMedium){
                    convertTXT();
                }

                mediumCreatePDF.setText("Download and Create PDF!");
                mediumCreatePDF.setDisable(false);
            }
        });
    }

    void downloadFromCamera(String usbAddress, String subDir){
        try{
            String[] command = new String[]{"mkdir", "" + currentDirectory + subDir};

            String os = System.getProperty("os.name").toLowerCase();
            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
            }

        }
        catch(Exception e){
            System.out.println("Created dir");
        }


        try {
            System.out.println("" + usbAddress);
            System.out.println("" + currentDirectory + subDir);
            String[] command = new String[]{"cp", usbAddress + "/*", "" + currentDirectory + subDir};

            String os = System.getProperty("os.name").toLowerCase();
            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if(os.contains("win") ){
                command = new String[]{"xcopy", "/f", usbAddress + "/*", "" + currentDirectory + subDir};
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
            }
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Application was unable to import from cameras", ButtonType.OK);
            alert.showAndWait();
            appendLog("Error importing from \n");
        }
    }

    void importFromCameras(){
        if(leftCamDirectory == null || rightCamDirectory == null){
            return;
        }
        downloadFromCamera("" + leftCamDirectory, "/left");
        downloadFromCamera("" + rightCamDirectory, "/right");
    }

    void deleteFromCameras(){
        try{
            String[] command = new String[]{"rm", "" + leftCamDirectory + "/*"};

            String os = System.getProperty("os.name").toLowerCase();
            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if(os.contains("win") ){
                command = new String[]{"del", "" + leftCamDirectory + "\\*"};
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            //Re-direct output of process to console
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            BufferedReader otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
            }

            process.waitFor();

            command = new String[]{"rm", "" + rightCamDirectory + "/*"};

            os = System.getProperty("os.name").toLowerCase();
            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if(os.contains("win") ){
                command = new String[]{"del", "" + rightCamDirectory + "\\*"};
            }

            pb = new ProcessBuilder(command);
            process = pb.start();

            //Re-direct output of process to console
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                appendLog(line + "\n");
            }

            process.waitFor();
            System.out.println("Done deleting");
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Application was unable to delete from cameras", ButtonType.OK);
            alert.showAndWait();
            appendLog("Error deleting\n");
        }
    }

    // For the hard tab of the GUI
    void hardTab() {
        hardImport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardImport.setText("Importing!");
                hardDelete.setDisable(true);
                importFromCameras();
                hardDelete.setDisable(false);
                hardImport.setText("Download Images from Cameras");
            }
        });

        hardDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete contents on camera??", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    hardDelete.setText("Deleting!");
                    deleteFromCameras();
                    hardDelete.setText("Delete Images From Cameras");
                }
            }
        });

        hardScan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardScan.setText("Processing!");
                margins = "" + hardMargins.getValue();
                scanTail();
                hardRunScanTailor.setDisable(false);
                hardCreatePDF.setDisable(false);
            }
        });

        hardRunScanTailor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardRunScanTailor.setText("Opening!");
                String fileOfDirectory = currentDirectory.toString() + "/";
                try {
                    String[] command = new String[]{"scantailor", fileOfDirectory + projectName + ".ScanTailor"};
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
                }catch(Exception e){
                    hardRunScanTailor.setText("Error opening");
                }
            }
        });

        hardCreatePDF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardCreatePDF.setText("Creating!");
                imageMagick();
                if(useOCRHard){
                    tesseract();
                }else{
                    convertPDF();
                }

                if(txtOutputHard){
                    convertTXT();
                }
            }
        });

        hardLayoutOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(hardLayoutOption.getValue());
                if(hardLayoutOption.getValue().equals("Auto Detect")){
                    //mediumCreatePDF.setDisable(true);
                    layout = "0";
                }
                else if(hardLayoutOption.getValue().equals("One Page")){
                    //mediumCreatePDF.setDisable(false);
                    layout = "1";
                }
                else if(hardLayoutOption.getValue().equals("Two Page")){
                    //mediumCreatePDF.setDisable(false);
                    layout = "2";
                }
            }
        });

        hardColorMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(hardColorMode.getValue());
                if(hardColorMode.getValue().equals("Text and Photographs")){
                    //mediumCreatePDF.setDisable(true);
                    colorMode = "mixed";
                    mediumColorMode.setValue("Text and Photographs");
                }
                else if(hardColorMode.getValue().equals("Text and Line Drawings Only")){
                    //mediumCreatePDF.setDisable(false);
                    colorMode = "black_and_white";
                    mediumColorMode.setValue("Text and Line Drawings Only");
                }
                else if(hardColorMode.getValue().equals("Full Photographs")) {
                    //mediumCreatePDF.setDisable(false);
                    colorMode = "color_grayscale";
                    mediumColorMode.setValue("Full Photographs");
                }
            }
        });

        hardOrientation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(hardOrientation.getValue());
                if(hardOrientation.getValue().equals("Left")){
                    //mediumCreatePDF.setDisable(true);
                    orientation = "left";
                }
                else if(hardOrientation.getValue().equals("Right")){
                    //mediumCreatePDF.setDisable(false);
                    orientation = "right";
                }
                else if(hardOrientation.getValue().equals("Upsidedown")){
                    //mediumCreatePDF.setDisable(false);
                    orientation = "upsidedown";
                }
            }
        });

        hardUseOCR.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                useOCRHard = hardUseOCR.isSelected();
            }
        });

        hardTxtOutput.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                txtOutputHard = hardTxtOutput.isSelected();
            }
        });
    }
}
