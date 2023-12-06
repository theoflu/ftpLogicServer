package com.yasu.service;

import com.yasu.entity.UserDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServerService {
    List<UserDto> getUsers ();
    ResponseEntity<?>  createUserDirectories(String path);
}
