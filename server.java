// APIs used
import java.io.BufferedReader; // used to read text from input stream
import java.io.IOException; // used to check any errors
import java.io.InputStreamReader; // used to read bytes and convert them into characters
import java.io.PrintWriter; // used to output print messages 
import java.net.ServerSocket; // used to implement server socket
import java.net.Socket; // used to implement the client socket
import java.util.Vector; // used as an arraylist to maintain list of threads (connected clients)
import java.util.*;

import java.lang.*;
// server class that uses a run method
// will constantly listen for clients to connect
public class server implements Runnable {

    private Vector<ServerThread> threads;
    private ServerSocket ss;
    private int clientCount = 0;
    private List<String>l = new ArrayList<String>();

    // creating new ArrayList threads
    // once a client connects, will be added to ArrayList
    public server() {
        threads = new Vector<ServerThread>();
    }

    @Override
    public void run() {
        try {

            // creating server socket class
            // will be used to listen to connection request from client
            // using local host for ip

            ss = new ServerSocket(9999); 

            System.out.println("Server has started.");

            while (true) {

                // creating socket class, to send and receive data from client
                // accept, in a while loop, waiting for request from client
                Socket socket = ss.accept(); 

                // create new handler object with socket
                ServerThread handler = new ServerThread(socket);

                // creating new thread with handler object
                // adding handler to end of arraylist
                Thread t = new Thread(handler);
                threads.add(handler);

                // starting thread
                t.start();
                System.out.println("New user has joined the server!");
            }

        } catch (IOException e) {
            System.out.println("Error with server/client connection");
        }
    }

    // forward method that takes the client's message
    // calls output method for each thread(client) in 
    public void forward(String msg) {  
        for (ServerThread i : threads) {
                i.output(msg);
        }
    }

    public void pm(int user, String msg) {
            if (clientCount > 0){
                threads.get(user).output(msg);
        }
    }

    /* ServerThread class, 
       used for receiving client input
       and outputting to connected clients */
    class ServerThread implements Runnable {

        private Socket socket;
        private BufferedReader recieve;
        private PrintWriter send;
        private String msg;
        private String userName = "user_" + clientCount;


        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        // run method, will constantly check for client input
        @Override
        public void run() {
            try {

                // output, class PrintWriter, used to write message to the Client's socket
                send = new PrintWriter(socket.getOutputStream(), true);

                // input, passed to BufferedReader, used to read message from Client's socket
                recieve = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientCount++;

                send.println("");
                send.println("Welcome, Your username is: " + "user_" + (clientCount - 1));
                send.println("to begin chatting," + " type any message");
                send.println("to private message, type .pm number (ex- .pm 1)");
                send.println("to exit chatroom type .exit");
                send.println("");

                send.println("users connected: ");
                l.add(userName);

                for (int i = 0; i < l.size(); i++) {
                    send.println(l.get(i));
                }


                forward(" -> " + userName + " has connected");

                while ((msg = recieve.readLine()) != null) {
                    if (msg.equals(".exit")) {
                        // .exit command, for client to quit chat
                        // calls bye method
                        forward(userName + " has left the chat");
                        l.remove(userName);
                        exit();

                    } else if (msg.startsWith(".pm")) {
                        String[] messageSplit = msg.split(" ");

                            int i_user = Integer.parseInt(messageSplit[1]);
                            if (i_user < clientCount);
                            String[] messageSplit2 = msg.split(".pm ");
                            pm(i_user, userName + " PM: " + (messageSplit2[1].substring(1)));
                    }
                                 
                    else {
                        // forward method, will forward client message to other clients
                        forward(userName + ": " + msg);
                    }
                }
            } catch (Exception e){
                // ignore
            }
        }

        // output method for PrintWriter
        // to write message to connected clients
        public void output(String msg){
            send.println(msg);
        }

        // disconnect method called when user enters .exit, 
        // will close client socket object
        public void exit() {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error: Issue closing client socket.");
            }
        }
    }

    // main method, executes when program is executed
    // will create new server object
    // and run server 
    public static void main(String[] args) {
        server server = new server();
        server.run();
    }
}

