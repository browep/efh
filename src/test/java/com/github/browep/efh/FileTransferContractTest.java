package com.github.browep.efh;

import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class FileTransferContractTest {

    public static final String CLIENT_ADDR = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
    public static final String SERVER_ADDR = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
    public static final String FILE_HASH_STR = "84dad89ab80d0843733d41c124c2745d2a4c7577977cce16d7cf1b124aaa09b0";
    public static final BigInteger INITIAL_WEI_VALUE = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();

    @Test
    public void canConnect() throws IOException {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        Assert.assertNotNull(fileHubAdapter);
    }

    @Test
    public void canDeployContract() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        String fileContractAddress = fileHubAdapter.deploy(CLIENT_ADDR, SERVER_ADDR, FILE_HASH_STR, INITIAL_WEI_VALUE);

        Assert.assertNotNull(fileContractAddress);
        Assert.assertTrue(!fileContractAddress.isEmpty());

        Assert.assertEquals(INITIAL_WEI_VALUE, fileHubAdapter.getContractBalance());
    }

    @Test
    public void canSendRedeem() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        fileHubAdapter.deploy(CLIENT_ADDR, SERVER_ADDR, FILE_HASH_STR, INITIAL_WEI_VALUE);
        String txHash = fileHubAdapter.redeem(10);
        Assert.assertNotNull(txHash);

    }

    @Test
    public void redeemSendsFunds() throws Exception {
        FileHubAdapter fileHubAdapter = new FileHubAdapter();
        fileHubAdapter.deploy(CLIENT_ADDR, SERVER_ADDR, FILE_HASH_STR, INITIAL_WEI_VALUE);

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
