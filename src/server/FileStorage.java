package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class FileStorage {
    private static int id = 0;
    private final Map<String, String> idsToNames = new HashMap<>();
    private final String pathToDir = "src" + File.separator + "server"
        + File.separator + "data" + File.separator;

    public FileStorage() {
        try {
            Files.createDirectories(Paths.get(pathToDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String add(String fileName, String fileContent) {
        File file = new File(pathToDir + fileName);
        if (file.exists()) {
            return null;
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(fileContent);
            idsToNames.put(String.valueOf(id), fileName);
            return String.valueOf(id++);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    AbstractFile get(String identifier, boolean isId) {
        try {
            String fileName = getFilenameFromId(identifier, isId);
            String content = Files.lines(Paths.get(pathToDir + fileName))
                .collect(Collectors.joining());
            return new AbstractFile(identifier, content);
        } catch (IOException e) {
            return null;
        }
    }

    boolean delete(String fileName) {
        try {
            // TODO : make it support IDs
            Path p = Paths.get(pathToDir + fileName);
            return Files.deleteIfExists(p);
        } catch (IOException e) {
            return false;
        }
    }

    private String getFilenameFromId(String identifier, boolean isId) {
        return isId
            ? idsToNames.getOrDefault(identifier, "error")
            : identifier;
    }
}
