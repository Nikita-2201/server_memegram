package com.example.demo.web;

import com.example.demo.entity.ImageModel;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.service.ImageUpLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageUpLoadService imageUpLoadService;

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadImageToUser(@RequestParam("file")MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUpLoadService.uploadImageToUser(file, principal);
        return new ResponseEntity<>(new MessageResponse("Image Upload successfully"), HttpStatus.OK);
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<MessageResponse> uploadImageToPost(@PathVariable("postId") String postId,
                                                             @RequestParam("file") MultipartFile file,
                                                             Principal principal) throws IOException{
        imageUpLoadService.uploadImageToPost(file,Long.parseLong(postId),principal);
        return new ResponseEntity<>(new MessageResponse("Image upload Successfully"), HttpStatus.OK);
    }

    @GetMapping("/profile/image")
    public ResponseEntity<ImageModel> getImageForUser(Principal principal){
        ImageModel userImage = imageUpLoadService.getImageToUser(principal);
        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("/{postId}/image")
    public ResponseEntity<ImageModel> getImageForPost(@PathVariable("postId") String postId){
        ImageModel postImage = imageUpLoadService.getImageToPost(Long.parseLong(postId));
        return new ResponseEntity<>(postImage, HttpStatus.OK);
    }
}
