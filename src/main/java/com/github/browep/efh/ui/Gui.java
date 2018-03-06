package com.github.browep.efh.ui;

import com.github.browep.efh.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Gui extends Application {

    static Client client;

    public static void construct(Client client) {
        Gui.client = client;
    }

    public static void launch() {
        Application.launch(Gui.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("efh.fxml");
        Parent root = FXMLLoader.load(location);
        primaryStage.setTitle("Ethereum File Hub");
        Scene scene = new Scene(root, 800, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
