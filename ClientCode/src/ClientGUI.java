//package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.function.Consumer;

public class ClientConnection extends Application {

    //instance of the client class
    Client myClient;

    TextField text;
    Button button;
    Stage myStage;
    Scene scene;
    Text hintText;
    HashMap<String, Scene> sceneMap;
    boolean inGame = false;

    //funciton we will use to update gui
    Consumer<Serializable> updateGUI;

    public void start(Stage primaryStage) throws Exception {

        //initializing hashmap
        sceneMap = new HashMap();

        //function that we send for the gui
        updateGUI = x -> {
          if( x instanceof String){
              String message = (String)x;
              //if we receieve a win from the server then we won
              if(message.contains("win")){
                  inGame = false;
                  hintText.setText( "YOu won the game congratulations Winner ;) winky face");
              }
              //if we receive a lose from the serve then we lose
              else if(message.contains("lose")){
                  inGame = false;
                  hintText.setText("You lost the game loser hahahaha pathetic");
              }
              //game starting message
              else if(message.contains("game")){
                  inGame = true;
                  button.setDisable(false);
              }
              //update the message based on what the message says
              else{
                  hintText.setText(hintText.getText() + "\n" + message);
              }
          }
        };

        //setting the client equal to something that way we actually do something
        myClient = new Client("127.0.0.1",5555,updateGUI);

        //creating our stage that way we can do our stuff
        myStage = primaryStage;

        //setting the title of our stage
        myStage.setTitle("Get ready to guess");

        button = new Button("Guess!");
        text = new TextField();
        hintText = new Text("We still haven't started the game! Make some friends!");

        button.setOnAction(event -> {
            //if we are in the game we enter this statement
            if(inGame){
                button.setDisable(true);
                //calling this function creates an exception so I guess we surround it with try catch
                //here we are sending the guess
                try {
                    myClient.send(text.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                //we are not in the game so we literally do nothing
                //do nothing
            }
            //here we should say we did something
            //if we are in a game then we do something otherwise we don't
        });

        //I am guessing this starts the thread and begins listening to our man below
        myClient.startConn();

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(60));

        VBox paneCenter = new VBox(15, hintText,text, button);

        pane.setCenter(paneCenter);

        scene = new Scene(pane, 400, 500);

        sceneMap.put("Guessing Game", scene);

        myStage.setScene(scene);
        myStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    //when we close the gui I think we should call the closeConn function

}
