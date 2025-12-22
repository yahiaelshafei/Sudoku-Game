package controller;

import model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LogManager {
    private static LogManager instance;
    private static final String LOG_FILE = "games/incomplete/log.txt";
    
    private LogManager() {}
    
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    public void logAction(UserAction action) throws IOException {
        Path logPath = Paths.get(LOG_FILE);
        
        if (!Files.exists(logPath)) {
            Files.createFile(logPath);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(logPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(action.toString());
            writer.newLine();
            writer.flush();
        }
    }
    
    public void logAction(String actionString) throws IOException {
        Path logPath = Paths.get(LOG_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(logPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(actionString);
            writer.newLine();
            writer.flush();
        }
    }
    
    public UserAction getLastAction() {
        Path logPath = Paths.get(LOG_FILE);
        
        if (!Files.exists(logPath)) {
            return null;
        }
        
        try {
            List<String> lines = Files.readAllLines(logPath);
            if (lines.isEmpty()) {
                return null;
            }
            
            String lastLine = lines.get(lines.size() - 1);
            return UserAction.fromString(lastLine);
            
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            return null;
        }
    }
    
    public void removeLastAction() throws IOException {
        Path logPath = Paths.get(LOG_FILE);
        
        if (!Files.exists(logPath)) {
            return;
        }
        
        List<String> lines = Files.readAllLines(logPath);
        
        if (!lines.isEmpty()) {
            lines.remove(lines.size() - 1);
            
            try (BufferedWriter writer = Files.newBufferedWriter(logPath)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }
    
    public void clearLog() throws IOException {
        Path logPath = Paths.get(LOG_FILE);
        
        if (Files.exists(logPath)) {
            Files.delete(logPath);
        }
    }
    
    public boolean hasLog() {
        Path logPath = Paths.get(LOG_FILE);
        
        if (!Files.exists(logPath)) {
            return false;
        }
        
        try {
            List<String> lines = Files.readAllLines(logPath);
            return !lines.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }
    
    public List<UserAction> getAllActions() {
        List<UserAction> actions = new ArrayList<>();
        Path logPath = Paths.get(LOG_FILE);
        
        if (!Files.exists(logPath)) {
            return actions;
        }
        
        try {
            List<String> lines = Files.readAllLines(logPath);
            for (String line : lines) {
                try {
                    actions.add(UserAction.fromString(line));
                } catch (Exception e) {
                    System.err.println("Error parsing log line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        
        return actions;
    }
}