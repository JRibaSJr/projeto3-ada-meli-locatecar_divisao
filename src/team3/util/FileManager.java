package team3.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    static {
        createDirectoriesIfNotExists();
    }

    private static void createDirectoriesIfNotExists() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(REPORTS_DIR));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretórios: " + e.getMessage());
        }
    }

    public static void saveToFile(String fileName, List<String> lines) throws IOException {
        Path filePath = Paths.get(DATA_DIR, fileName);
        Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Stream<String> readFromFile(String fileName) throws IOException {
        Path filePath = Paths.get(DATA_DIR, fileName);
        if (Files.exists(filePath)) {
            return Files.lines(filePath);
        }
        return Stream.empty();
    }

    public static void saveReport(String reportName, String content) throws IOException {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String fileName = reportName + "_" + timestamp + ".txt";
        Path filePath = Paths.get(REPORTS_DIR, fileName);
        Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE);
        System.out.println("Relatório salvo em: " + filePath.toAbsolutePath());
    }

    public static boolean fileExists(String fileName) {
        return Files.exists(Paths.get(DATA_DIR, fileName));
    }

    public static void writeObjectToFile(Object obj, String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(Paths.get(DATA_DIR, fileName).toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
        }
    }

    public static Object readObjectFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(Paths.get(DATA_DIR, fileName).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        }
    }

    public static void appendToFile(String fileName, String content) throws IOException {
        Path filePath = Paths.get(DATA_DIR, fileName);
        Files.write(filePath, (content + System.lineSeparator()).getBytes(), 
                   StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}