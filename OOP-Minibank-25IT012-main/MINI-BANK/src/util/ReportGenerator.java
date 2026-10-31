package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {
    public static void generateReport(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int fileCount = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         END-OF-DAY SUMMARY REPORT      \n");
        sb.append("========================================\n");

        if (Files.exists(logsDir) && Files.isDirectory(logsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry) && entry.toString().endsWith(".log")) {
                        fileCount++;
                        BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                        sb.append("Log: ").append(entry.getFileName())
                          .append(" | Size: ").append(attrs.size()).append(" bytes")
                          .append(" | Modified: ").append(attrs.lastModifiedTime()).append("\n");

                        try (BufferedReader reader = Files.newBufferedReader(entry)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty()) {
                                    continue;
                                }
                                String[] parts = line.split("\\s+");
                                if (parts.length >= 3) {
                                    String type = parts[0].toUpperCase();
                                    long amt = Long.parseLong(parts[2]);
                                    if (type.equals("DEPOSIT")) {
                                        totalDeposits += amt;
                                    } else if (type.equals("WITHDRAW")) {
                                        totalWithdrawals += amt;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        sb.append("----------------------------------------\n");
        sb.append("Total Log Files Processed : ").append(fileCount).append("\n");
        sb.append("Total Deposits Amount     : Rs. ").append(totalDeposits).append("\n");
        sb.append("Total Withdrawals Amount  : Rs. ").append(totalWithdrawals).append("\n");
        sb.append("Net Volume                : Rs. ").append(totalDeposits - totalWithdrawals).append("\n");
        sb.append("========================================\n");

        if (reportFile.getParent() != null) {
            Files.createDirectories(reportFile.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
            writer.write(sb.toString());
        }
    }
}
