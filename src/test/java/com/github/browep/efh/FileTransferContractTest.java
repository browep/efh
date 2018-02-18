package com.github.browep.efh;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class FileTransferContractTest {

    @Test
    public void canConnect() throws IOException {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        Assert.assertNotNull(fileHubAdapter);
    }

    @Test
    public void canDeployContract() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        String fileContractAddress = fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

        Assert.assertNotNull(fileContractAddress);
        Assert.assertTrue(!fileContractAddress.isEmpty());

        Assert.assertEquals(Constants.INITIAL_WEI_VALUE, fileHubAdapter.getContractBalance());
    }

    @Test
    public void canSendRedeem() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);
        String txHash = fileHubAdapter.redeem(10);
        Assert.assertNotNull(txHash);

    }

    @Test
    public void redeemSendsFunds() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

        BigInteger serverBalanceBefore = fileHubAdapter.getServerBalance();

        fileHubAdapter.redeem(10);

        BigInteger contractEndBalance = fileHubAdapter.getContractBalance();
        Assert.assertEquals(BigInteger.ZERO, contractEndBalance);

        BigInteger serverBalanceAfter = fileHubAdapter.getServerBalance();
        Assert.assertTrue("\nbefore: " + serverBalanceBefore +
                                 "\nafter:  " + serverBalanceAfter,
                serverBalanceAfter.compareTo(serverBalanceBefore) > 0);
    }
}
