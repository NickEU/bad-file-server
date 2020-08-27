package client;

import java.util.Scanner;

class UserInterface {
    final String MSG_REQUEST_SENT = "The request was sent.";
    Scanner sc = new Scanner(System.in);
    Client client = new Client("127.0.0.1", 33333);

    void start() {
        client.start();
        System.out.print("Enter action " +
            "(1 - get the file, 2 - save the file, 3 - delete the file): ");
        String action = sc.nextLine();
        switch (action) {
            case "1":
                getFile();
                break;
            case "2":
                saveFile();
                break;
            case "3":
                deleteFile();
                break;
            case "exit":
                client.shutdownServer();
                break;
            default:
                System.out.println("Error! Unrecognized command. Expected one of: ");
                break;
        }
    }

    private void deleteFile() {
        boolean isId = gotIdFromUser("delete");
        String identifier = sc.nextLine();
        boolean fileDeleted = client.deleteFileOnServer(identifier, isId);
        System.out.println(MSG_REQUEST_SENT);
        String result = "Ok, the response says that the file was " +
            (fileDeleted ? "successfully deleted!" : "not found!");
        System.out.println(result);
    }

    private boolean gotIdFromUser(String operationType) {
        while (true) {
            System.out.printf("Do you want to %s the file by name or by id (1 - name, 2 - id): ", operationType);
            String userChoice = sc.nextLine();
            if ("1".equals(userChoice)) {
                System.out.print("Enter name of the file: ");
                return false;
            }

            if ("2".equals(userChoice)) {
                System.out.print("Enter id: ");
                return true;
            }
            System.out.println("Error! Expected 1 or 2 as input");
        }
    }

    private void saveFile() {
        System.out.print("Enter name of the file: ");
        String fileNameClient = sc.nextLine();
        System.out.print("Enter name of the file to be saved on server: ");
        String fileNameServer = sc.nextLine();
        String id = client.sendFileToServer(fileNameClient, fileNameServer);
        System.out.println(MSG_REQUEST_SENT);
        String result = "Ok, the response says that " +
            (!id.isEmpty() ? "the file was created! ID = " + id : "creating the file was forbidden!");
        System.out.println(result);
    }

    private void getFile() {
        boolean isId = gotIdFromUser("get");
        String identifier = sc.nextLine();
        String FILE_CONTENT = client.getFileFromServer(identifier, isId);
        System.out.println(MSG_REQUEST_SENT);
        if (FILE_CONTENT == null) {
            System.out.println("Ok, the response says that the file was not found!");
        } else {
            System.out.println("Ok, the content of the file is: " + FILE_CONTENT);
        }
    }
}
