package com.github.browep.efh;

import com.github.browep.efh.ui.Gui;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {

    private static Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {

        if (args.length != 3) {
            logger.error(
                    "Usage: java Client <host name> <port number> <percent>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        int desiredPercent = Integer.parseInt(args[2]);

        Client client = new Client(hostName, portNumber, desiredPercent);

        Gui.construct(client);

        Gui.launch();

    }

}
