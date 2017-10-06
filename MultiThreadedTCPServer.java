
/*
Copyright 2017 abecket, ssavva05
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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

/**
 * This the MultiThreadedTCPServer class. This class include all the
 * implementation of the server that get requests and reply accordingly.
 */
public class MultiThreadedTCPServer {
	private static final int THREAD_POOL_SIZE = 10;

	private static class TCPWorker implements Runnable {

		private Socket client;
		private String clientbuffer;

		/**
		 * This is the TCP worker object that creates a worker object to handle
		 * a request.
		 * 
		 * @param client
		 */
		public TCPWorker(Socket client) {
			this.client = client;
			this.clientbuffer = "";
		}

		/**
		 * The core implementation is packed in the run method because we need
		 * to have hyper threaded request handling.
		 */
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

	/** This is the thread pool */
	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	/**
	 * This is the main. As a main point the program start here and computes
	 * also the time that we need for execution
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String args[]) throws InterruptedException {
		try {
			ServerSocket listener = new ServerSocket(Integer.parseInt(args[0]));

			System.out.println("Server listening to: " + listener.getInetAddress() + ":" + listener.getLocalPort());

			// while (true) {
			int cntr = 0;
			long time_start = System.nanoTime();
			while (cntr < Integer.parseInt(args[1])) {
				Socket client = listener.accept();
				TCP_WORKER_SERVICE.submit(new TCPWorker(client));
				cntr++;
			}

			long time_stop = System.nanoTime();
			listener.close();
			long time = time_stop - time_start;
			double seconds = (double) (time) / 1000000000.0;
			
			double reqd = cntr/seconds;
			int a = (int) Math.round(reqd);
			//double reqd = (double) (req) / 1000000000.0;
			System.out.println(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
