//package sample;

//so many imports!
import javafx.application.Application;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Random;
import java.util.function.Consumer;

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
    int numberGuesses = 0;

    //number of active players i.e number of players in a game I think I might need this somewhere
    //when we start the game set numActivePlayers equal to numPlayers
    static int numActivePlayers = 0;

    //textfield that tells us the number of clients connected
    static Text clientsConnected;

    //function that updates the GUI
    Consumer<String> updateGUI;

    public void start(Stage primaryStage) {

        //we are defining the updateGUI function
        updateGUI = textSend -> {
            //if someone won the game or if everyone has guessed then we enable the button to be play
            if(textSend.contains("win") || numberGuesses == numActivePlayers){
                //enable a button to be pushed which starts the game if we have more than four players
                //probably say something like the client won
                //reset the number of guesses
                numberGuesses = 0;
                //get a new random number
                randomNumber = rand.nextInt() % 200;
            }
            //probably right here put some text in the server saying what the next hint will be based on current hint
            else{
                //update hint text in the server
                //increase number of guesses
                ++numberGuesses;
            }
        };

        //pass the function that update the text to the serverThread
        myServer = new serverThread(updateGUI);

        activatingServer();
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

    public static void main(String[] args) {
        launch(args);
    }

}//end of application