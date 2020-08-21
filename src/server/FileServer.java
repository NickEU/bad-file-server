package server;

class FileServer {
    final String SERVER_IP_ADDRESS = "127.0.0.1";
    final int PORT = 33333;
    final FileStorage fileStorage = new FileStorage();

    void start() {
        System.out.println("Server started!");
        // do your server stuff
    }

    public boolean add(String fileName) {
        return fileStorage.add(fileName);
    }

    public AbstractFile get(String fileName) {
        return fileStorage.get(fileName);
    }

    public boolean delete(String fileName) {
        return fileStorage.delete(fileName);
    }
}
