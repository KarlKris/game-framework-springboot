package com.li.client.controller;

import com.li.client.service.ProtocolService;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.ProtocolMethodCtx;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author li-yuanwen
 * @date 2021/12/15
 */
@FXMLController
public class ProtocolController implements Initializable {

    private static final String SEPARATOR = "-";

    /** 协议视图 **/
    @FXML
    public Pane protocolPane;
    /** 协议下拉框 **/
    @FXML
    private ComboBox<String> protocolComboBox;

    @Resource
    private ProtocolService protocolService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        protocolComboBox.getItems().clear();
        for (ProtocolMethodCtx ctx : protocolService.getMethodCtxHolder()) {
            SocketProtocol protocol = ctx.getProtocol();
            String protocolName = protocol.getModule() + SEPARATOR
                    + protocol.getMethodId() + SEPARATOR + ctx.getMethod().getName();
            protocolComboBox.getItems().add(protocolName);
        }
    }
}
