//package sample;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Vector;
import java.util.function.Consumer;

public class ConnThread extends Thread {

    //each players "thread" through which they send information has all this
    Socket socket;
    //a unique number indicating what player they are
    int myPlayerNum;

    Consumer updateGUI;

    ObjectInputStream in;
    ObjectOutputStream out;

    ConnThread(Consumer function){
        updateGUI = function;
        socket = null;
        myPlayerNum = -1;
    }

    //we are in here and we have already been threaded so in here we need to
    //this is the server one
    public void run(){
        //putting the input and output streams in the parentheses that way they can close without me explicitly telling them to
        try {
            in = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);

            while (true) {
                //I can parse the string and I can work with just an int
                Object data = (Serializable) in.readObject().toString();

                //if we are here then we have received a guess from the client
                //I think we should receive a string here and then we send it to all active players
                String messageToEveryone = determiningPoints(data.toString());

                //update the gui based on what they sent to the client
                updateGUI.accept(messageToEveryone);

                //we send out a message to everyone and that message changes based on their guess
                for(int i = 0; i < ServerConnection.numActivePlayers ; ++i){
                    serverThread.connections.get(i).out.writeObject(messageToEveryone);
                }

            }//end of while loop
        }//end of try
        catch (Exception e) {
            e.printStackTrace();
        }
    }//end of run

    //figuring outcome of what opponent put in
    synchronized String determiningPoints(String myMove){

        //checkWinner returns true if opponent won and returns false if current player won
        int move = Integer.parseInt(myMove);
        String returnString = "hello";

        //doing certain actions depending on which hint the server happens to give
        if(ServerConnection.randomNumber != move) {
            switch (ServerConnection.whichHint) {
                case 0:
                    //check if serverNumber is divisible by playerNumber
                    returnString = isDivisible(move);
                    break;
                case 1:
                    //compare the sum of each digit
                    returnString = sumOfDigits(move);
                    break;
                case 2:
                    //tell if given number is less than or equal to
                    returnString = smallerOrBigger(move);
                    break;
                case 3:
                    //how many digits of the given number does the server number match
                    returnString = numOfMatchingDigits(move);
                    break;
            }
        }

        else if(move == ServerConnection.randomNumber)
            returnString = "win";

        //We are going to use this to determine which hint the server gives
        ServerConnection.previousHint =  ServerConnection.whichHint;
        //we want to give them a new hint every time
        while(ServerConnection.previousHint != ServerConnection.whichHint)
            ServerConnection.whichHint = ServerConnection.rand.nextInt() % 4;

        return returnString;
    }//end of determiningPoints

    String isDivisible(int playerMove){
        if (ServerConnection.randomNumber % playerMove == 0) {
            return ("The server number is divisible by " + playerMove);
            //tell them that the random number is divisible by this number
        } else {
            return ("The server number is not divisible by " + playerMove);
            //tell them that the random number is not divisible by this number
        }
    }

    String numOfMatchingDigits(int playerMove){
        int numMatchingDigit = 0;
        int tempNumber2 = ServerConnection.randomNumber ;
        int tempNumber = playerMove ;
        int playerDigit = 0;
        int serverDigit = 0;
        Vector<Boolean> matchLocations = new Vector<Boolean>();
        int indexOfMatchForServer = 0;

        while(tempNumber != 0 ){
            playerDigit = tempNumber % 10;
            tempNumber = tempNumber /10;
            indexOfMatchForServer = 0;
            tempNumber2 = ServerConnection.randomNumber;
            while(tempNumber2 != 0 ){
                serverDigit = tempNumber2 % 10;
                tempNumber2 = tempNumber2 /10;
                //if the digits match and they match in a different place
                if(serverDigit == playerDigit && matchLocations.get(indexOfMatchForServer) != true) {
                    //increase the number that have matched and we put true to indicate that digit has matched already
                    ++numMatchingDigit;
                    matchLocations.add(indexOfMatchForServer,true);
                }
                //increase index where we would match
                ++indexOfMatchForServer;
            }
        }//end of second while loop

        //do something with the amount of digits that match
        return ("Number of digits from" + playerMove + " that match with server number is "+numMatchingDigit);
    }

    String smallerOrBigger(int playerMove){
        if(playerMove > ServerConnection.randomNumber){
            return (playerMove + " is greater than number in server");
            //tell them it is greater than the number they gave
        }
        else {
            return (playerMove + " is less than number in server");
            //tell them it is small than the number they gave.
        }
    }

    String sumOfDigits(int playerMove){
        //getting the power to zero that we are dividing by
        int tempRand = ServerConnection.randomNumber;
        int serverDigitSum = 0;
        //adding each digit to serverDigitSum
        while( tempRand!=0){
            serverDigitSum += ServerConnection.randomNumber % 10;
            tempRand = tempRand / (10);
        }
        tempRand = playerMove;
        int playerDigitSum = 0;
        while( tempRand != 0 ){
            playerDigitSum += ServerConnection.randomNumber % 10;
            tempRand = tempRand / (10);
        }

        if(playerDigitSum == serverDigitSum){
            return ("Sum of digits from: "+playerMove+" is equal than server number!");
            //tell them the sum of the digits is the same
        }
        else if (playerDigitSum > serverDigitSum) {
            return ("Sum of digits from: "+playerMove+" is greater than server number!");
            //tell them the sum of the digits are greater than
        }
        else {
            return ("Sum of digits from: "+playerMove+" is less than server number!");
            //tell them the sum of the digits are less than
        }
        //how the sum of the digits compare to yours
    }
}
