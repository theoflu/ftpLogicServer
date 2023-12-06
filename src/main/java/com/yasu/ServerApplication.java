package com.yasu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
@SpringBootApplication
@EnableFeignClients
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        int port = 3456;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Sunucu çalışıyor...");


            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("İstemci bağlandı: " + clientSocket.getInetAddress());
                Thread clientThread = new Thread(() -> receiveFile(clientSocket));
                clientThread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }






    private static   void receiveFile(Socket clientSocket) {
        try {
            InputStream is = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String filePath = reader.readLine();
            if(filePath.equals("notFoundFile"))
            {
                System.out.println("Böyle bir dosya yok.");
                clientSocket.close();
            }
            else {
                if (filePath.equals("InsufficientStorageSpace")) {
                    System.out.println("Yeterli Alanınız Yok.");
                    clientSocket.close();
                } else {

                    //File file = new File(filePath);
                    long fileSize = Long.parseLong(reader.readLine());
                    String filename = reader.readLine();
                    String targetDirectory = reader.readLine();
                    String newFilePath = targetDirectory + filename;

                    FileOutputStream fos = new FileOutputStream(newFilePath);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;

                    System.out.println("Dosya alımı başlıyor: " + filename);
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        if (totalBytesRead >= fileSize) {
                            break;
                        }
                    }

                    System.out.println("Dosya alımı tamamlandı: " + filename);


                    // Sıkıştırılmış dosyayı aç
                    unzipFile(newFilePath, targetDirectory,filename);
                    fos.close();
                    clientSocket.close();
                    System.out.println("Client çıktı");
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private static void unzipFile(String zipFilePath, String targetDirectory,String filename) {
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry;
            int dotIndex = filename.lastIndexOf('.');

            String extension = (dotIndex != -1) ? filename.substring(dotIndex + 1) : "HATA";
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();

                //  uzantısana göre dosyalarını çıkart
                if (entryName.endsWith("."+extension)) {
                    // Dosya adını klasöre çevir ve varsa ilk '/' işaretine kadar olan kısmı kaldır
                    String modifiedEntryName = entryName.replace("/", File.separator);
                    if (modifiedEntryName.contains(File.separator)) {
                        modifiedEntryName = modifiedEntryName.substring(modifiedEntryName.indexOf(File.separator) + 1);
                    }

                    String entryPath = targetDirectory + File.separator + modifiedEntryName;

                    File entryFile = new File(entryPath);
                    entryFile.getParentFile().mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

