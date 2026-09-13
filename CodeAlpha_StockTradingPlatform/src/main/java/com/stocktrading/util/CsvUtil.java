package com.stocktrading.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV read/write helper used by the file-based repositories.
 * Kept intentionally simple: fields must not contain commas or newlines.
 */
public final class CsvUtil {

    private CsvUtil() {
    }

    /**
     * Reads all lines of a CSV file (excluding the header row) and splits each
     * line into fields. Returns an empty list if the file does not exist.
     */
    public static List<String[]> readRows(Path path) {
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(path)) {
            return rows;
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // skip header
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data file: " + path, e);
        }
        return rows;
    }

    /**
     * Writes a header line plus one line per row to the given file, creating
     * parent directories as needed. Overwrites any existing file.
     */
    public static void writeRows(Path path, String header, List<String> rows) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(header).append(System.lineSeparator());
            for (String row : rows) {
                sb.append(row).append(System.lineSeparator());
            }
            Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write data file: " + path, e);
        }
    }
}
