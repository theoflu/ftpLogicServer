package com.yasu.service.impl;

import com.yasu.clientStuff.ClientRequests;
import com.yasu.entity.UserDto;
import com.yasu.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {
    private final ClientRequests userlists;
    @Override
    public List<UserDto> getUsers (){
        List<UserDto> userlist = userlists.userList().getBody();//diğer apiden bilgileri aldık.
      return userlist;
    }

    @Override
    public ResponseEntity<?> createUserDirectories(String path) {
        try {//TODO diğer service istek atsak orda öyle yapsaksss?
            File file = new File(path);
            File file2 = new File(path+"\\TrashCan");
            if (!file.exists()) {
                if (file.mkdir() && file2.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create directories."+ e.getMessage());

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Directory is created!");
    }
}
