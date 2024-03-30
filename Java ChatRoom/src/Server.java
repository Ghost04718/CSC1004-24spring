import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Server {

    private ServerSocket serverSocket;

    // File to store chat history
    private String chatRecordFile = "";

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(47);
        Server server = new Server(serverSocket);
        server.launchServer();
    }

    public void launchServer() {
        try {
            // Accept and handle incoming client connections
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                Thread clientThread = new Thread(clientHandler);
                System.out.println("SERVER: A New User Has Connected!");

                clientHandlers.add(clientHandler);

                clientThread.start();

                // Generate a chat history file if not exists
                if (chatRecordFile.isEmpty()) {
                    chatRecordFile = "chat_record_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".txt";
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void broadcastUserMessage(String s, ClientHandler sender) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String str = sender.getClientName() + ": " + s + "  " + "[" + timestamp + "]";

        for (ClientHandler ch : clientHandlers) {
            if (ch != sender) {
                ch.sendMessage(str);
            }
        }

        saveMessageToFile(str);
    }

    public void broadcastServerMessage(String s) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String str = "SERVER: " + s + "  " + "[" + timestamp + "]";

        for (ClientHandler cH : clientHandlers) {
            cH.sendMessage(str);
        }

        saveMessageToFile(str);
    }

    public void removeClient(ClientHandler ch) {
        // Remove a client from the list and inform other clients
        clientHandlers.remove(ch);
        String s = ch.getClientName();
        System.out.println("SERVER: " + s + " has left the chat." + "\n");
        broadcastServerMessage(s + " has left the chat.");
    }

    public void saveMessageToFile(String s) {
        try {
            FileWriter fw = new FileWriter(chatRecordFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
