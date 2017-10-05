import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.util.Date;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient implements Runnable {

    public static int counter;

    private static Socket client;
    private String clientbuffer;
    private static String IP;
    private static String Port;

        public TCPClient(Socket client) {
            TCPClient.client = client;
            this.clientbuffer = "";
            counter++;
        }

        @Override
        public void run() {
            try {
                String message, response;
                Socket socket = new Socket(IP, Integer.parseInt(Port));

                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                BufferedReader server = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                String clientMessage = "HELLO "+ Inet4Address.getLocalHost().getHostAddress() + " " +  socket.getLocalPort() + counter + System.lineSeparator();
                Reader clientMessageReader = new StringReader(clientMessage);
                BufferedReader messageFromClient = new BufferedReader(clientMessageReader);                

                message = messageFromClient.readLine() + System.lineSeparator();

                output.writeBytes(message);
                response = server.readLine();

                System.out.println("[" + new Date() + "] Received: " + response);
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);
    
	public static void main(String args[]) {
    	IP = args[0];
    	Port = args[1];
    	
        try {
        	ServerSocket listener = new ServerSocket(Integer.parseInt(Port));

            int cntr = 0;
            while (cntr < 300) {
                Socket newClient = listener.accept();
                TCP_WORKER_SERVICE.submit(new TCPClient(newClient));
                cntr ++;
                newClient.close();
            }
            
            listener.close();

            

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

