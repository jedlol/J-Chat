
// APIs used
import java.io.BufferedReader;  // used to read text from input stream
import java.io.IOException; // used to check any errors
import java.io.InputStreamReader; // used to read bytes and convert them into characters
import java.io.PrintWriter; // used to output print messages 
import java.net.Socket; // used to implement the client socket

// client class, uses run method
// will constantly listen for messages from server
public class client implements Runnable {

    private BufferedReader receive;
    private PrintWriter send;
    private Thread t;

    @Override
    public void run() {

        try {

            // creating socket class, 
            // connecting to server class with same socket, and IP
            // using local host ip, 9999 socket
            Socket socket = new Socket("127.0.0.1", 9999);

            // PrintWriter and BufferedReader for output and input
            send = new PrintWriter(socket.getOutputStream(), true);
            receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // creating new handler object
            ClientThread handler = new ClientThread();

            // creating new thread with handler object
            // starting thread
            t = new Thread(handler);
            t.start();

            // string for incoming messages
            String incomming;

            // receiving input from server
            while((incomming = receive.readLine())  != null) {
                System.out.println(incomming);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }  

    // shutdown method called when user enters .exit
    // interrupting thread
    public void exit() {
        t.interrupt();
        t = null;
    }

    // ClientThread class
    // used to take user input, and send to server
    // uses runnable, to constantly listen for user input
    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {

                // BufferedReader used for taking user input
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

                while (true) {

                    // string msg, for taking user input message
                    String msg = input.readLine();

                    // .exit command, if user inputs .exit
                    // will disconnect client program
                    if (msg.equals(".exit")){
                        send.println(msg);
                        input.close();
                        exit();

                    // send object, used for sending user input to server
                    } else {

                        send.println(msg); // send user input using print writer
                    }
                }

            } catch (IOException e){
                //ignore
            }
        }
    }

    // main method, once the client program is executed
    // a client object is created
    // the client object is called with run method
    public static void main(String[] args) {
        client client = new client();
        client.run();
    }
}