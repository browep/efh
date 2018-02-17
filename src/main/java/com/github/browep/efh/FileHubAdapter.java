package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
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

    public FileHubAdapter() throws IOException {
        web3j = Web3j.build(new HttpService("http://localhost:7545"));
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());

        credentials = Credentials.create("c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3");

    }

    /**
     * deploy
     * @return contract address
     * @throws Exception
     */
    public String deploy() throws Exception {
        BigInteger fileHash = new BigInteger("84dad89ab80d0843733d41c124c2745d2a4c7577977cce16d7cf1b124aaa09b0", 16);
        fileTransfer = FileTransfer.deploy(web3j, credentials, ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT,
                BigInteger.valueOf(10000),
                "0x627306090abaB3A6e1400e9345bC60c78a8BEf57",
                "0xf17f52151EbEF6C7334FAD080c5704D77216b732",
                fileHash, BigInteger.valueOf(Long.MAX_VALUE)
                ).send();

        return fileTransfer.getContractAddress();
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
