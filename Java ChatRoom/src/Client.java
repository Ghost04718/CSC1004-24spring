import java.io.*;
import java.net.*;

public class Client {

    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private BufferedReader userReader;

    public Client(Socket s) throws IOException {
        this.clientSocket = s;
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.userReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", 47);
        Client client = new Client(clientSocket);
        client.launchClient();
    }

    // Method to handle client interaction
    public void launchClient() {
        try{
            // Thread to continuously read and print server messages
            Thread clientThread = new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            });
            clientThread.start();

            String userMessage;
            while ((userMessage = userReader.readLine()) != null) {
                if (userMessage.equals("#exit")) {
                    writer.write("#exit");
                    writer.flush();
                    break;
                }
                writer.write(userMessage + "\n");
                writer.flush();
            }

            // Wait for the clientThread to finish, then close resources
            clientThread.join();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }
}
