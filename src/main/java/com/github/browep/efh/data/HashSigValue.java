package com.github.browep.efh.data;

import org.ethereum.crypto.ECKey;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * encapsulate hash sig and value
 */
public class HashSigValue {
    public final byte[] hash;
    public final ECKey.ECDSASignature ecdsaSignature;
    public final BigInteger valueInWei;

    public HashSigValue(byte[] hash, ECKey.ECDSASignature ecdsaSignature, BigInteger valueInWei) {
        this.hash = hash;
        this.ecdsaSignature = ecdsaSignature;
        this.valueInWei = valueInWei;
    }

    @Override
    public String toString() {
        return "HashSigValue{" +
                "hash=" + Numeric.toHexString(hash) +
                ", ecdsaSignature=" + ecdsaSignature.toHex() +
                ", valueInWei=" + valueInWei +
                '}';
    }
}
