package com.github.browep.efh;

import com.github.browep.efh.data.TransferProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Observable {

    public static final File OUTPUT_DIR = new File("/tmp/client_dl");
    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private String hostName;
    private int portNumber;
    private long totalReceivedBytes = 0;
    private BigInteger totalWeiSent = BigInteger.ZERO;

    final ExecutorService exService = Executors.newSingleThreadExecutor();
    private FileHubAdapter fileHubAdapter;
    private File outputFile;
    private long fileSize;
    private Thread thread;

    private ReentrantLock lock = new ReentrantLock();

    private void setState(State state) {
        this.state = state;
        setChanged();
        exService.execute(this::notifyObservers);
    }

    public enum State {
        NOT_STARTED("Not Started"), CONNECTED_TO_SERVER("Connected to Server"), CONTRACT_CREATED("Contract Created"),
        RECEIVING_FILE("Receiving File"), SAVING_FILE("Saving File"), DONE("Done"), PAUSED("Paused");

        public final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

    }

    private State state = State.NOT_STARTED;

    public Client(String hostName, int portNumber, long fileSizeBytes) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.fileSize = fileSizeBytes;
    }

    public void start() {
        thread = new Thread(this::execute);
        thread.start();
    }

    public void pause() {
        lock.lock();
    }

    public void resume() {
        lock.unlock();
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

            OUTPUT_DIR.mkdirs();
            outputFile = File.createTempFile("transfer", ".mp4", OUTPUT_DIR );
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] bytes = new byte[Constants.CHUNK_SIZE];

            int val = 0;

            logger.info("Receiving the file");
            setState(State.RECEIVING_FILE);
            while ((val = in.read(bytes, 0, bytes.length)) > 0 && totalWeiSent.compareTo(fileCostInWei()) <= 0) {

                if (lock.isLocked()) {
                    logger.info("waiting");

                    lock.lock();
                    try {
                        setState(State.PAUSED);
                        logger.info("continuing");
                        setState(State.RECEIVING_FILE);
                    } finally {
                        lock.unlock();
                    }
                }

                fos.write(bytes, 0, val);
                fos.flush();
                totalReceivedBytes += val;
                logger.info("received: " + totalReceivedBytes + "/" + fileSize + " creating transaction.");

                BigInteger weiToSend;
                if (totalReceivedBytes != fileSize) {
                    weiToSend = TransferProcessor.getWeiValueOfBytesSent(fileSize, totalReceivedBytes, fileCostInWei(), logger);

                } else {
                    logger.info("received all of the file: " + totalReceivedBytes);
                    weiToSend = fileCostInWei();
                }

                String signedAmount = fileHubAdapter.signAndSerialize(weiToSend);
                logger.info("signing amount of: " + weiToSend);
                logger.info("sending: " + signedAmount);
                out.println(signedAmount);

                totalWeiSent = weiToSend;

                if (totalWeiSent.compareTo(fileCostInWei()) >= 0) {
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
        return fileSize;
    }

    public BigInteger getWeiSent() {
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