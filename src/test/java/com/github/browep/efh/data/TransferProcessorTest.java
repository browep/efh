package com.github.browep.efh.data;

import com.github.browep.efh.Constants;
import com.github.browep.efh.FileHubAdapter;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class TransferProcessorTest {

    Logger logger = LoggerFactory.getLogger(TransferProcessorTest.class);

    @Test
    public void serializeDeserialize_1() throws Exception {

        BigInteger valueInWei = BigInteger.valueOf(100);

        FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
        fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

        FileHubAdapter.HashAndSig hashAndSig = fileHubAdapter.sign(valueInWei, ECKey.fromPrivate(Numeric.hexStringToByteArray(Constants.CLIENT_PRIV_KEY)));
        String signedAmount = TransferProcessor.serialize(hashAndSig, valueInWei);

        logger.info("signedAmount: " + signedAmount);

        HashSigValue hashSigValue = TransferProcessor.deserialize(signedAmount);

        Assert.assertEquals(hashAndSig.ecdsaSignature.r, hashSigValue.ecdsaSignature.r);
        Assert.assertEquals(hashAndSig.ecdsaSignature.s, hashSigValue.ecdsaSignature.s);
        Assert.assertEquals(hashAndSig.ecdsaSignature.v % 27, hashSigValue.ecdsaSignature.v % 27);
        Assert.assertEquals(Numeric.toHexString(hashAndSig.hash), Numeric.toHexString(hashSigValue.hash));
        Assert.assertEquals(valueInWei, hashSigValue.valueInWei);

    }
}
