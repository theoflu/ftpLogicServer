package com.yasu.ftpStuff.client;
import java.io.*;
import java.net.Socket;

public class client2 {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Sunucu adresi
        int serverPort = 3456; // Sunucu portu
        String sourceFolder = "C:\\Users\\yusuf\\OneDrive\\Masaüstü\\gond"; // Gönderilecek klasör
        String targetDirectory = "C:\\user\\as3d"; // Hedef dizin

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);

            // Klasör adını ve boyutunu sunucuya gönder
            sendFolderInfo(os, sourceFolder, targetDirectory);

            // Klasörü sunucuya gönder
            sendFolder(os, sourceFolder);

            System.out.println("Klasör gönderildi: " + sourceFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFolderInfo(OutputStream os, String sourceFolder, String targetDirectory) {
        File folder = new File(sourceFolder);
        long folderSize = calculateFolderSize(folder);

        PrintWriter writer = new PrintWriter(os, true);
        writer.println(folder.getName()); // Klasör adı
        writer.println(folderSize); // Klasör boyutu
        writer.println(targetDirectory); // Hedef dizin
    }

    private static long calculateFolderSize(File folder) {
        long size = 0;

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else if (file.isDirectory()) {
                        size += calculateFolderSize(file);
                    }
                }
            }
        }

        return size;
    }

    private static void sendFolder(OutputStream os, String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        sendFile(os, file, folderPath);
                    } else if (file.isDirectory()) {
                        sendFolder(os, file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static void sendFile(OutputStream os, File file, String sourceFolder) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;

        PrintWriter writer = new PrintWriter(os, true);
        writer.println(file.getAbsolutePath().replace(sourceFolder, "")); // Dosyanın yolu
        writer.println(file.length()); // Dosya boyutu

        while ((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        fis.close();
    }
}
