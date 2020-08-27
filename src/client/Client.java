package client;

import conventions.API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

class Client {
    private final String SERVER_ADDRESS;
    private final int PORT;
    private DataInputStream input;
    private DataOutputStream output;
    private static final String EMPTY_STRING = "";

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

    String getFileFromServer(String identifier, boolean isId) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_GET + buildRequestStrFromId(identifier, isId));
            String response = input.readUTF();
            if (response.startsWith(API.STATUS_CODE_404)) {
                return null;
            } else if (response.startsWith(API.STATUS_CODE_200)) {
                return Arrays.stream(response.split(API.COMMAND_ARG_SEPARATOR))
                    .skip(1).collect(Collectors.joining(" "));
            } else {
                return null; //something went wrong
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    String sendFileToServer(String fileNameClient, String fileNameServer) {
        try {
            byte[] fileContents = getFileContents(fileNameClient);
            output.writeUTF(API.HTTP_REQUEST_METHOD_PUT + API.COMMAND_ARG_SEPARATOR
                + fileNameServer);
            output.writeInt(fileContents.length);
            output.write(fileContents);
            String response = input.readUTF();
            if (response.startsWith(API.STATUS_CODE_403)) {
                return EMPTY_STRING;
            }
            if (response.startsWith(API.STATUS_CODE_200)) {
                return response.split(API.COMMAND_ARG_SEPARATOR)[1];
            }
            return EMPTY_STRING;
        } catch (IOException e) {
            e.printStackTrace();
            return EMPTY_STRING;
        }
    }

    private byte[] getFileContents(String fileName) {
        //TODO: implement reading a file from src/client/data
        return new byte[5];
    }

    boolean deleteFileOnServer(String identifier, boolean isId) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_DELETE + buildRequestStrFromId(identifier, isId));
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

    private String buildRequestStrFromId(String identifier, boolean isId) {
        return API.COMMAND_ARG_SEPARATOR
            + (isId ? API.REQ_FILE_BY_ID : API.REQ_FILE_BY_NAME)
            + API.COMMAND_ARG_SEPARATOR + identifier;
    }

    public void shutdownServer() {
        try {
            output.writeUTF("exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
