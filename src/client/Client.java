package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client {
    final String SERVER_ADDRESS = "127.0.0.1";
    final int PORT = 33333;

    void start() {
        System.out.println("Client started!");
        try {
            InetAddress ip = InetAddress.getByName(SERVER_ADDRESS);
            Socket socket = new Socket(ip, PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String msg = "Give me everything you have!";
            output.writeUTF(msg);
            System.out.println("Sent: " + msg);
            String response = input.readUTF();
            System.out.println("Received: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
