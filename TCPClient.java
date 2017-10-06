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
	private static final int NUMBER_OF_CLIENTS = 15;
	private static final int NUMBER_OF_REQUESTS_PER_CLIENTS = 5;
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
        	for (int i=1;i<=NUMBER_OF_REQUESTS_PER_CLIENTS;i++) {
        			try { 		
        				Socket socket = new Socket(IP, Integer.parseInt(Port));
		          
        				String message, response;

        				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        				BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 
        				String clientMessage = "HELLO "+ Inet4Address.getLocalHost().getHostAddress() + " " +  socket.getLocalPort() + " " +  this.userid + System.lineSeparator();
        				Reader clientMessageReader = new StringReader(clientMessage);
        				BufferedReader messageFromClient = new BufferedReader(clientMessageReader);                

        				message = messageFromClient.readLine() + System.lineSeparator();

        				long beforeSendingTime = System.nanoTime();
        				output.writeBytes(message);
        				response = server.readLine();
        				long afterReceivingTime = System.nanoTime();
        				long rttForThisRequest = afterReceivingTime - beforeSendingTime;
        				               
        				totalTimeSpentPerClient += rttForThisRequest;
                
        				System.out.println("[" + new Date() + "] Received: " + response);
        				
        				if (i == NUMBER_OF_REQUESTS_PER_CLIENTS)
        				{
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


    public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(SIZE_OF_POOL);
    
	public static void main(String args[]) throws IOException {
    	IP = args[0];
    	Port = args[1];
    	long tmpTotalAverageTimeSpentPerClient = 0;

        int userid = 1;
		while (userid <= NUMBER_OF_CLIENTS) {
		    TCPClient tcpClient = new TCPClient(userid);
		    TCP_WORKER_SERVICE.submit(tcpClient);
		    userid ++;
		}
    }

}
