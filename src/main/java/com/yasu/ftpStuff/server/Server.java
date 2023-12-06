package com.yasu.ftpStuff.server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class Server {




    public static void main(String[] args) {
        int port = 3456;
        startCheckTask();

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
    public  static void startCheckTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Server::check, 0, 1, TimeUnit.MINUTES);
    }

    public  static void check(){
        System.out.println("GİRDİMS");
        List<String> userlist=new  ArrayList();
        userlist.add("as3d");
        userlist.add("osman");
        int i=0;
        while (i<=userlist.size()) {
            String folderPath = "C:\\user\\" +userlist.get(i) + "\\TrashCan"; // Silinecek dosyaların bulunduğu klasör yolu
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                long currentTime = System.currentTimeMillis();
                if (files != null) {
                    for (File file : files) {
                        long lastModified = file.lastModified();
                        long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000; // 30 günün milisaniye cinsinden değeri
                        if (currentTime - lastModified > thirtyDaysInMillis) {
                            boolean isDeleted = file.delete();
                            if (isDeleted) {
                                System.out.println("Dosya başarıyla silindi: " + file.getAbsolutePath());
                            } else {
                                System.out.println("Dosya silinemedi: " + file.getAbsolutePath());
                            }
                        }
                        else
                            System.out.println("BROMM DAHA ZAMANI VAR ZAMANIII");
                    }
                }
            } else {
                System.out.println("Belirtilen klasör bulunamadı veya bir klasör değil.");
            }
            i++;
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
