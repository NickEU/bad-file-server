package client;

import conventions.API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.Helpers.getFileContents;

class Client {
    private final String PATH_TO_DATA_DIR = "src" + File.separator + "client"
        + File.separator + "data" + File.separator;
    private final String SERVER_ADDRESS;
    private final int PORT;
    private DataInputStream input;
    private DataOutputStream output;
    private static final String EMPTY_STRING = "";

    Client(String SERVER_ADDRESS, int PORT) {
        this.SERVER_ADDRESS = SERVER_ADDRESS;
        this.PORT = PORT;
        try {
            Files.createDirectories(Paths.get(PATH_TO_DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    byte[] getFileFromServer(String identifier, boolean isId) {
        try {
            output.writeUTF(API.HTTP_REQUEST_METHOD_GET + buildRequestStrFromId(identifier, isId));
            String response = input.readUTF();
            if (response.equals(API.STATUS_CODE_200)) {
                int responseLength = input.readInt();
                byte[] fileContents = new byte[responseLength];
                input.readFully(fileContents, 0, responseLength);
                return fileContents;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    String sendFileToServer(String fileNameClient, String fileNameServer) {
        try {
            byte[] fileContents = getFileContents(Paths.get(PATH_TO_DATA_DIR + fileNameClient));
            if (fileContents == null) {
                return EMPTY_STRING;
            }
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

    public boolean saveFileToLocalStorage(String fileName, byte[] fileContent) {
        try {
            Files.write(new File(PATH_TO_DATA_DIR + fileName).toPath(), fileContent);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
