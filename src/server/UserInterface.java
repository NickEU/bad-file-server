package server;

import java.util.Arrays;
import java.util.Scanner;

class UserInterface {
    private final Scanner sc = new Scanner(System.in);
    private FileServer server;

    void start() {
        server = new FileServer();
        runMainMenuLoop();
    }

    private void runMainMenuLoop() {
        final String cmdExit = "exit";
        final String cmdAdd = "add";
        final String cmdGet = "get";
        final String cmdDel = "delete";
        String[] commands = {cmdAdd, cmdGet, cmdDel};

        while (true) {
            String userInput = sc.nextLine().toLowerCase();
            if (cmdExit.equals(userInput)) {
                return;
            }
            String[] inputTokens = userInput.split("\\s+");
            if (inputTokens.length != 2) {
                System.out.println("Error! Invalid format. Expected: command argument");
                continue;
            }
            String cmd = inputTokens[0];
            String fileName = inputTokens[1];
            String response;
            switch (cmd) {
                case cmdAdd:
                    response = server.add(fileName)
                        ? "The file " + fileName + " added successfully"
                        : "Cannot add the file " + fileName;
                    break;
                case cmdGet:
                    AbstractFile result = server.get(fileName);
                    response = result != null
                        ? "The file " + fileName + " was sent"
                        : "The file " + fileName + " not found";
                    break;
                case cmdDel:
                    response = server.delete(fileName)
                        ? "The file " + fileName + " was deleted"
                        : "The file " + fileName + " not found";
                    break;
                default:
                    response = "Error! Unrecognized command. Expected one of: "
                        + Arrays.toString(commands);
                    break;
            }
            System.out.println(response);
        }
    }
}
