package com.li.client.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.li.client.network.ClientNetworkService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@Slf4j
@FXMLController
public class LoginController {

    /** 服务器地址 **/
    @FXML
    public TextField addressText;
    /** 服务器地址 **/
    @FXML
    public TextField portText;
    /** 服务器地址 **/
    @FXML
    public TextField accountText;
    /** 服务器地址 **/
    @FXML
    public Button loginBtn;


    @Resource
    private ClientNetworkService networkService;

    /**
     * 登陆
     */
    public void login() {
        String address = addressText.getText();
        String port = portText.getText();
        String account = accountText.getText();

        if (StrUtil.isBlank(address) || StrUtil.isBlank(port) || StrUtil.isBlank(account)) {
            return;
        }

        try {
            networkService.login(address, Convert.toInt(port), account);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
