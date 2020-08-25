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
    private static int id;
    private final Map<String, String> idsToNames = new HashMap<>();
    private final String pathToDataDir = "src" + File.separator + "server"
        + File.separator + "data" + File.separator;
    private final String pathToConfigFile = "src" + File.separator + "server"
        + File.separator + "config.txt";
    private final String SCARY_DELIMITER = "===";

    public FileStorage() {
        try {
            Files.createDirectories(Paths.get(pathToDataDir));
            readMapFromFile();
            id = idsToNames.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String add(String fileName, String fileContent) {
        File file = new File(pathToDataDir + fileName);
        if (file.exists()) {
            return null;
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(fileContent);
            return saveIdToMap(fileName);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String saveIdToMap(String fileName) {
        idsToNames.put(String.valueOf(id), fileName);
        saveMapToFile();
        return String.valueOf(id++);
    }

    private void saveMapToFile() {
        try (PrintWriter pw = new PrintWriter(pathToConfigFile)) {
            idsToNames.forEach((k, v) -> pw.println(k + SCARY_DELIMITER + v));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readMapFromFile() {
        try {
            Files.lines(Paths.get(pathToConfigFile))
                .map(s -> s.split(SCARY_DELIMITER))
                .forEach(s -> idsToNames.put(s[0], s[1]));
        } catch (IOException ignored) {
        }
    }

    AbstractFile get(String identifier, boolean isId) {
        try {
            String fileName = getFilenameFromId(identifier, isId);
            String content = Files.lines(Paths.get(pathToDataDir + fileName))
                .collect(Collectors.joining());
            return new AbstractFile(identifier, content);
        } catch (IOException e) {
            return null;
        }
    }

    boolean delete(String fileName) {
        try {
            // TODO : make it support IDs
            Path p = Paths.get(pathToDataDir + fileName);
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
