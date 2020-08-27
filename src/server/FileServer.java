package server;

import conventions.API;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
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
                    processRequest(req.split(API.COMMAND_ARG_SEPARATOR), input, output);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(String[] req, DataInputStream input, DataOutputStream output) throws IOException {
        String reqType = req[0];
        String response;
        switch (reqType) {
            case API.HTTP_REQUEST_METHOD_GET:
                try {
                    processGetRequest(req, output);
                } catch (IOException e) {
                    output.writeUTF(API.STATUS_CODE_404);
                }
                return;
            case API.HTTP_REQUEST_METHOD_PUT:
                try {
                    output.writeUTF(processPutRequest(req, input));
                } catch (IOException e) {
                    output.writeUTF(API.STATUS_CODE_403);
                }
                return;
            case API.HTTP_REQUEST_METHOD_DELETE:
                response = delete(req[2], API.REQ_FILE_BY_ID.equals(req[1]))
                    ? API.STATUS_CODE_200
                    : API.STATUS_CODE_404;
                break;
            default:
                response = API.STATUS_CODE_404;
                break;
        }
        output.writeUTF(response);
    }

    private String processPutRequest(String[] req, DataInputStream input) throws IOException {
        String desiredFileName = req.length == 1 ? "" : req[1];
        String fileName = desiredFileName.isEmpty()
            ? generateFileName()
            : desiredFileName;

        int fileContentLength = input.readInt();
        byte[] fileContent = new byte[fileContentLength];
        input.readFully(fileContent, 0, fileContentLength);
        String id = add(fileName, fileContent);
        return id != null
            ? API.STATUS_CODE_200 + API.COMMAND_ARG_SEPARATOR + id
            : API.STATUS_CODE_403;
    }

    private void processGetRequest(String[] req, DataOutputStream output) throws IOException {
        AbstractFile result = get(req[2], API.REQ_FILE_BY_ID.equals(req[1]));
        if (result == null) {
            throw new IOException();
        }
        output.writeUTF(API.STATUS_CODE_200);
        byte[] fileContents = Objects.requireNonNull(result).getContents();
        output.writeInt(fileContents.length);
        output.write(fileContents);
    }

    private String generateFileName() {
        var rnd = new Random();
        int bound = 50;
        int tries = 0;
        while (true) {
            String potentialName = "t" + rnd.nextInt(bound) + ".txt";
            if (fileStorage.fileNameIsNotInUse(potentialName)) {
                return potentialName;
            }
            if (tries++ > 10) {
                bound *= 2;
            }
        }
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
