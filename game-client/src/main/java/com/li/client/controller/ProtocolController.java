package com.li.client.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.client.network.ClientNetworkService;
import com.li.client.service.ProtocolService;
import com.li.network.message.SocketProtocol;
import com.li.network.protocol.InBodyMethodParameter;
import com.li.network.protocol.MethodParameter;
import com.li.network.protocol.ProtocolMethodCtx;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

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


    /** 协议下拉框 **/
    @FXML
    private ComboBox<String> protocolComboBox;
    /** 发送内容 **/
    @FXML
    private TextArea body;

    @Resource
    private ProtocolService protocolService;
    @Resource
    private ClientNetworkService clientNetworkService;
    @Resource
    private ObjectMapper objectMapper;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        protocolComboBox.getItems().clear();
        for (ProtocolMethodCtx ctx : protocolService.getMethodCtxHolder()) {
            SocketProtocol protocol = ctx.getProtocol();
            if (protocol.isPushProtocol()) {
                continue;
            }
            if (protocol.getModule() <= 1000) {
                continue;
            }
            String protocolName = protocol.getModule() + SEPARATOR
                    + protocol.getMethodId() + SEPARATOR + ctx.getMethod().getName();
            protocolComboBox.getItems().add(protocolName);
        }
    }

    public void onProtocolComboBoxAction() throws InstantiationException, IllegalAccessException, JsonProcessingException {
        String value = protocolComboBox.getValue();
        if (StrUtil.isEmpty(value)) {
            return;
        }

        String[] split = value.split(SEPARATOR);
        ProtocolMethodCtx protocolMethodCtx = protocolService.getProtocolMethodCtxBySocketProtocol(new SocketProtocol(Convert.toShort(split[0]), Convert.toByte(split[1])));
        if (protocolMethodCtx == null) {
            return;
        }

        String requestBody = null;
        MethodParameter[] params = protocolMethodCtx.getParams();
        for (MethodParameter param : params) {
            if (!(param instanceof InBodyMethodParameter)) {
                continue;
            }
            InBodyMethodParameter p = (InBodyMethodParameter) param;
            Class<?> aClass = p.getParameterClass();
            boolean basicType = ClassUtil.isBasicType(aClass);
            if (basicType) {
                Object defaultValue = getDefaultValue(aClass);
                if (defaultValue != null) {
                    requestBody = objectMapper.writeValueAsString(defaultValue);
                    break;
                }
            } else {
                Object instance = aClass.newInstance();
                requestBody = objectMapper.writeValueAsString(instance);
                break;
            }

        }

        body.clear();
        if (requestBody != null) {
            body.appendText(requestBody);
        }

    }


    public void sendProtocol() throws JsonProcessingException {
        String value = protocolComboBox.getValue();
        if (StrUtil.isEmpty(value)) {
            return;
        }

        String[] split = value.split(SEPARATOR);
        SocketProtocol protocol = new SocketProtocol(Convert.toShort(split[0]), Convert.toByte(split[1]));
        ProtocolMethodCtx protocolMethodCtx = protocolService.getProtocolMethodCtxBySocketProtocol(new SocketProtocol(Convert.toShort(split[0]), Convert.toByte(split[1])));
        if (protocolMethodCtx == null) {
            return;
        }
        Object requestBody = null;

        String requestBodyText = body.getText();
        if (StrUtil.isNotBlank(requestBodyText)) {
            MethodParameter[] params = protocolMethodCtx.getParams();
            for (MethodParameter param : params) {
                if (!(param instanceof InBodyMethodParameter)) {
                    continue;
                }
                InBodyMethodParameter p = (InBodyMethodParameter) param;
                Class<?> clz = p.getParameterClass();
                requestBody = objectMapper.readValue(requestBodyText, clz);
                break;
            }
        }

        clientNetworkService.send(protocol, requestBody);
    }

    private Object getDefaultValue(Class<?> clz) {
        if (Byte.TYPE.isAssignableFrom(clz)) {
            return (byte) 0;
        } else if (Short.TYPE.isAssignableFrom(clz)) {
            return (short) 0;
        } else if (Integer.TYPE.isAssignableFrom(clz)) {
            return 0;
        } else if (Long.TYPE.isAssignableFrom(clz)) {
            return 0L;
        } else if (Double.TYPE.isAssignableFrom(clz)) {
            return 0D;
        } else if (Character.TYPE.isAssignableFrom(clz)) {
            return '\u0000';
        } else if (Boolean.TYPE.isAssignableFrom(clz)) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

}
