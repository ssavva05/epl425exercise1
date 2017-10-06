import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class MultiThreadedTCPServer {
	private static final int THREAD_POOL_SIZE = 10; 
	private static class TCPWorker implements Runnable {

		private Socket client;
		private String clientbuffer;

		public TCPWorker(Socket client) {
			this.client = client;
			this.clientbuffer = "";
		}

		@Override
		public void run() {

			try {
				System.out.println("Client connected with: " + this.client.getInetAddress());

				BufferedReader reader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

				this.clientbuffer = reader.readLine();
				System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);

				String str = this.clientbuffer.toString();
				String[] splited = str.split(" ");

				String outputs = "";
				
				if (splited[0].compareTo("HELLO") == 0) {

					Random rand = new Random(System.currentTimeMillis());
					// 2000 is the maximum and the 300 is our minimum
					int n = rand.nextInt(2000) + 300;
					outputs += "WELCOME " + splited[3] + " " + n + " ";
					
				} else {
					outputs += "UNAUTHORIZED USER";
				}
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				output.writeBytes(outputs + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public static void main(String args[]) throws InterruptedException {
		try {
			ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]));

			System.out.println("Server listening to: " + listener.getInetAddress() + ":" + listener.getLocalPort());

			// while (true) {
			int cntr = 0;
		   long time_start = System.nanoTime() ;
			while (cntr < Integer.parseInt(args[1])) {
				Socket client = listener.accept();

				TCP_WORKER_SERVICE.submit(new TCPWorker(client));
				cntr++;
			}
			
			long time_stop = System.nanoTime() ;
			listener.close();
			
			long time =  time_stop - time_start;
			//long seconds = TimeUnit.NANOSECONDS.toSeconds(time);
			//long req = (long) (((cntr+1)*000000000.1)/time);
			long req = (long) (cntr/time);
			System.out.println("The server satisfies "+ req+" per nanosecond");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

