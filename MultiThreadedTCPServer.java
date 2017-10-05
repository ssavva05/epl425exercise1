import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

public class MultiThreadedTCPServer {

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

				// socket.getLocalPort() + )
				String str = reader.toString();
				String[] splited = str.split(" ");

				String outputs = "";

				if (splited[0].compareTo("HELLO") == 0) {

					Random rand = new Random(System.currentTimeMillis());
					// 2000 is the maximum and the 300 is our minimum
					int n = rand.nextInt(2000) + 300;
					outputs = "WELCOME " + splited[3] + " " + n + " ";
				}

				else {

					outputs = "UNAUTHORIZED USER";
				}
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				// output.writeBytes(this.clientbuffer.toUpperCase() +
				// System.lineSeparator());
				output.writeBytes(outputs + System.lineSeparator());
				System.out.println("passes "+ outputs);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

	public static void main(String args[]) {
		try {
			ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]));

			System.out.println("Server listening to: " + listener.getInetAddress() + ":" + listener.getLocalPort());

			// while (true) {
			int cntr = 0;
			while (cntr < Integer.parseInt(args[1])) {
				Socket client = listener.accept();

				TCP_WORKER_SERVICE.submit(new TCPWorker(client));

				//listener.close();
				cntr++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

