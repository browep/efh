package com.github.browep.efh;

import com.github.browep.efh.ui.Gui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMain {

    private static Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) {

        if (args.length != 3) {
            logger.error(
                    "Usage: java Client <host name> <port number> <file size in bytes>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        long fileSizeBytes = Long.parseLong(args[2]);

        Client client = new Client(hostName, portNumber, fileSizeBytes);

        Gui.construct(client);

        Gui.launch();

        logger.info("GUI exited");

    }

}
