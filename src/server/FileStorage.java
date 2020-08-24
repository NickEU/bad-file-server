package server;

import java.util.HashMap;
import java.util.Map;

class FileStorage {
    Map<String, AbstractFile> files = new HashMap<>();

    boolean add(String fileName, String fileContent) {
        if (files.get(fileName) != null) {
            return false;
        }
        files.put(fileName, new AbstractFile(fileName, fileContent));
        return true;
    }

    AbstractFile get(String fileName) {
        return files.get(fileName);
    }

    boolean delete(String fileName) {
        return files.remove(fileName) != null;
    }
}
