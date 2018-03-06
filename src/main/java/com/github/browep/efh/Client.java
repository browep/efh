package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Int;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private String hostName;
    private int portNumber;
    private int desiredPercent;

    public Client(String hostName, int portNumber, int desiredPercent) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.desiredPercent = desiredPercent;
    }

    public void start() {
        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                InputStream in = socket.getInputStream();
        ) {

            FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
            fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

            out.println(fileHubAdapter.getContractAddress());

            File outputFile = File.createTempFile("transfer", ".mp4", new File("/tmp/client_dl"));
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] bytes = new byte[Constants.CHUNK_SIZE];

            int val = 0;
            long totalReceivedBytes = 0;

            int redeemPercent = 0;

            logger.info("Receiving the file");
            while ((val = in.read(bytes, 0, bytes.length)) > 0 && redeemPercent <= desiredPercent) {
                fos.write(bytes, 0, val);
                fos.flush();
                totalReceivedBytes += val;
                redeemPercent = BigDecimal.valueOf(totalReceivedBytes)
                        .divide(BigDecimal.valueOf(Constants.FILE_SIZE), 3, RoundingMode.HALF_EVEN)
                        .multiply(BigDecimal.valueOf(100))
                        .intValue();
                logger.info("received: " + totalReceivedBytes +"/" + Constants.FILE_SIZE + " creating transaction. received: " + redeemPercent +"%, desired precent: " + desiredPercent + "%");

                String redeemTx = fileHubAdapter.createRedeemTx(redeemPercent );
                logger.info("sending: " + redeemTx);
                logger.info("percent: " + redeemPercent);
                out.println(redeemTx);

            }

            logger.info("Received file: " + outputFile.getCanonicalPath());

            fos.flush();
            fos.close();

        } catch (UnknownHostException e) {
            logger.error("Don't know about host " + hostName, e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Couldn't get I/O for the connection to " +
                    hostName, e);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}