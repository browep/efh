package com.github.browep.efh.ui;

import com.github.browep.efh.Client;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Observable;
import java.util.Observer;

public class GuiController implements Observer {

    private static Logger logger = LoggerFactory.getLogger(GuiController.class);

    private Scene scene;
    private final Button startButton;
    private Client client;
    private final Label statusLabel;
    private final ProgressBar downloadProgress;
    private final ProgressBar etherProgress;

    public GuiController(Scene scene, Client client) {
        this.scene = scene;

        startButton = (Button) scene.lookup("#start");
        statusLabel = (Label) scene.lookup("#status");
        downloadProgress = (ProgressBar) scene.lookup("#download_progress");
        etherProgress = (ProgressBar) scene.lookup("#ether_sent_progress");

        this.client = client;
        client.addObserver(this);

        startButton.setOnAction(
                event -> {
                    logger.info("starting download");
                    client.start();
                }

        );

        update(client, null);

    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(this::updateState);
    }

    private void updateState() {
        Client.State state = client.getState();
        logger.debug("status updated: " + state);
        statusLabel.setText("Status: "+ state.displayName);
        downloadProgress.setProgress(BigDecimal.valueOf(client.getTotalReceivedBytes()).divide(BigDecimal.valueOf(client.getTotalFileBytes()), 3, RoundingMode.HALF_EVEN).doubleValue());
        etherProgress.setProgress(BigDecimal.valueOf(client.getWeiSent()).divide(BigDecimal.valueOf(client.fileCostInWei().longValue()), 3, RoundingMode.HALF_EVEN).doubleValue());

    }


}
