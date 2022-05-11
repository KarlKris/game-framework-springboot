package com.li.client.controller;

import com.li.client.ui.UiType;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@FXMLController
@FXMLView("/fxml/main.fxml")
public class MainController extends AbstractFxmlView implements Initializable {

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


    private Map<UiType, Pane> paneMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneMap = new EnumMap<>(UiType.class);
        paneMap.put(UiType.LOGIN, loginPane);
        paneMap.put(UiType.PLAYER_DETAILS, indexPane);

        switchUI(UiType.LOGIN);
    }


    public void switchUI(UiType type) {
        Pane pane = paneMap.get(type);
        if (pane == null) {
            return;
        }
        ObservableList<Node> children = leftPane.getChildren();
        children.clear();
        children.add(pane);
    }


}
