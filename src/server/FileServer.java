package server;

import conventions.API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

class FileServer {
    final String SERVER_IP_ADDRESS = "127.0.0.1";
    final int PORT = 33333;
    final FileStorage fileStorage = new FileStorage();

    void start() {
        System.out.println("Server started!");
        try {
            InetAddress ip = InetAddress.getByName(SERVER_IP_ADDRESS);
            while (true) {
                try (
                    ServerSocket server = new ServerSocket(PORT, 99, ip);
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    String req = input.readUTF();
                    if ("exit".equalsIgnoreCase(req.trim())) {
                        return;
                    }
                    String response = processRequest(req.split(API.COMMAND_ARG_SEPARATOR), input);
                    output.writeUTF(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processRequest(String[] req, DataInputStream input) {
        String reqType = req[0];
        switch (reqType) {
            case API.HTTP_REQUEST_METHOD_GET:
                AbstractFile result = get(req[2], API.REQ_FILE_BY_ID.equals(req[1]));
                return result != null
                    ? API.STATUS_CODE_200 + API.COMMAND_ARG_SEPARATOR + result
                    : API.STATUS_CODE_404;
            case API.HTTP_REQUEST_METHOD_PUT:
                String desiredFileName = req.length == 1 ? "" : req[1];
                String fileName = desiredFileName.isEmpty()
                    ? generateFileName()
                    : desiredFileName;
                try {
                    int fileContentLength = input.readInt();
                    byte[] fileContent = new byte[fileContentLength];
                    input.readFully(fileContent, 0, fileContentLength);
                    String id = add(fileName, fileContent);
                    return id != null
                        ? API.STATUS_CODE_200 + API.COMMAND_ARG_SEPARATOR + id
                        : API.STATUS_CODE_403;
                } catch (IOException e) {
                    return API.STATUS_CODE_404;
                }
            case API.HTTP_REQUEST_METHOD_DELETE:
                return delete(req[2], API.REQ_FILE_BY_ID.equals(req[1]))
                    ? API.STATUS_CODE_200
                    : API.STATUS_CODE_404;
            default:
                return API.STATUS_CODE_404;
        }
    }

    private String generateFileName() {
        return "t" + new Random().nextInt(20) + ".txt";
    }

    public String add(String fileName, byte[] fileContent) {
        return fileStorage.add(fileName, fileContent);
    }

    public AbstractFile get(String identifier, boolean isId) {
        return fileStorage.get(identifier, isId);
    }

    public boolean delete(String fileName, boolean isId) {
        return fileStorage.delete(fileName, isId);
    }
}
