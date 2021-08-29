package com.li.gamemanager.common.service.impl;

import com.xaweb.manager.common.entity.User;
import com.xaweb.manager.common.exception.BadRequestException;
import com.xaweb.manager.common.model.DefaultUser;
import com.xaweb.manager.common.properties.FileProperties;
import com.xaweb.manager.common.repository.UserRepository;
import com.xaweb.manager.common.service.UserService;
import com.xaweb.manager.utils.FileUtil;
import com.xaweb.manager.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author li-yuanwen
 * @date 2021/6/12 17:52
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileProperties properties;
    @Autowired
    private UserCacheService userCacheService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        for (DefaultUser defaultUser : DefaultUser.values()) {
            User user = findByName(defaultUser.name().toLowerCase());
            if (user != null) {
                continue;
            }
            String pwd = passwordEncoder.encode(defaultUser.getPwd());
            log.error("新用户[{}],加密密码[{}],明文密码[{}]", defaultUser.name(), pwd, defaultUser.getPwd());
            user = new User(defaultUser.name().toLowerCase(), pwd, defaultUser.getRole());
            createUser(user);
        }
    }

    @Override
    public User findByName(String userName) {
        return userRepository.findById(userName).orElse(null);
    }

    @Override
    public void createUser(User newUser) {
        userRepository.save(newUser);
    }

    @Override
    public void updateUser(User updateUser) {
        userRepository.save(updateUser);
    }

    @Override
    public void changePwd(String username, String encryptPassword) {
        User user = findByName(username);
        if (user != null) {
            user.changePwd(encryptPassword);
        }
        updateUser(user);
    }

    @Override
    public Map<String, String> updateAvatar(MultipartFile multipartFile) {
        // 文件大小验证
        FileUtil.checkSize(properties.getAvatarMaxSize(), multipartFile.getSize());
        // 验证文件上传的格式
        String image = "gif jpg png jpeg";
        String fileType = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        if(fileType != null && !image.contains(fileType)){
            throw new BadRequestException("文件格式错误！, 仅支持 " + image +" 格式");
        }
        User user = findByName(SecurityUtils.getCurrentUsername());
        String oldPath = user.getAvatarPath();
        File file = FileUtil.upload(multipartFile, properties.getPath().getAvatar());
        user.setAvatarPath(Objects.requireNonNull(file).getPath());
        user.setAvatarName(file.getName());
        userRepository.save(user);
        if (!StringUtils.isEmpty(oldPath)) {
            FileUtil.del(oldPath);
        }
        @NotBlank String username = user.getUserName();
        flushCache(username);
        return new HashMap<String, String>(1) {{
            put("avatar", file.getName());
        }};
    }

    /**
     * 清理 登陆时 用户缓存信息
     *
     * @param username /
     */
    private void flushCache(String username) {
        userCacheService.cleanUserCache(username);
    }
}
