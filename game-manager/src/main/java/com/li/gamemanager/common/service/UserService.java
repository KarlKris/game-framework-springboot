package com.li.gamemanager.common.service;

import com.li.gamemanager.common.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author li-yuanwen
 */
public interface UserService {


    /**
     * 根据用户名称查询
     * @param userName 用户名称
     * @return /
     */
    User findByName(String userName);


    /**
     * 创建用户
     * @param newUser 用户对象
     */
    void createUser(User newUser);


    /**
     * 更新用户
     * @param updateUser 更新后用户对象
     */
    void updateUser(User updateUser);


    /**
     * 修改密码
     * @param username 用户名
     * @param encryptPassword 密码
     */
    void changePwd(String username, String encryptPassword);

    /**
     * 修改头像
     * @param file 文件
     * @return /
     */
    Map<String, String> updateAvatar(MultipartFile file);

}
