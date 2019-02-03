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
import java.awt.geom.AffineTransform;

//import javax.imageio.ImageIO;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

import java.text.*;



public class Controller {
    File currentDirectory = null;
    File leftCamDirectory = null;
    File rightCamDirectory = null;
    File defaultDirectory;
    String projectName = "Project";
    File extendedDirectory = null;

    String layout = "0";
    String layoutDirection = "lr";
    String orientation = "left";
    String rotate = "0.0";
    String deskew = "auto";
    String contentDirection = "normal";
    String margins = "8";
    String alignment = "center";
    String dpi = "300";
    String outputDpi = "600";
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

    boolean running = false;

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
        //hardRunScanTailor.setDisable(true);
        //hardCreatePDF.setDisable(true);
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

    private void runOperation(String op) {
        if(running){
            return;
        }
        running = true;

        Task<Void> taskLoad = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                loadAnimate();
                return null;
            }
        };
        //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
        (new Thread(taskLoad)).start();

        try {
            if (op.equals("easy")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        importFromCameras();
                        //NOTE: MAY NEED TO UNCOMMENT THIS LINE
                        if (camerasConnected) {
                            rotateImages();
                        }
                        sideStitch();
                        scanTail();
                        //tailorStitch();
                        tesseract();
                    /*if(camerasConnected){
                        //deleteFromCameras();
                    }*/
                        appendLog("Done\n");
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            } else if (op.equals("medium")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        //makeDirectories();
                        importFromCameras();
                        if (camerasConnected) {
                            rotateImages();
                            //deleteFromCameras();
                        }
                        sideStitch();
                        scanTail();
                        //tailorStitch();
                        if (useOCRMedium) {
                            tesseract();
                        } else {
                            convertPDF();
                        }

                        if (txtOutputMedium) {
                            convertTXT();
                        }

                        mediumCreatePDF.setText("Download and Create PDF!");
                        mediumCreatePDF.setDisable(false);
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            } else if (op.equals("import")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        importFromCameras();
                        hardDelete.setDisable(false);
                        hardImport.setText("Download Images from Cameras");
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            } else if (op.equals("delete")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        deleteFromCameras();
                        hardDelete.setText("Delete Images From Cameras");
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            } else if (op.equals("stitchAndTailor")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        sideStitch();
                        scanTail();
                        hardRunScanTailor.setDisable(false);
                        hardCreatePDF.setDisable(false);
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            } else if (op.equals("ocr")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        //tailorStitch();
                        if (useOCRHard) {
                            tesseract();
                        } else {
                            convertPDF();
                        }

                        if (txtOutputHard) {
                            convertTXT();
                        }
                        running = false;
                        return null;
                    }
                };
                //task.messageProperty().addListener((obs, oldMessage, newMessage) -> label.setText(newMessage));
                (new Thread(task)).start();
            }
        }catch (Exception e){
            running = false;
        }
        //todo: make all operations work multithreading
    }

    void appendLog(String s){
        s = s.trim() + "\n";
        try {
            final String txt = s;
            javafx.application.Platform.runLater( () -> easyLog.appendText(txt) );
            javafx.application.Platform.runLater( () -> normalLog.appendText(txt) );
            javafx.application.Platform.runLater( () -> expertLog.appendText(txt) );
        }catch(Exception e){
            System.out.println("Error appending text");
        }
    }

    void loadAnimate(){
        final String[] states = new String[]{"Loading...-",
                "Loading...\\",
                "Loading...|",
                "Loading.../",
                "Loading...-",
                "Loading...\\",
                "Loading...|"
        };
        int loadIndex = 0;
        try {
            while (running) {
                final String thisLoad = states[loadIndex];
                javafx.application.Platform.runLater(() -> easyCreate.setText(thisLoad));
                javafx.application.Platform.runLater(() -> mediumCreatePDF.setText(thisLoad));

                loadIndex = (loadIndex + 1) % states.length;
                Thread.sleep(300);
            }
        }catch (Exception e){
            System.out.println("Error waiting");
        }
        javafx.application.Platform.runLater( () -> easyCreate.setText("Download and Create PDF"));
        javafx.application.Platform.runLater( () -> mediumCreatePDF.setText("Download and Create PDF"));
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
                    //makeDirectories();
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

        usingCameras.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                camerasConnected = usingCameras.isSelected();
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
                if(!(currentDirectory == null) && !running){
                    easyCreate.setText("Processing...");
                    appendLog("Processing...\n");
                    runOperation("easy");
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please chose a place to scan to.", ButtonType.OK);
                    alert.showAndWait();
                }
                easyCreate.setText("Download and Create PDF");
            }
        });
    }

    void makeDirectories(){
        try {
            Date date = new Date();
            String strDateFormat = "_yyyy-MM-dd_hh-mm-ss-a";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate = dateFormat.format(date);

            if(easyProjectName.getCharacters().toString().length() > 0)
                projectName = easyProjectName.getCharacters().toString();

            String fileOfDirectory = currentDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                fileOfDirectory = fileOfDirectory + "\\" + projectName + formattedDate;
                extendedDirectory = new File(fileOfDirectory);
                fileOfDirectory = fileOfDirectory + "\\";
            } else {
                fileOfDirectory = fileOfDirectory + "/" + projectName + formattedDate;
                extendedDirectory = new File(fileOfDirectory);
                fileOfDirectory = fileOfDirectory + "/";
            }

            String leftDir = fileOfDirectory + "left";
            String rightDir = fileOfDirectory + "right";
            String comDir = fileOfDirectory + "combined";
            String tailDir = fileOfDirectory + "tailored";
            String pdfDir = fileOfDirectory + "pdf";

            //make left directory
            String[] command = new String[]{"mkdir", "" + fileOfDirectory.substring(0, fileOfDirectory.length()-1)};

            if (!(os.contains("win") || os.contains("osx"))) {
                String[] newCommand = {"/bin/bash", "-c", ""};
                for (String a : command) {
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if (os.contains("win")) {
                String[] newCommand = {"cmd", "/c", ""};
                for (String a : command) {
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            process.waitFor();

            command[2] = "mkdir " + leftDir;
            pb = new ProcessBuilder(command);
            process = pb.start();
            process.waitFor();

            command[2] = "mkdir " + rightDir;
            pb = new ProcessBuilder(command);
            process = pb.start();
            process.waitFor();

            command[2] = "mkdir " + comDir;
            pb = new ProcessBuilder(command);
            process = pb.start();
            process.waitFor();

            command[2] = "mkdir " + tailDir;
            pb = new ProcessBuilder(command);
            process = pb.start();
            process.waitFor();

            command[2] = "mkdir " + pdfDir;
            pb = new ProcessBuilder(command);
            process = pb.start();
            process.waitFor();

            appendLog("Made new directories at " + extendedDirectory.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void rotateImages(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String leftPath = fileOfDirectory;
            String rightPath = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
                leftPath += "\\left\\*";
                rightPath += "\\right\\*";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
                leftPath += "/left/*";
                rightPath += "/right/*";
            }

            System.out.println("Starting Imagemagick rotations...");
            appendLog("Rotating images...");

            String[] command = new String[]{"mogrify", "-rotate", "-90", leftPath};

            if(os.contains("win")){
                command = new String[]{"magick", "mogrify", "-rotate", "-90", leftPath};
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

            command = new String[]{"mogrify", "-rotate", "90", rightPath};

            if(os.contains("win")){
                command = new String[]{"magick", "mogrify", "-rotate", "90", rightPath};
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

            otherReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            line = null;
            while ((line = otherReader.readLine()) != null) {
                System.out.println(line);
                appendLog(line);
            }

            System.out.println("Finished Imagemagick");
            appendLog("Finished rotations\n");
        }
        catch (Exception e){
            appendLog("Failed Imagemagick rotation\n");
            appendLog(e.getMessage());
            System.out.println("Failed Imagemagick");
        }
    }

    void sideStitch(){
        try {
            appendLog("Stitching paired images together...\n");
            System.out.println("Stitching.");
            ArrayList<BufferedImage> leftImages = new ArrayList<BufferedImage>();
            ArrayList<BufferedImage> rightImages = new ArrayList<BufferedImage>();
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
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

            TreeMap<String,File> leftMap = new TreeMap<String,File>();
            TreeMap<String,File> rightMap = new TreeMap<String,File>();

            for(int j = 0; j < leftFiles.length; j++){
                if(leftFiles[j].isFile()){
                    //leftImages.add(ImageIO.read(leftFiles[j]));
                    leftMap.put(leftFiles[j].getName(), leftFiles[j]);
                    //System.out.println(leftFiles[j].getName());
                }
            }

            for(int j = 0; j < rightFiles.length; j++){
                if(rightFiles[j].isFile()){
                    //rightImages.add(ImageIO.read(rightFiles[j]));
                    rightMap.put(rightFiles[j].getName(), rightFiles[j]);
                    //System.out.println(rightFiles[j].getName());
                }
            }

            leftFiles = Arrays.copyOf(leftMap.values().toArray(), leftMap.values().toArray().length, File[].class);
            rightFiles = Arrays.copyOf(rightMap.values().toArray(), rightMap.values().toArray().length, File[].class);

            /*
            for(int j = 0; j < leftFiles.length; j++){
                if(leftFiles[j].isFile()){
                    leftImages.add(ImageIO.read(leftFiles[j]));
                    System.out.println(leftFiles[j].getName());
                }
            }

            for(int j = 0; j < rightFiles.length; j++){
                if(rightFiles[j].isFile()){
                    rightImages.add(ImageIO.read(rightFiles[j]));
                    System.out.println(rightFiles[j].getName());
                }
            }*/

            fileOfDirectory = fileOfDirectory + "combined";

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            //BufferedImage[] outImages = new BufferedImage[Math.min(leftNames.size(), rightNames.size())];
            int imCount = 0;

            for(int i = 0; i < Math.min(leftFiles.length, rightFiles.length); i++){
                String numberName = "" + i;
                while(numberName.length() < 4){
                    numberName = "0" + numberName;
                }

                String outFile = fileOfDirectory + "pageBlock" + numberName + ".jpg";
                System.out.println(outFile);
                appendLog(outFile);

                int imagesCount = 4;
                BufferedImage images[] = new BufferedImage[2];
                images[0] = ImageIO.read(leftFiles[i]);
                images[1] = ImageIO.read(rightFiles[i]);

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

                ImageIO.write(concatImage, "jpg", new File(outFile)); // export concat image
                //outImages[imCount++] = concatImage;
            }
            System.out.println("Finished stitch");
            appendLog("Finished stitching paired images.\n");
            /**/
        }
        catch (Exception e){
            appendLog("Failed stitching photos with Imagemagick\n");
            appendLog("" + e.getMessage());
            e.printStackTrace();
            System.out.println("Failed Imagemagick");
        }
    }

    void tailorStitch(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String dest = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory += "\\tailored\\*.tif";
                dest += "\\tailored\\combined_pages.tiff";
            }else{
                fileOfDirectory += "/tailored/*.tif";
                dest += "/tailored/combined_pages.tiff";
            }

            System.out.println("Starting Imagemagick conversions...");
            appendLog("Converting .tifs to single .tiff...");

            String[] command = new String[]{"convert", "-limit", "memory", "32MiB", "-limit", "map", "64MiB", fileOfDirectory, dest};
            if(os.contains("win")){
                command = new String[]{"magick", "convert", "-limit", "memory", "32MiB", "-limit", "map", "64MiB", fileOfDirectory, dest};
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
            appendLog("Finished conversion\n");
        }
        catch (Exception e){
            appendLog("Failed Imagemagick conversion\n");
            appendLog(e.getMessage());
            System.out.println("Failed Imagemagick");
        }
    }

    void configureTesserectSource(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String dest = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory += "\\tailored\\";
                dest += "\\tailored\\combined_pages.txt";
            }else{
                fileOfDirectory += "/tailored/";
                dest += "/tailored/combined_pages.txt";
            }

            appendLog("Saving file names to txt...");

            File sourceFolder = new File(fileOfDirectory);
            File[] combinedFiles = sourceFolder.listFiles();
            String[] fileNames = new String[combinedFiles.length];

            for(int i = 0; i < combinedFiles.length; i++){
                fileNames[i] = combinedFiles[i].getPath();
            }

            Arrays.sort(fileNames);

            PrintWriter writer = new PrintWriter(new File(dest));
            for(String nm : fileNames){
                writer.println(nm);
            }
            writer.flush();
            writer.close();

            System.out.println("Saved txt.");
            appendLog("Saved txt.\n");
        }
        catch (Exception e){
            appendLog("Failed Imagemagick conversion\n");
            appendLog(e.getMessage());
            System.out.println("Failed Imagemagick");
        }
    }

    // Does the scanner, using the auto scan script
    void scanTail(){
        try{
            String leftPath = "left";
            String rightPath = "right";

            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            File combinedFolder = new File(fileOfDirectory + "combined");
            File[] combinedFiles = combinedFolder.listFiles();

            if(easyProjectName.getCharacters().toString().length() > 0)
                projectName = easyProjectName.getCharacters().toString();

            System.out.println("Starting ScanTailor...");
            appendLog("Starting ScanTailor...");

            ArrayList<String> commands = new ArrayList<String>();

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
                "--output-project=" + fileOfDirectory + projectName + ".ScanTailor"
            };

            /*if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }

                newCommand[2] = newCommand[2].substring(0, newCommand[2].length()-1);

                command = newCommand;
            }*/

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
                        "--output-project=" + fileOfDirectory + projectName + ".ScanTailor"
                };
            }

            for(String s : command){
                commands.add("" + s);
            }

            for(File f : combinedFiles){
                commands.add("" + f.getPath());
            }

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "tailored\\";
            }else{
                fileOfDirectory = fileOfDirectory + "tailored/";
            }

            commands.add(fileOfDirectory);

            command = new String[commands.size()];

            for(int j = 0; j < commands.size(); j++){
                command[j] = "" + commands.get(j);
                System.out.print(commands.get(j) + " ");
            }

            System.out.println();

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

    void convertPDF(){
        try{
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String inputImages = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
                inputImages += "\\tailored\\combined_pages.tiff";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
                inputImages += "/tailored/combined_pages.tiff";
            }

            System.out.println("Starting Imagemagick...");
            appendLog("Converting to PDF...");

            String[] command = new String[]{"convert", inputImages, fileOfDirectory + projectName + ".pdf"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            if(os.contains("win")){
                command = new String[]{"C:\\Program Files\\ImageMagick-7.0.8-Q16\\magick.exe", "convert", inputImages, fileOfDirectory + projectName + ".pdf"};
            }

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
            appendLog("Finished convert to PDF\n");
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
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();

            String inputImages = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
                inputImages += "\\tailored\\combined_pages.tiff";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
                inputImages += "/tailored/combined_pages.tiff";
            }

            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Starting Tesseract...");
            appendLog("Starting txt generation with Tesseract...");

            String[] command = new String[]{"tesseract", inputImages, fileOfDirectory + projectName, "txt"};

            if(os.contains("win")){
                command[0] = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract";
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
            System.out.println("Finished Tesseract");
            appendLog("Finished txt\n");
        }
        catch (Exception e){
            appendLog("Failed tesseract\n");
            appendLog(e.getMessage());
            System.out.println("Failed tesseract");
        }
    }

    void tesseract(String path, String fileName){
        try {
            System.out.println("Tesseract: " + fileName);
            System.out.println(path);
            appendLog("Tesseract: " + fileName);
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\pdf\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/pdf/";
            }

            String[] command = new String[]{"tesseract", path, fileOfDirectory + fileName, "-l", "eng", "pdf"};

            if(os.contains("win")){
                command[0] = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract";
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
        }
        catch (Exception e) {
            appendLog("Failed tesseract\n");
            appendLog(e.getMessage());
            System.out.println("Failed tesseract");
        }
    }

    void pdfMerge(File[] list, String path){
        try {
            String[] command = new String[]{"gs", "-dBATCH", "-dNOPAUSE", "-q", "-sDEVICE=pdfwrite", "-sOutputFile=" + path};
            ArrayList<String> cl = new ArrayList<String>();

            for(int i = 0; i < command.length; i++){
                cl.add(command[i]);
            }

            for(int i = 0; i < list.length; i++){
                cl.add(list[i].getPath());
            }

            command = new String[cl.size()];
            for(int i = 0; i < command.length; i++){
                command[i] = cl.get(i);
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
        }catch(Exception e){
            appendLog("Failed pdf merge\n");
            appendLog(e.getMessage());
            e.printStackTrace();
            System.out.println("Failed pdf merge");
        }
    }

    //New method: tesseract each tif individually - combining pdfs in the end
    void tesseract(){
        try {
            System.out.println("Starting tesseract...");
            appendLog("Starting tesseract...");
            appendLog("Running OCR on each page separately.");
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String tailFolder = "";
            String pdfFolder = "";

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
            }

            tailFolder = fileOfDirectory + "tailored";
            pdfFolder = fileOfDirectory + "pdf";

            //Get list of files in tailored folder
            File tails = new File(tailFolder);
            File[] listOfTails = tails.listFiles();

            //call tesseract(path, name) for each file
            for(int i = 0; i < listOfTails.length; i++){
                tesseract(listOfTails[i].getPath(), listOfTails[i].getName().substring(0,listOfTails[i].getName().lastIndexOf(".")));
            }

            System.out.println("Finished OCR");
            appendLog("Finished OCR");
            System.out.println("Merging PDFs...");
            appendLog("Merging PDFs...");

            //get list of pdfs in pdf folder
            File pdfs = new File(pdfFolder);
            File[] listOfPdfs = pdfs.listFiles();
            Arrays.sort(listOfPdfs);

            pdfMerge(listOfPdfs, fileOfDirectory + projectName + ".pdf");

        }
        catch (Exception e){
            appendLog("Failed tesseract\n");
            appendLog(e.getMessage());
            e.printStackTrace();
            System.out.println("Failed tesseract");
        }
    }

    //Original method: tesseract all the tifs into a pdf in one go
    /*
    void tesseract(){
        try {
            configureTesserectSource();
            //Sets file string to current directory
            String fileOfDirectory = extendedDirectory.toString();
            String os = System.getProperty("os.name").toLowerCase();
            String inputImages = fileOfDirectory;

            if(os.contains("win")){
                fileOfDirectory = fileOfDirectory + "\\";
                //inputImages += "\\tailored\\combined_pages.tiff";
                inputImages += "\\tailored\\combined_pages.txt";
            }else{
                fileOfDirectory = fileOfDirectory + "/";
                //inputImages += "/tailored/combined_pages.tiff";
                inputImages += "/tailored/combined_pages.txt";
            }

            //tesseract output.tiff outputOCR -l eng pdf
            System.out.println("Starting Tesseract...");
            appendLog("Starting OCR to create PDF with Tesseract...");

            String[] command = new String[]{"tesseract", inputImages, fileOfDirectory + projectName, "-l", "eng", "pdf"};

            if(os.contains("win")){
                command[0] = "C:\\Program Files (x86)\\Tesseract-OCR\\tesseract";
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
            System.out.println("Finished Tesseract");
            appendLog("Finished Tesseract\n");
        }
        catch (Exception e){
            appendLog("Failed tesseract\n");
            appendLog(e.getMessage());
            System.out.println("Failed tesseract");
        }
    }*/

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
                runOperation("medium");
            }
        });
    }

    void downloadFromCamera(String usbAddress, String subDir){
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win") ){
                subDir = "\\" + subDir;
            }else{
                subDir = "/" + subDir;
            }

            System.out.println("" + usbAddress);
            System.out.println("" + extendedDirectory+ subDir);
            String[] command = new String[]{"cp", usbAddress + "/*", "" + extendedDirectory + subDir};

            if(!(os.contains("win") || os.contains("osx"))){
                String[] newCommand = {"/bin/bash", "-c", ""};
                for(String a: command){
                    newCommand[2] += a + " ";
                }
                command = newCommand;
            }

            if(os.contains("win") ){
                command = new String[]{"xcopy", "/f", usbAddress + "\\*", "" + extendedDirectory + subDir};
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
        makeDirectories();
        downloadFromCamera("" + leftCamDirectory, "left");
        downloadFromCamera("" + rightCamDirectory, "right");
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
                //makeDirectories();
                runOperation("import");
            }
        });

        hardDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete contents on camera??", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    hardDelete.setText("Deleting!");
                    runOperation("delete");
                }
            }
        });

        hardScan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardScan.setText("Processing!");
                margins = "" + hardMargins.getValue();
                //makeDirectories();
                runOperation("stitchAndTailor");
            }
        });

        hardRunScanTailor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                hardRunScanTailor.setText("Opening!");
                String fileOfDirectory = extendedDirectory.toString() + "/";
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
                runOperation("ocr");
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
