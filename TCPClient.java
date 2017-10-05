import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient implements Runnable {

    public static int counter;

   // private static Socket client;
    private static String IP;
    private static String Port;

        public TCPClient() {
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
    
	public static void main(String args[]) throws IOException {
    	IP = args[0];
    	Port = args[1];
    	
        int cntr = 0;
		while (cntr < 300) {
		    //Socket newClient = listener.accept();
		    TCPClient tcpClient = new TCPClient();
		    //TCP_WORKER_SERVICE.submit((new TCPClient(newClient)).run());
		    tcpClient.run();
		    cntr ++;
		}


    }

	/*public static Socket getClient() {
		return client;
	}

	public static void setClient(Socket client) {
		TCPClient.client = client;
	}*/

}

