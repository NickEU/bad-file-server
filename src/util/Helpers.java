package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Helpers {
    public static byte[] getFileContents(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }
    }
}
