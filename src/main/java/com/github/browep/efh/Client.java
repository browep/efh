package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends Observable {

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private String hostName;
    private int portNumber;
    private int desiredPercent;
    private long totalReceivedBytes = 0;
    private long totalWeiSent = 0;

    final ExecutorService exService = Executors.newSingleThreadExecutor();
    private FileHubAdapter fileHubAdapter;
    private File outputFile;

    private void setState(State state) {
        this.state = state;
        setChanged();
        exService.execute(this::notifyObservers);
    }

    public enum State {
        NOT_STARTED("Not Started"), CONNECTED_TO_SERVER("Connected to Server"), CONTRACT_CREATED("Contract Created"),
        RECEIVING_FILE("Receiving File"), SAVING_FILE("Saving File"), DONE("Done");

        public final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

    }

    private State state = State.NOT_STARTED;

    public Client(String hostName, int portNumber, int desiredPercent) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.desiredPercent = desiredPercent;
    }

    public void start() {

        Thread thread = new Thread(this::execute);
        thread.start();
    }

    private void execute() {
        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                InputStream in = socket.getInputStream();
        ) {

            setState(State.CONNECTED_TO_SERVER);

            fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
            fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

            setState(State.CONTRACT_CREATED);

            out.println(fileHubAdapter.getContractAddress());

            outputFile = File.createTempFile("transfer", ".mp4", new File("/tmp/client_dl"));
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] bytes = new byte[Constants.CHUNK_SIZE];

            int val = 0;

            int redeemPercent = 0;

            logger.info("Receiving the file");
            setState(State.RECEIVING_FILE);
            while ((val = in.read(bytes, 0, bytes.length)) > 0 && redeemPercent <= desiredPercent) {


                fos.write(bytes, 0, val);
                fos.flush();
                totalReceivedBytes += val;
                redeemPercent = BigDecimal.valueOf(totalReceivedBytes)
                        .divide(BigDecimal.valueOf(Constants.FILE_SIZE), 3, RoundingMode.HALF_EVEN)
                        .multiply(BigDecimal.valueOf(100))
                        .intValue();
                logger.info("received: " + totalReceivedBytes + "/" + Constants.FILE_SIZE + " creating transaction. received: " + redeemPercent + "%, desired precent: " + desiredPercent + "%");

                String redeemTx = fileHubAdapter.createRedeemTx(redeemPercent);
                logger.info("sending: " + redeemTx);
                logger.info("percent: " + redeemPercent);
                out.println(redeemTx);

                totalWeiSent = fileCostInWei().divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(redeemPercent)).longValue();

                if (redeemPercent == 100) {
                    setState(State.SAVING_FILE);
                } else {
                    setState(State.RECEIVING_FILE);
                }
            }

            logger.info("Received file: " + outputFile.getCanonicalPath());
            setState(State.SAVING_FILE);

            fos.flush();
            fos.close();

            setState(State.DONE);

        } catch (UnknownHostException e) {
            logger.error("Don't know about host " + hostName, e);
        } catch (IOException e) {
            logger.error("Couldn't get I/O for the connection to " +
                    hostName, e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public State getState() {
        return state;
    }

    public long getTotalReceivedBytes() {
        return totalReceivedBytes;
    }

    public long getTotalFileBytes() {
        return Constants.FILE_SIZE;
    }

    public long getWeiSent() {
        return totalWeiSent;
    }

    public BigInteger fileCostInWei() {
        return Constants.INITIAL_WEI_VALUE;
    }

    @Nullable
    public String getContractAddress() {
        return fileHubAdapter != null ? fileHubAdapter.getContractAddress() : null;
    }

    @Nullable
    public String getDlFilePath() {
        return outputFile != null ? outputFile.getAbsolutePath() : null;
    }
}