package client;

import conventions.API;

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

    Client(String SERVER_ADDRESS, int PORT) {
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

    String getFile(String fileName) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_GET + API.COMMAND_ARG_SEPARATOR + fileName);
            String response = input.readUTF();
            if (response.startsWith(API.STATUS_CODE_404)) {
                return null;
            } else if (response.startsWith(API.STATUS_CODE_200)) {
                return response.split(API.COMMAND_ARG_SEPARATOR)[1];
            } else {
                return null; //something went wrong
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    boolean createFile(String fileName, String data) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_PUT + API.COMMAND_ARG_SEPARATOR
                + fileName + API.COMMAND_ARG_SEPARATOR + data);
            String response = input.readUTF();
            if (API.STATUS_CODE_403.equals(response)) {
                return false;
            }

            return API.STATUS_CODE_200.equals(response);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean deleteFile(String fileName) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_DELETE + API.COMMAND_ARG_SEPARATOR + fileName);
            String response = input.readUTF();
            if (API.STATUS_CODE_404.equals(response)) {
                return false;
            }

            return API.STATUS_CODE_200.equals(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
