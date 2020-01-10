package Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;

//Main application for the scanner GUI
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            // Loads FXML from file
            Parent root = FXMLLoader.load(getClass().getResource("Scanner.fxml"));
            //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Scanner.fxml"));
            primaryStage.getIcons().add(new Image("file:icon.png"));
            //Sets title of window
            primaryStage.setTitle("A Scanner Darkly");
            //Sets window dimensions
            primaryStage.setScene(new Scene(root, 600, 500));
            primaryStage.setResizable(false);
            //Show window
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // On start, launch the app
    public static void main(String[] args) {
        launch(args);
    }
}
