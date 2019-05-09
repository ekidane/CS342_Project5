/* Team 19 - Levi Herrera, Ellen Kidane, Mohammad Ramahi, Trishla Shah
 * CS 342, Software Design
 * Project 5 - Guess a Number
 *
 * Guess the number. Server randomly generates a positive number in some range like 1 - 100.
 * All clients try to guess what it is. The server gives one hint about the number to start
 * with. When a client guesses incorrectly the server will give them information about the
 * number. No client can guess twice. If they guess correct number with at most all clients
 * guessing then the clients win otherwise they lose. Losing takes clients to a losing screen
 * and then winning takes them to a victory screen. After a game the server makes up a new number.
 *
 * This class controls the networking of the client.
 *
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client {

    private ConnThread connthread = new ConnThread();
    private Consumer<Serializable> callback;
    private String ip;
    private int port;

    public Client(String ip, int port, Consumer<Serializable> data) {
        this.callback = data;
        connthread.setDaemon(true);
        this.ip = ip;
        this.port = port;
    }

    public void startConn() throws Exception{
        connthread.start();
    }

    public void send(Serializable data) throws Exception{
        connthread.out.writeObject(data);
    }

    public void closeConn() throws Exception{
        connthread.socket.close();
    }

    public String getIP() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    class ConnThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;

        public void run() {
            try(Socket socket = new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while(true) {
                    Serializable data = (Serializable) in.readObject();
                    callback.accept(data);
                }

            }
            catch(Exception e) {
                callback.accept("connection closed");
            }
        }
    }

}