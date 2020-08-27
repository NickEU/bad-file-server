package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static util.Helpers.getFileContents;

class FileStorage {
    private static int id;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final SortedMap<Integer, String> idsToNames = new TreeMap<>();
    private final String PATH_TO_DATA_DIR = "src" + File.separator + "server"
        + File.separator + "data" + File.separator;
    private final String pathToConfigFile = "src" + File.separator + "server"
        + File.separator + "config.txt";
    private final String SCARY_DELIMITER = "===";

    public FileStorage() {
        try {
            Files.createDirectories(Paths.get(PATH_TO_DATA_DIR));
            readMapFromFile();
            id = idsToNames.lastKey() + 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String add(String fileName, byte[] fileContent) {
        try {
            return pool.submit(
                () -> {
                    File file = new File(PATH_TO_DATA_DIR + fileName);
                    if (file.exists()) {
                        return null;
                    }

                    try {
                        Files.write(file.toPath(), fileContent);
                        return saveIdToMap(fileName);
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private synchronized String saveIdToMap(String fileName) {
        idsToNames.put(id, fileName);
        saveMapToFile();
        return String.valueOf(id++);
    }

    private synchronized void saveMapToFile() {
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
                .forEach(s -> idsToNames.put(Integer.parseInt(s[0]), s[1]));
        } catch (IOException ignored) {
        }
    }

    AbstractFile get(String identifier, boolean isId) {
        String fileName = getFilenameFromId(identifier, isId);
        String pathToFile = PATH_TO_DATA_DIR + fileName;
        if (!new File(pathToFile).exists()) {
            return null;
        }
        byte[] content = getFileContents(Paths.get(pathToFile));
        return new AbstractFile(identifier, content);
    }

    boolean delete(String identifier, boolean isId) {
        try {
            String fileName = getFilenameFromId(identifier, isId);
            Path p = Paths.get(PATH_TO_DATA_DIR + fileName);
            boolean wasDeleted = Files.deleteIfExists(p);
            idsToNames.values().remove(fileName);
            saveMapToFile();
            return wasDeleted;
        } catch (IOException e) {
            return false;
        }
    }

    private String getFilenameFromId(String identifier, boolean isId) {
        return isId
            ? idsToNames.getOrDefault(Integer.parseInt(identifier), "error")
            : identifier;
    }

    boolean fileNameIsNotInUse(String fileName) {
        return !idsToNames.containsValue(fileName);
    }
}
