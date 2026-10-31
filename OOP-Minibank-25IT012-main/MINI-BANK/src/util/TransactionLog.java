package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public class TransactionLog {
    public static void append(Path file, String line) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(file, Collections.singletonList(line),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }
}
