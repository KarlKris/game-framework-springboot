package com.li.gamemanager.common.service.impl;


import com.li.gamemanager.common.entity.User;
import com.li.gamemanager.common.exception.ManagerBadRequestException;
import com.li.gamemanager.common.model.DefaultUser;
import com.li.gamemanager.common.properties.FileProperties;
import com.li.gamemanager.common.repository.UserRepository;
import com.li.gamemanager.common.service.UserReactiveService;
import com.li.gamemanager.utils.FileUtils;
import com.li.gamemanager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author li-yuanwen
 * @date 2021/6/12 17:52
 **/
@Slf4j
@Service
public class UserReactiveServiceImpl implements UserReactiveService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileProperties properties;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostConstruct
    private void init() {
        Flux.fromArray(DefaultUser.values())
                .subscribe(defaultUser
                        -> {
                    String userName = defaultUser.name().toLowerCase();
                    userRepository.existsById(userName)
                            .filter(exist -> !exist)
                            .subscribe(exist -> {
                                String pwd = passwordEncoder.encode(defaultUser.getPwd());
                                userRepository.save(new User(userName, pwd, defaultUser.getRole().name()))
                                        .subscribe(user -> log.warn("新用户[{}],密码[{}]", user.getUserName(), user.getPassword()));
                            });
                });
    }


    @Override
    public Mono<User> findByName(String userName) {
        return userRepository.findById(userName);
    }

    @Override
    public Mono<User> createUser(User newUser) {
        return userRepository.existsById(newUser.getUserName()).flatMap(exist -> {
            if (exist) {
                return Mono.error(new ManagerBadRequestException("用户[" + newUser.getUserName() + "]已存在"));
            }
            return userRepository.save(newUser);
        });
    }

    @Override
    public Mono<User> updateUser(User updateUser) {
        return userRepository.save(updateUser);
    }

    @Override
    public Mono<User> changePwd(String username, String encryptPassword) {
        return findByName(username).flatMap(user -> {
            if (user != null) {
                user.changePwd(encryptPassword);
            }
            return updateUser(user);
        });
    }

    @Override
    public Mono<Map<String, String>> updateAvatar(MultipartFile multipartFile) {
        // 文件大小验证
        FileUtils.checkSize(properties.getAvatarMaxSize(), multipartFile.getSize());
        // 验证文件上传的格式
        String image = "gif jpg png jpeg";
        String fileType = FileUtils.getExtensionName(multipartFile.getOriginalFilename());
        if (fileType != null && !image.contains(fileType)) {
            throw new ManagerBadRequestException("文件格式错误！, 仅支持 " + image + " 格式");
        }
        return SecurityUtils.getCurrentUsername().flatMap(userName -> {
            return findByName(userName).flatMap(user -> {
                String oldPath = user.getAvatarPath();
                File file = FileUtils.upload(multipartFile, properties.getPath().getAvatar());
                user.setAvatarPath(Objects.requireNonNull(file).getPath());
                user.setAvatarName(file.getName());
                userRepository.save(user);
                if (!StringUtils.isEmpty(oldPath)) {
                    FileUtils.del(oldPath);
                }
                return Mono.just(Collections.singletonMap("avatar", file.getName()));
            });
        });
    }

}
