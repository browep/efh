package com.github.browep.efh;

import com.github.browep.efh.ui.Gui;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {

    private static Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {

        if (args.length != 2) {
            logger.error(
                    "Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        Client client = new Client(hostName, portNumber, 100);

        Gui.construct(client);

        Gui.launch();

        logger.info("GUI exited");

    }

}
