package com.li.client.controller;

import cn.hutool.core.convert.Convert;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@FXMLController
public class StatController {

    @FXML
    private Label requestNum;
    @FXML
    private Label responseNum;
    @FXML
    private Label lastTime;
    @FXML
    private Label avgTime;
    @FXML
    private Label largestTime;


    public void incrementRequest() {
        Integer curNum = Convert.toInt(requestNum.getText(), 0);
        String value = String.valueOf(curNum + 1);
        Platform.runLater(() -> requestNum.setText(value));
    }

    public void incrementResponse(long avgTime) {
        Integer curNum = Convert.toInt(responseNum.getText(), 0);
        String value = String.valueOf(curNum + 1);

        Platform.runLater(() -> {
            responseNum.setText(value);
            this.avgTime.setText(String.valueOf((avgTime)));
        });
    }

}
