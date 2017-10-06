import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;
import java.net.Inet4Address;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient implements Runnable {
	
	private static final int SIZE_OF_POOL = 10;
	private static final int NUMBER_OF_CLIENTS = 10;
	private static final int NUMBER_OF_REQUESTS_PER_CLIENTS = 300;
	
    	private static String IP;
    	private static String Port;
    	private int userid;
    	private long totalTimeSpentPerClient;
    	private long averageTimeSpentPerClient;

        public TCPClient(int userid) {
            this.userid = userid;
            this.totalTimeSpentPerClient = 0;
            this.averageTimeSpentPerClient = 0;
        }

        @Override
        public void run() {
		// Each user will send a given number of request
        	for (int i=1;i<=NUMBER_OF_REQUESTS_PER_CLIENTS;i++) {
        			try { 		
        				Socket socket = new Socket(IP, Integer.parseInt(Port));
		          
        				String message, response;

        				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        				BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 
					// The user will send the message: "HELLO [IP adress] [port number] [userid]"
        				String clientMessage = "HELLO "+ Inet4Address.getLocalHost().getHostAddress() + " " +  socket.getLocalPort() + " " +  this.userid + System.lineSeparator();
        				Reader clientMessageReader = new StringReader(clientMessage);
        				BufferedReader messageFromClient = new BufferedReader(clientMessageReader);                

        				message = messageFromClient.readLine() + System.lineSeparator();

					// In order to computer the RTT we check the time just before sending the request
					// and just after receiving it
        				long beforeSendingTime = System.nanoTime();
        				output.writeBytes(message);
        				response = server.readLine();
        				long afterReceivingTime = System.nanoTime();
        				long rttForThisRequest = afterReceivingTime - beforeSendingTime;
        				               
        				totalTimeSpentPerClient += rttForThisRequest;
                
        				System.out.println("[" + new Date() + "] Received: " + response);
        				
        				if (i == NUMBER_OF_REQUESTS_PER_CLIENTS)
        				{
						// When the user sent all its requests we compute the average RTT it took
        					averageTimeSpentPerClient = totalTimeSpentPerClient/NUMBER_OF_REQUESTS_PER_CLIENTS;
        					System.out.println("[Average time spent for client " + userid+ "] " + averageTimeSpentPerClient);
        				}
        				
        				
        				try {
        					Thread.sleep((int)(Math.random() * 1000));
        				} catch (InterruptedException e) {
        				}
		
        				socket.close();
 	
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        		}
        	
        	
        }


	// The requests will be sent using a given number of threads (size of pool)
    	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(SIZE_OF_POOL);
    
	public static void main(String args[]) throws IOException {
    		IP = args[0];
    		Port = args[1];

        	int userid = 1;
		// We simulate a given number of client which will share the threads of the pool to send their request
		while (userid <= NUMBER_OF_CLIENTS) {
		    TCPClient tcpClient = new TCPClient(userid);
		    TCP_WORKER_SERVICE.submit(tcpClient);
		    userid ++;
		}
    }

}
