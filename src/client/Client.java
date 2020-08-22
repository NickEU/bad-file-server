package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client {
    private final String SERVER_ADDRESS;
    private final int PORT;
    private DataInputStream input;
    private DataOutputStream output;

    public Client(String SERVER_ADDRESS, int PORT) {
        this.SERVER_ADDRESS = SERVER_ADDRESS;
        this.PORT = PORT;
    }

    void start() {
        try {
            InetAddress ip = InetAddress.getByName(SERVER_ADDRESS);
            Socket socket = new Socket(ip, PORT);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFile(String fileName) {
        try {
            output.writeUTF("GET " + fileName);
            String response = input.readUTF();
            if (response.startsWith("404")) {
                return null;
            } else if (response.startsWith("200")) {
                return response.split(" ")[1];
            } else {
                return null; //something went wrong
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
