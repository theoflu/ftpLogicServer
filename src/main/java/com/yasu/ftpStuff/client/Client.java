package com.yasu.ftpStuff.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private String serverAddress;
    private int serverPort;


    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

    }

    public void sendFile(String filePath,String username) {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream os = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(os, true);

            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                long fileSize = file.length();
                sendFileNameAndSize(writer, filePath, fileSize,file.getName(),"C:\\user\\");
                sendFileData(os, file);
                System.out.println("Dosya gönderildi: " + "C:\\user\\");
                listFiles("C:\\user\\"+username);
            } else {
                System.out.println("Belirtilen dosya bulunamadı veya bir dosya değil.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileNameAndSize(PrintWriter writer, String filepath, long fileSize, String fileName,String targetDirectory) {
        writer.println(filepath);
        writer.println(fileSize);
        writer.println(fileName);
        writer.println(targetDirectory);
    }

    private void sendFileData(OutputStream os, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    public void listFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                System.out.println("Klasördeki Dosyalar:");
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println(file.getName());
                    }
                }
            } else {
                System.out.println("Klasör boş.");
            }
        } else {
            System.out.println("Belirtilen klasör bulunamadı veya bir klasör değil.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 3456);
        client.sendFile("C:\\Users\\yusuf\\OneDrive\\Masaüstü\\haftalık.txt","yasu");
    }
}
