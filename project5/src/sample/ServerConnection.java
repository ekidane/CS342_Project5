
package sample;

//so many imports!
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;
import javafx.scene.media.AudioClip;

//I likely just need to throw in the things for the client to see stuff and we are gucci
public class ServerConnection extends Application {

    //this is the server we are going to be using maybe I should be making this an array or something
    serverThread myServer ;

    //portNumber of the server
    int portNumber = 5555;

    //doing this to get a random number apparently the seed is different every time?
    static Random rand = new Random();

    //the number we are going to be dealing with
    static int randomNumber = rand.nextInt() % 200;
    //We are going to use this to determine which hint they give
    static int whichHint = rand.nextInt() % 4;
    static int previousHint = 0;

    //number of guesses made
    static int numberGuesses = 0;

    //number of active players i.e number of players in a game I think I might need this somewhere
    //when we start the game set numActivePlayers equal to numPlayers
    static int numActivePlayers = 0;

    //textfield that tells us the number of clients connected
    static Text clientsConnected;
    Text hintText ;
    Button startGame;

    //function that updates the GUI
    Consumer<String> updateGUI;
    AudioClip neatMusic;
    Stage myStage ;

    public void start(Stage primaryStage) throws Exception{

        clientsConnected = new Text("Here we show when a client has connected");

        myStage = primaryStage;
        //on button press we do the following
        neatMusic = new AudioClip(this.getClass().getResource("neatMusic.mp3").toString());
        neatMusic.play();

        hintText = new Text((whichHintText()));
        //should in theory work! I have no idea why it doesn't
        startGame = new Button("Push this to start the game!");

        //event handler for the button
        startGame.setOnAction(event -> {
            if(serverThread.numOfPlayers >= 4){
                for(ConnThread randThread: serverThread.connections){
                    try {
                        randThread.out.writeObject("game");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //send game start to every one
                startGame.setDisable(true);
            }
        });

        //we are defining the updateGUI function
        updateGUI = textSend -> {
            //if someone won the game or if everyone has guessed then we enable the button to be play
            if(textSend.contains("win") ){
                hintText.setText("The clients have won uwu");
                startGame.setDisable(false);
                //enable a button to be pushed which starts the game if we have more than four players
                //probably say something like the client won
                //reset the number of guesses
                numberGuesses = 0;
                //get a new random number
                randomNumber = rand.nextInt() % 200;
            }
            else if( numberGuesses == numActivePlayers){
                hintText.setText("The clients have lose ahahahha");
                startGame.setDisable(false);
                //enable a button to be pushed which starts the game if we have more than four players
                //probably say something like the client won
                //reset the number of guesses
                numberGuesses = 0;
                //get a new random number
                randomNumber = rand.nextInt() % 200;
            }
            //probably right here put some text in the server saying what the next hint will be based on current hint
            else{
                hintText.setText(whichHintText());
                ++numberGuesses;
            }
        };

        //pass the function that update the text to the serverThread
        myServer = new serverThread(updateGUI);

        BorderPane pane = new BorderPane(hintText);
        pane.setBottom(clientsConnected);
        //should work because there is no reason it shouldn't
        pane.setTop(startGame);

        activatingServer();
        myStage.setScene(new Scene(pane,500,500));
        myStage.show();
    }

    //when they push a button and there are more than 4 players
    //we play the game.
    //numActivePlayers is now equal to numPlayers
    //we show a hint that is based on what the hint is

    void activatingServer() {
        //turning on the server with a given port number
        myServer.portNumber = portNumber;
        myServer.start();

    }//the end of the function  that has the button handler

    //when we close the gui we call letsClose to close everything
    @Override
    public void stop(){
        myServer.letsClose();
    }

    String whichHintText(){
        if(whichHint == 0){
            return "Telling client if the server is divisible by the server number!";
        }
        else if(whichHint == 1){
            return "Telling client if the sum of the digits of their number is more or less than the server number!";
        }
        else if(whichHint == 2){
            return "Telling client if the server number is smaller or bigger than their guess!";
        }
        else {
            return "Telling client how many digits match in their guess to the server number!";
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
