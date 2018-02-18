package com.github.browep.efh;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.sample.contracts.generated.FileTransfer;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import org.web3j.utils.Numeric;

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

    public String createRedeemTx(int percent) {
        Function function = new Function(
                "redeem",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(percent)),
                Collections.<TypeReference<?>>emptyList());

        String data = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(BigInteger.valueOf(percent),
                ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT, fileTransfer.getContractAddress(), data);
        byte[] signedTx = TransactionEncoder.signMessage(rawTransaction, credentials);

        return Numeric.toHexString(signedTx);
    }

    public EthSendTransaction sendRedeemTx(String redeemTx) throws IOException {

       return web3j.ethSendRawTransaction(redeemTx).send();
    }


    /**
     * create param hash for the redeem function to server
     */
    public String prepareParamHashToServer(int percent) {
    		String paramHash = null;
    		//encode Function
    		Function function = new Function(
                "redeem",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(percent)),
                Collections.<TypeReference<?>>emptyList());

    		String binaryEncFunction = FunctionEncoder.encode(function);
    		String data = binaryEncFunction; // + fileTransfer.

    		BigInteger nonce = BigInteger.valueOf(-1); //fileTransfer.g.getNonce();

    		//encode Transaction
    		RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce,
                    ManagedTransaction.GAS_PRICE,
                    Contract.GAS_LIMIT,
                    fileTransfer.getContractAddress(),
                    BigInteger.valueOf(0),
                    data);

    		// Sign Transaction
    		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

    		paramHash = Numeric.toHexString(signedMessage);

    		return paramHash;
    }

    /**
     * create param hash for the redeem function to server
     */
    public String prepareParamHashToServerEthJ(int percent) {
    		String paramHash = null;
    		Function function = new Function(
                "redeem",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(percent)),
                Collections.<TypeReference<?>>emptyList());

    		String binaryEncFunction = FunctionEncoder.encode(function);
    		String data = binaryEncFunction; // + fileTransfer.

    		BigInteger nonce = BigInteger.valueOf(-1); //fileTransfer.getNonce();

    		byte[] senderPrivateKey = Hex.decode("c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3");
            //byte[] fromAddress = ECKey.fromPrivate(senderPrivateKey).getAddress();
            Transaction tx = new Transaction(
                    ByteUtil.bigIntegerToBytes(nonce),
                    ByteUtil.longToBytesNoLeadZeroes(0),
                    ByteUtil.longToBytesNoLeadZeroes(200000),
                    fileTransfer.getContractAddress().getBytes(),
                    ByteUtil.bigIntegerToBytes(BigInteger.valueOf(1)),  // 1_000_000_000 gwei, 1_000_000_000_000L szabo, 1_000_000_000_000_000L finney, 1_000_000_000_000_000_000L ether
                    data.getBytes(),
                    Integer.valueOf(1));

            tx.sign(ECKey.fromPrivate(senderPrivateKey));

    		paramHash = Numeric.toHexString(tx.getHash());

    		return paramHash;
    }

    public void decryptParamHashFromCLient(String hex) {
    		byte[] signedMessage  = Numeric.hexStringToByteArray(hex);
    		Transaction transaction1 = new Transaction(signedMessage);
    		transaction1.rlpParse();
    		System.out.println("Trx decoded: "+ transaction1.getContractAddress());
    }

}
