package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.sample.contracts.generated.FileTransfer;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.IOException;
import java.math.BigInteger;

public class FileHubAdapter {

    private static final Logger log = LoggerFactory.getLogger(FileHubAdapter.class);
    private final Web3j web3j;
    private final Credentials credentials;
    private FileTransfer fileTransfer;
    private String clientAddr;
    private String serverAddr;
    private BigInteger fileHash;

    public FileHubAdapter(String privKey) throws IOException {
        web3j = Web3j.build(new HttpService("http://localhost:7545"));
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        credentials = Credentials.create(privKey);
    }

    public static FileHubAdapter load(String contractAddress, String privKey) throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter(privKey);
        fileHubAdapter.load(contractAddress);
        return fileHubAdapter;
    }

    private void load(String contractAddress) throws Exception {
        fileTransfer = FileTransfer.load(contractAddress, web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT);
        clientAddr = fileTransfer.getClient().send();
        serverAddr = fileTransfer.getServer().send();
        fileHash = fileTransfer.getFileHash().send();
    }

    /**
     * deploy
     * @return contract address
     * @throws Exception
     * @param clientAddr
     * @param serverAddr
     * @param fileHashStr
     * @param initialWeiValue
     */
    public String deploy(String clientAddr, String serverAddr, String fileHashStr, BigInteger initialWeiValue) throws Exception {
        fileHash = new BigInteger(fileHashStr, 16);
        this.clientAddr = clientAddr;
        this.serverAddr = serverAddr;
        fileTransfer = FileTransfer.deploy(web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT,
                initialWeiValue,
                this.clientAddr,
                this.serverAddr,
                fileHash, BigInteger.valueOf(Long.MAX_VALUE)
                ).send();

        return fileTransfer.getContractAddress();
    }

    public BigInteger getClientBalance() throws IOException {
        return web3j.ethGetBalance(clientAddr, DefaultBlockParameterName.PENDING).send().getBalance();
    }

    public BigInteger getServerBalance() throws IOException {
        return web3j.ethGetBalance(serverAddr, DefaultBlockParameterName.PENDING).send().getBalance();
    }

    public BigInteger getContractBalance() throws IOException {
        return web3j.ethGetBalance(fileTransfer.getContractAddress(), DefaultBlockParameterName.PENDING).send().getBalance();
    }

    public String getContractAddress() {
        return fileTransfer.getContractAddress();
    }

    public BigInteger getFileHash() throws Exception {
        return fileTransfer.getFileHash().send();
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public BigInteger getCurrentBlock() throws IOException {
        return web3j.ethBlockNumber().send().getBlockNumber();
    }

    public BigInteger getExpirationBlock() throws Exception {
        return fileTransfer.getExpirationBlock().send();
    }

    /**
     *
     * @param percent percent of the transaction to redeem ( 1-100 )
     * @return hash of the transaction
     * @throws Exception
     */
    public String redeem(int percent) throws Exception {
        TransactionReceipt transactionReceipt = fileTransfer.redeem(BigInteger.valueOf(percent)).send();
        String txHash = transactionReceipt.getTransactionHash();
        log.info("redeemHash: " + txHash);
        return txHash;

    }
}
