package com.github.browep.efh.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiController {

    private static Logger logger = LoggerFactory.getLogger(GuiController.class);

    private Scene scene;
    private final Button startButton;

    public GuiController(Scene scene) {
        this.scene = scene;

        startButton = (Button) scene.lookup("#start");

        startButton.setOnAction(event -> logger.info("starting download"));


    }
}
