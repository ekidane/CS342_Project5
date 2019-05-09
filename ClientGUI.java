package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class driverClass extends Application {

    TextField text;
    Button button;
    Stage myStage;
    Scene scene, scene2;
    HashMap<String, Scene> sceneMap;

    public static void main(String[] args) {

        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Get ready to guess");

        myStage = primaryStage;

        button = new Button("Guess!");

        button.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

            }
        });

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(60));

        VBox paneCenter = new VBox(15, text, button);

        pane.setCenter(paneCenter);

        scene = new Scene(pane, 400, 500);
        scene2 = new Scene(paneCenter, 200, 300);

        sceneMap.put("Guessing Game", scene);
        sceneMap.put("Where To Play", scene2);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}