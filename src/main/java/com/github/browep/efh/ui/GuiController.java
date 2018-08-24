package com.github.browep.efh.ui;

import com.github.browep.efh.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    private final TextField contractTextField;
    private final TextField filePathField;
    private final Label downloadLabel;
    private final Label etherLabel;
    private EventHandler<ActionEvent> doPause;
    private EventHandler<ActionEvent> doResume;

    public GuiController(Scene scene, Client client) {
        this.scene = scene;

        contractTextField = (TextField) scene.lookup("#contract_address_field");
        filePathField = (TextField) scene.lookup("#file_location_field");

        startButton = (Button) scene.lookup("#start");
        statusLabel = (Label) scene.lookup("#status");
        downloadProgress = (ProgressBar) scene.lookup("#download_progress");
        etherProgress = (ProgressBar) scene.lookup("#ether_sent_progress");
        etherProgress.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        downloadLabel = (Label) scene.lookup("#download_count");
        etherLabel = (Label) scene.lookup("#ether_count");

        this.client = client;
        client.addObserver(this);

        doPause = event1 -> {
            client.pause();
            startButton.setOnAction(doResume);
            startButton.setText("Resume");
        };

        doResume = event1 -> {
            client.resume();
            startButton.setOnAction(doPause);
            startButton.setText("Pause");
        };

        startButton.setOnAction(
                event -> {
                    logger.info("starting download");
                    client.start();
                    startButton.setOnAction(doPause);
                    startButton.setText("Pause");
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
        statusLabel.setText("Status: " + state.displayName);
        long totalReceivedBytes = client.getTotalReceivedBytes();
        long totalFileBytes = client.getTotalFileBytes();
        downloadProgress.setProgress(BigDecimal.valueOf(totalReceivedBytes).divide(BigDecimal.valueOf(totalFileBytes), 3, RoundingMode.HALF_EVEN).doubleValue());
        downloadLabel.setText(totalReceivedBytes + " / " + totalFileBytes + " bytes");

        BigInteger weiSent = client.getWeiSent();
        BigInteger fileCostInWei = client.fileCostInWei();
        etherLabel.setText(fileCostInWei.subtract(weiSent) + " / " + fileCostInWei + " Wei");
        etherProgress.setProgress(1 - new BigDecimal(weiSent).divide(BigDecimal.valueOf(fileCostInWei.longValue()), 3, RoundingMode.HALF_EVEN).doubleValue());

        if (client.getContractAddress() != null) {
            contractTextField.setText(client.getContractAddress());
        }

        if (client.getDlFilePath() != null) {
            filePathField.setText(client.getDlFilePath());
        }

    }


}
