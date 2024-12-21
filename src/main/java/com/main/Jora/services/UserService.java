package com.main.Jora.services;

import com.main.Jora.configs.CustomException;
import com.main.Jora.models.User;
import com.main.Jora.models.UserAvatar;
import com.main.Jora.repositories.UserAvatarRepository;
import com.main.Jora.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserAvatarRepository userAvatarRepository;

    public boolean createUser(User user){
        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) return false;
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Saving new user {}", user);
        userRepository.save(user);
        return true;
    }
    @CachePut(value = "user", key = "#user_id")
    public User editUser(Long user_id, User form)
            throws CustomException.ObjectExistsException {
        log.warn("Got user form: {} (id), {} (name), {} (email)", form.getId(), form.getUsername(), form.getEmail());
        User user = userRepository.findById(user_id).orElse(null);
        if (user == null) return null;
        log.warn("Updating user: {} (id), {} (name), {} (email)", user.getId(), user.getUsername(), user.getEmail());

        if (!form.getEmail().equals(user.getEmail())){
            if (userRepository.findByEmail(form.getEmail()) != null)
                throw new CustomException.ObjectExistsException("");
            user.setEmail(form.getEmail());
        }
        user.setUsername(form.getUsername());

        if (form.getPassword() != null){
            log.info("Changing password");
            if (!form.getPassword().equals(form.getConfirmPassword())){
                throw new IllegalArgumentException("Confirmation password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }
        userRepository.save(user);
        return user;
    }
    @Cacheable(value = "user", key = "#user_id")
    public User getUserById(Long user_id) {
        return userRepository.findById(user_id).orElse(null);
    }
    public void setAvatar(MultipartFile file, Long user_id) throws IOException{
        log.info("Image compressing: {}", file.getOriginalFilename());
        InputStream inputStream = file.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .scale(1) // Масштаб 1: оригинальный размер
                .outputQuality(0.5) // Качество сжатия до 50%
                .toOutputStream(os);

        UserAvatar userAvatar = findAvatarByUserId(user_id);
        userAvatar.setBytes(os.toByteArray());

        log.info("Saving avatar for user: {}", userAvatar.getUserId());
        userAvatarRepository.save(userAvatar);
    }
    public void deleteAvatarForUser(Long user_id){
        UserAvatar userAvatar = findAvatarByUserId(user_id);
        log.info("Deleting avatar for user: {}", userAvatar.getUserId());
        userAvatarRepository.delete(userAvatar);
    }
    public UserAvatar findAvatarByUserId(Long user_id){
        UserAvatar userAvatar = userAvatarRepository.findByUserId(user_id);
        if (userAvatar == null)
            try{
                return setDefaultAvatar(user_id);
            } catch (IOException e){
                log.error("Can't set default avatar: {}", e.getMessage());
                return null;
        }
        return userAvatar;
    }
    private UserAvatar setDefaultAvatar(Long user_id) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/static/images/default_avatar.jpg");
        if (inputStream == null) {
            throw new IOException("Default avatar not found");
        }
        byte[] avatarBytes = inputStream.readAllBytes();

        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setBytes(avatarBytes);
        userAvatar.setUserId(user_id);

        log.info("Saving default avatar for user: {}", userAvatar.getUserId());
        userAvatarRepository.save(userAvatar);

        return userAvatar;
    }
}