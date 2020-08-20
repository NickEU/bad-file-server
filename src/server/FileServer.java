package server;

import java.util.ArrayList;
import java.util.List;

class FileServer {
    List<AbstractFile> files = new ArrayList<>();

    public boolean add(String fileName) {
        return true;
    }

    public AbstractFile get(String fileName) {
        return null;
    }

    public boolean delete(String fileName) {
        return true;
    }
}
