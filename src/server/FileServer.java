package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class FileServer {
    final String SERVER_IP_ADDRESS = "127.0.0.1";
    final int PORT = 33333;
    final FileStorage fileStorage = new FileStorage();

    void start() {
        System.out.println("Server started!");
        try {
            InetAddress ip = InetAddress.getByName(SERVER_IP_ADDRESS);
            ServerSocket server = new ServerSocket(PORT, 99, ip);
            Socket socket = server.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String req = input.readUTF();
            System.out.println("Received: " + req);
            String res = "All files were sent!";
            output.writeUTF(res);
            System.out.println("Sent: " + res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean add(String fileName) {
        return fileStorage.add(fileName);
    }

    public AbstractFile get(String fileName) {
        return fileStorage.get(fileName);
    }

    public boolean delete(String fileName) {
        return fileStorage.delete(fileName);
    }
}
