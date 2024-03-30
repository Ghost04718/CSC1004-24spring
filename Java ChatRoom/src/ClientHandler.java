import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Server server;
    private Socket clientSocket;
    private String clientName;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int messageNumbers;

    public ClientHandler(Server s, Socket cS) throws IOException {
        this.server = s;
        this.clientSocket = cS;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        messageNumbers = 0;
    }

    // Runnable method to handle client communication
    public void run() {
        try {
            writer.write("Welcome! Please enter your username for the group chat:" + "\n");
            writer.flush();
            clientName = reader.readLine();
            server.broadcastServerMessage(clientName + " has joined the chat.");

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("#exit")) {
                    break;
                }
                messageNumbers++;
                server.broadcastUserMessage(message + "  [message(" + messageNumbers + ")]", this);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                writer.close();
                reader.close();
                clientSocket.close();
                server.removeClient(this);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    // Method to send a message to the client
    public void sendMessage(String s) {
        try {
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getClientName() {
        return clientName;
    }
}
