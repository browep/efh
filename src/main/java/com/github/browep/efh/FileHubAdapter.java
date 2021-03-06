package com.github.browep.efh;

import java.io.IOException;
import java.math.BigInteger;

import com.github.browep.efh.data.TransferProcessor;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.sample.contracts.generated.FileTransfer;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import static org.ethereum.util.ByteUtil.bigIntegerToBytes;

public class FileHubAdapter {

    private static final Logger log = LoggerFactory.getLogger(FileHubAdapter.class);
    private final Web3j web3j;
    private final Credentials credentials;
    private FileTransfer fileTransfer;
    private String clientAddr;
    private String serverAddr;
    private BigInteger fileHash;

    public FileHubAdapter(String privKey) throws IOException {
        web3j = Web3j.build(new HttpService(Constants.ETH_NODE_URL));
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
     *
     * @param clientAddr
     * @param serverAddr
     * @param fileHashStr
     * @param initialWeiValue
     * @return contract address
     * @throws Exception
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
     * @param redeemValue value to redeem, in wei
     * @return hash of the transaction
     * @throws Exception
     */
    public String redeem(byte[] hash, ECKey.ECDSASignature ecdsaSignature, BigInteger redeemValue) throws Exception {
        SignatureParts signatureParts = new SignatureParts(ecdsaSignature).invoke();
        TransactionReceipt transactionReceipt = fileTransfer.redeem(
                hash,
                signatureParts.v,
                signatureParts.r,
                signatureParts.s,
                redeemValue
        ).send();
        String txHash = transactionReceipt.getTransactionHash();
        log.info("redeemHash: " + txHash);
        return txHash;

    }

    public HashAndSig sign(BigInteger valueInWei, ECKey ecKey) {
        byte[] bytes = ByteUtil.copyToArray(valueInWei);
        byte[] hash = Hash.sha3(bytes);
        ECKey.ECDSASignature ecdsaSignature = ecKey.sign(hash);
        return new HashAndSig(ecdsaSignature, hash);
    }

    public String signAndSerialize(BigInteger amountInWei) {
        ECKey ecKey = ECKey.fromPrivate(credentials.getEcKeyPair().getPrivateKey().toByteArray());
        HashAndSig hashAndSig = sign(amountInWei, ecKey);
        return TransferProcessor.serialize(hashAndSig, amountInWei);
    }

    public boolean isRedeemable(HashAndSig hashAndSig, BigInteger valueInWei) throws Exception {
        ECKey.ECDSASignature ecdsaSignature = hashAndSig.ecdsaSignature;
        SignatureParts signatureParts = new SignatureParts(ecdsaSignature).invoke();
        BigInteger v = signatureParts.getV();
        byte[] r = signatureParts.getR();
        byte[] s = signatureParts.getS();
        return fileTransfer.isRedeemable(hashAndSig.hash,
                v,
                r, s, valueInWei)
        .send();
    }

    public static class HashAndSig {
        public final ECKey.ECDSASignature ecdsaSignature;
        public final byte[] hash;

        public HashAndSig(ECKey.ECDSASignature ecdsaSignature, byte[] hash) {
            this.ecdsaSignature = ecdsaSignature;
            this.hash = hash;
        }
    }

    private class SignatureParts {
        private ECKey.ECDSASignature ecdsaSignature;
        private BigInteger v;
        private byte[] r;
        private byte[] s;

        public SignatureParts(ECKey.ECDSASignature ecdsaSignature) {
            this.ecdsaSignature = ecdsaSignature;
        }

        public BigInteger getV() {
            return v;
        }

        public byte[] getR() {
            return r;
        }

        public byte[] getS() {
            return s;
        }

        public SignatureParts invoke() {
            v = BigInteger.valueOf((long) ecdsaSignature.v);
            r = bigIntegerToBytes(ecdsaSignature.r, 32);
            s = bigIntegerToBytes(ecdsaSignature.s, 32);
            return this;
        }
    }
}
