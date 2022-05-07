package com.li.client;

import com.li.client.controller.MainController;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author li-yuanwen
 * @date 2021/12/13
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.li"})
public class ClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ClientApplication.class, MainController.class, args);
    }
}
