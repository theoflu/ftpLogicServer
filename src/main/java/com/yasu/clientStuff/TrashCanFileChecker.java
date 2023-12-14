package com.yasu.clientStuff;

import com.yasu.entity.UserDto;
import com.yasu.service.impl.ServerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.List;
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class TrashCanFileChecker {
    private final ServerServiceImpl serverServiceImpl;
    @Scheduled(fixedDelay = 10000)
    public void check(){
        System.out.println("Yenilendi");
        List<UserDto> userlist= serverServiceImpl.getUsers();
        int i=0;
        while (i<userlist.size()) {
            String folderPath = "C:\\user\\" +userlist.get(i).getUsername() + "\\TrashCan"; // Silinecek dosyaların bulunduğu klasör yolu
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
                            System.out.println("BROMM DAHA ZAMANI VAR ZAMANIII "+ userlist.get(i).getUsername());
                    }
                }
            } else {
                System.out.println("Belirtilen klasör bulunamadı veya bir klasör değil.");
            }
            i++;
        }


    }
}
