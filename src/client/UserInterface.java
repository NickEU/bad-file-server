package client;

import java.util.Scanner;

class UserInterface {
    Scanner sc = new Scanner(System.in);
    Client client = new Client("127.0.0.1", 33333);
    final String MSG_REQUEST_SENT = "The request was sent.";

    void start() {
        client.start();
        System.out.print("Enter action " +
            "(1 - get the file, 2 - create a file, 3 - delete the file): ");
        String action = sc.nextLine();
        switch (action) {
            case "1":
                getFile();
                break;
            case "2":
                createFile();
                break;
            case "3":
                deleteFile();
                break;
            default:
                System.out.println("Error! Unrecognized command. Expected one of: ");
                break;
        }
    }

    private void deleteFile() {
        String fileName = getFilenameFromUser();
        // do stuff
    }

    private void createFile() {
        String fileName = getFilenameFromUser();
        System.out.print("Enter file content: ");
        String data = sc.nextLine();
        boolean fileCreated = client.createFile(fileName, data);
        System.out.println(MSG_REQUEST_SENT);
        String result = fileCreated
            ? "Ok, the response says that the file was created!"
            : "Ok, the response says that creating the file was forbidden!";
        System.out.println(result);
    }

    private void getFile() {
        String fileName = getFilenameFromUser();
        String FILE_CONTENT = client.getFile(fileName);
        System.out.println(MSG_REQUEST_SENT);
        if (FILE_CONTENT == null) {
            System.out.println("Ok, the response says that the file was not found!");
        } else {
            System.out.println("Ok, the content of the file is: " + FILE_CONTENT);
        }
    }

    private String getFilenameFromUser() {
        System.out.print("Enter filename: ");
        return sc.nextLine();
    }
}
