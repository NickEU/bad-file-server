package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class FileStorage {
    private final String pathToDir = "File Server" + File.separator + "task"
        + File.separator + "src" + File.separator + "server"
        + File.separator + "data" + File.separator;
    Map<String, AbstractFile> files = new HashMap<>();

    public FileStorage() {
        try {
            Files.createDirectories(Paths.get(pathToDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean add(String fileName, String fileContent) {
        File file = new File(pathToDir + fileName);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            return false;
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(fileContent);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    AbstractFile get(String fileName) {
        try {
            String content = Files.lines(Paths.get(pathToDir + fileName))
                .collect(Collectors.joining());
            return new AbstractFile(fileName, content);
        } catch (IOException e) {
            return null;
        }
    }

    boolean delete(String fileName) {
        return files.remove(fileName) != null;
    }
}
