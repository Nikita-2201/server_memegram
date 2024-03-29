package com.example.demo.service;

import com.example.demo.entity.ImageModel;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.exception.ImageNotFoundException;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUpLoadService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageUpLoadService.class);

    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;

    @Autowired
    public ImageUpLoadService(ImageRepository imageRepository, UserRepository userRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }


    private byte[] compressBytes(byte[] data){//сжатие фотографии(потеря качества и тд)
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()){
            int count =deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try{
            outputStream.close();
        } catch (IOException e){
            LOG.error("Cannot compressed Bytes");
        }
        System.out.println("Compressed Image Byte Size - "+ outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] data){//"Раздувания" битов(для Angular)
        Inflater inflater= new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try{
            while (!inflater.finished()){
                int count=inflater.inflate(buffer);
                outputStream.write(buffer,0,count);
            }
            outputStream.close();
        }catch (IOException | DataFormatException e){
            LOG.error("Cannot decomepress Bytes");
        }
        return outputStream.toByteArray();
    }

    public ImageModel uploadImageToUser(MultipartFile multipartFile, Principal principal) throws IOException{
        User user = getUserByPrincipal(principal);
        LOG.info("Uploading image profile to User: {}", user.getName());

        ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }
        ImageModel imageModel= new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImageBytes(compressBytes(multipartFile.getBytes()));//для записи в бд
        imageModel.setFile_name(multipartFile.getOriginalFilename());

        return  imageRepository.save(imageModel);
    }

    public ImageModel uploadImageToPost(MultipartFile file, Long postId, Principal principal) throws IOException{
        User user = getUserByPrincipal(principal);

        Post post = user.getPosts()
                .stream()
                        .filter(p->p.getId().equals(postId))
                                .collect(toSinglePostCollector());


        ImageModel postProfile = imageRepository.findByPostId(post.getId()).orElse(null);

        ImageModel imageModel= new ImageModel();
        imageModel.setPostId(post.getId());
        //imageModel.setImageBytes(multipartFile.getBytes());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setFile_name(file.getOriginalFilename());

        LOG.info("Upload image to Post: {}", post.getId());
        return imageRepository.save(imageModel);
    }

    //метод который поможет вернуть фотографию для пользователя
    public ImageModel getImageToUser(Principal principal){
        User user = getUserByPrincipal(principal);
        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if(ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }

        return imageModel;
    }

    public ImageModel getImageToPost(Long postId){
        ImageModel imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(()->new ImageNotFoundException("Cannot find image to Post: " + postId));
        if(!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }

        return imageModel;
    }


    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found with username " + username));
    }


    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }


}
