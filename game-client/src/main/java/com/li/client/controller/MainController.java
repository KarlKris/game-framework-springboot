package com.li.client.controller;

import com.li.client.network.ClientNetworkService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@FXMLController
public class MainController  {

    /** 协议视图 **/
    @FXML
    public Pane protocolPane;
    /** 聊天室视图 **/
    @FXML
    public Pane chatroomPane;
    /** 消息视图 **/
    @FXML
    public Pane messagePane;
    /** 通讯数据统计视图 **/
    @FXML
    public Pane statPane;
    /** 左视图 **/
    @FXML
    public Pane leftPane;
    /** 左视图,登陆 **/
    @FXML
    public Pane loginPane;
    /** 左视图,主界面 **/
    @FXML
    public Pane indexPane;

    @Resource
    private ClientNetworkService networkService;


}
