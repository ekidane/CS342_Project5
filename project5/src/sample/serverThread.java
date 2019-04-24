//package sample;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.function.Consumer;

//this is our thread for the server
public class serverThread extends Thread{

    int portNumber;
    //make scope bigger
    ServerSocket mySock = null;
    //Vector of superSockets that way we can have as many as we want
    Vector<Socket> players = new Vector();
    //vector of connthread that way we can access the data that each one receives
    static Vector<ConnThread> connections = new Vector();
    static int numOfPlayers = 0;
    Consumer updateGUI;

    //constructor for the server
    serverThread(Consumer newFunction) {
        portNumber = -1;
        updateGUI = newFunction;
    }

    //this function shuts everything down in the server
    void letsClose(){
        if(mySock == null)
            return;
        try {
            //I guess when we call this we will kill the listener over in the run function
            //we will get an exception once we have close the thing
            mySock.close();
            numOfPlayers = 0;
            for(Socket randSock : players){
                randSock.close();
            }
            for(ConnThread randThread: connections){
                randThread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        //we create the server socket once
        try {
            mySock = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //number of players and the vector of sockets that way we can keep all the things listening and can access them
        //we are starting the infinite loop
        while (true) {
            //then we continue to listen for people to connect to us
            try {
                //showing that we have a new player connected
                ServerConnection.clientsConnected.setText("Clients Connected: "+numOfPlayers);
                //right here we are waiting for a socket to have communication possible between the server and player
                Socket newSocket = (mySock.accept()); //mySock.accept() is a blocking function so we won't continue until a client connects to us
                //we add the superSocket to our players vector and we know their location is the same as the number of players
                players.add(newSocket);

                //We are creating a new thread where we read in the input of whatever port connected to me
                ConnThread newThread = new ConnThread(updateGUI);
                //this socket player we want to thread it so that we can have it listening for the socket it is connected to
                newThread.socket = players.get(numOfPlayers);
                //each thread has a number which is the number of players that have connected to the server so each player has a unique number. THis was in my project 3
                newThread.myPlayerNum = numOfPlayers;
                //initialize output stream of the new thread
                newThread.out = new ObjectOutputStream( newThread.socket.getOutputStream());
                newThread.out.flush();

                //adding the thread to our connections vector and then starting the thread
                connections.add(newThread);
                connections.get(numOfPlayers).start();

                //here we are increasing the number of players
                ++numOfPlayers;
            }
            catch(IOException e){
                //indicate we caught the exception and then we break
                --numOfPlayers;
                //update the text with the new amount of players
                System.out.println("We have hit an exception and are going to stop listening for a socket to connect to.");
                break;
            }
        }//end of while loop
    }//end of run
}// end of serverThread
