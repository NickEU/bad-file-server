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
    private final String STATUS_CODE_200 = "200";
    private final String STATUS_CODE_404 = "404";
    private final String STATUS_CODE_403 = "403";
    private final String COMMAND_ARG_SEPARATOR = " ";

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
        final String httpRequestMethodGet = "GET";
        try {
            output.writeUTF(httpRequestMethodGet + COMMAND_ARG_SEPARATOR + fileName);
            String response = input.readUTF();
            if (response.startsWith(STATUS_CODE_404)) {
                return null;
            } else if (response.startsWith(STATUS_CODE_200)) {
                return response.split(COMMAND_ARG_SEPARATOR)[1];
            } else {
                return null; //something went wrong
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createFile(String fileName, String data) {
        final String httpRequestMethodPut = "PUT";
        try {
            output.writeUTF(httpRequestMethodPut + COMMAND_ARG_SEPARATOR
                + fileName + COMMAND_ARG_SEPARATOR + data);
            String response = input.readUTF();
            if (STATUS_CODE_403.equals(response)) {
                return false;
            }

            return STATUS_CODE_200.equals(response);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFile(String fileName) {
        final String httpRequestMethodDelete = "DELETE";
        try {
            output.writeUTF(httpRequestMethodDelete + COMMAND_ARG_SEPARATOR + fileName);
            String response = input.readUTF();
            if (STATUS_CODE_404.equals(response)) {
                return false;
            }

            return STATUS_CODE_200.equals(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
