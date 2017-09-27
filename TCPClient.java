import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

public class TCPClient {

    public static int counter;
private static class TCPClient implements Runnable {

        private Socket client;
        private String clientbuffer;

        public TCPClient(Socket client) {
            this.client = client;
            this.clientbuffer = "";
            counter++;
        }

        @Override
        public void run() {
            try {
                System.out.println("Client connected with: " + this.client.getInetAddress());

                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(this.client.getInputStream())
                );

                this.clientbuffer = reader.readLine();
                System.out.println("[" + new Date() + "] Received: " + this.clientbuffer);

                output.writeBytes(this.clientbuffer.toUpperCase() + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

}
    
    public static void main(String args[]) {
        try {

            String message, response;
            Socket socket = new Socket("34.209.49.228", 80);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader server = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            message = reader.readLine() + System.lineSeparator();


            output.writeBytes(message);
            response = server.readLine();

            System.out.println("[" + new Date() + "] Received: " + response);

            
            while (true) {
                Socket client = socket.accept();

                TCP_WORKER_SERVICE.submit(
                        new TCPClient(client)
                );

            }
            
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
