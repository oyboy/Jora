package com.main.Jora.repositories;

import com.main.Jora.models.UserAvatar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAvatarRepository extends CrudRepository<UserAvatar, Long> {
    UserAvatar findByUserId(Long userId);
}
