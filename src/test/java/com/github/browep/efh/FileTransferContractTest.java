package com.github.browep.efh;

import org.apache.commons.logging.LogFactory;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class FileTransferContractTest {

    Logger logger = LoggerFactory.getLogger(FileTransferContractTest.class);

    @Test
    public void canConnect() throws IOException {
        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
        Assert.assertNotNull(fileHubAdapter);
    }

    @Test
    public void canDeployContract() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
        String fileContractAddress = fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

        Assert.assertNotNull(fileContractAddress);
        Assert.assertTrue(!fileContractAddress.isEmpty());

        Assert.assertEquals(Constants.INITIAL_WEI_VALUE, fileHubAdapter.getContractBalance());
    }

//    @Test
//    public void canSendRedeem() throws Exception {
//        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
//        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);
//        String txHash = fileHubAdapter.redeem(10);
//        Assert.assertNotNull(txHash);
//
//    }
//
//    @Test
//    public void redeemSendsFunds() throws Exception {
//        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
//        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);
//
//        BigInteger serverBalanceBefore = fileHubAdapter.getServerBalance();
//
//        fileHubAdapter.redeem(10);
//
//        BigInteger contractEndBalance = fileHubAdapter.getContractBalance();
//        Assert.assertEquals(BigInteger.ZERO, contractEndBalance);
//
//        BigInteger serverBalanceAfter = fileHubAdapter.getServerBalance();
//        Assert.assertTrue("\nbefore: " + serverBalanceBefore +
//                                 "\nafter:  " + serverBalanceAfter,
//                serverBalanceAfter.compareTo(serverBalanceBefore) > 0);
//    }

    @Test
    public void testIsRedeemable() throws Exception {
        BigInteger valueInWei = BigInteger.valueOf(100);

        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

        FileHubAdapter.HashAndSig hashAndSig = fileHubAdapter.sign(valueInWei, ECKey.fromPrivate(Numeric.hexStringToByteArray(Constants.CLIENT_PRIV_KEY)));

        boolean successBool = fileHubAdapter.isRedeemable(hashAndSig, valueInWei);

        Assert.assertTrue(successBool);
    }

    @Test
    public void testIsNotRedeemable() throws Exception {
        BigInteger amountInWei = BigInteger.valueOf(100);

        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);
        FileHubAdapter.HashAndSig hashAndSig = fileHubAdapter.sign(amountInWei, ECKey.fromPrivate(Numeric.hexStringToByteArray(Constants.SERVER_PRIV_KEY)));
        boolean successBool = fileHubAdapter.isRedeemable(hashAndSig, amountInWei);

        Assert.assertFalse(successBool);
    }
}
