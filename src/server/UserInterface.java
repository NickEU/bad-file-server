package server;

import java.util.Scanner;

class UserInterface {
    private final Scanner sc = new Scanner(System.in);
    void start() {
        runMainMenuLoop();
    }

    private void runMainMenuLoop() {
        while (true) {
            String userInput = sc.nextLine().toLowerCase();
            if ("exit".equals(userInput)) {
                break;
            }
            System.out.println("Doing stuff!");
        }
    }
}
