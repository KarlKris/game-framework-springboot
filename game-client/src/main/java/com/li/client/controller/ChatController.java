package com.li.client.controller;

import cn.hutool.core.date.DateUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@FXMLController
public class ChatController {

    @FXML
    private TextArea chatBox;

    public void addMessage(String sender, String msg) {
        Platform.runLater(() -> chatBox.appendText(DateUtil.date() + "  " + sender + "  :  " +  msg + "\n"));
    }

    public void clearMessage() {
        Platform.runLater(() -> chatBox.clear());
    }

}
