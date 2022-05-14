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
public class MessageController {

    @FXML
    private TextArea messageArea;


    public void addInfoMessage(String msg) {
        Platform.runLater(() -> messageArea.appendText(DateUtil.date() + "  INFO   " + msg + "\n"));
    }

    public void addErrorMessage(String msg) {
        Platform.runLater(() -> messageArea.appendText(DateUtil.date() + "  ERROR  " + msg + "\n"));
    }

    public void clear() {
        Platform.runLater(() -> messageArea.clear());
    }

}
