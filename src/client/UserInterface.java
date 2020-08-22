package client;

import java.util.Scanner;

class UserInterface {
    Scanner sc = new Scanner(System.in);
    Client client = new Client("127.0.0.1", 33333);

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
        // do stuff
    }

    private void getFile() {
        String fileName = getFilenameFromUser();
        String FILE_CONTENT = client.getFile(fileName);
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
