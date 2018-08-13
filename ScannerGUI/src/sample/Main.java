package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setTitle("A Scanner Darkly");
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
