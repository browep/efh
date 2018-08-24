package com.github.browep.efh;

import org.web3j.utils.Convert;

import java.math.BigInteger;

public class Constants {
    public static final String CLIENT_ADDR = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57".toLowerCase();
    public static final String SERVER_ADDR = "0xf17f52151EbEF6C7334FAD080c5704D77216b732".toLowerCase();
    public static final String FILE_HASH_STR = "84dad89ab80d0843733d41c124c2745d2a4c7577977cce16d7cf1b124aaa09b0";
    public static final BigInteger FILE_HASH_NUM = new BigInteger(FILE_HASH_STR, 16);
    public static final BigInteger INITIAL_WEI_VALUE = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();
    public static final String CLIENT_PRIV_KEY = "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
    public static final String SERVER_PRIV_KEY = "ae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f";


    public static final int CHUNK_SIZE = 1024 * 1024 * 10;
}
