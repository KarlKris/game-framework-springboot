package com.li.client.controller;

import de.felixroske.jfxsupport.FXMLController;
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

}
