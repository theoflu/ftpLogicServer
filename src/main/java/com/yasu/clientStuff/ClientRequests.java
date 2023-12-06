package com.yasu.clientStuff;

import com.yasu.entity.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@FeignClient(url = "http://localhost:8080/user",name = "client")
public interface ClientRequests {

    @GetMapping("/list")
         ResponseEntity<List<UserDto>> userList();

}
