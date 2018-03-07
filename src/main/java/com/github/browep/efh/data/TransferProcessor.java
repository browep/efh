package com.github.browep.efh.data;

import com.github.browep.efh.FileHubAdapter;
import com.github.browep.efh.data.HashSigValue;
import javassist.bytecode.ByteArray;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Base64;

public class TransferProcessor {

    public static boolean verifyContract(
            FileHubAdapter fileHubAdapter,
            BigInteger expectedWei,
            BigInteger expectedFileHash,
            String expectedServerAddress,
            BigInteger numTimeoutblocks
    ) throws Exception {
        BigInteger minExpiration = fileHubAdapter.getCurrentBlock().add(numTimeoutblocks);
        String contractServerAddress = fileHubAdapter.getServerAddr();
        if (fileHubAdapter.getContractBalance().compareTo(expectedWei) < 0) {
            System.err.println("contract balance too low.  contract balance: " + fileHubAdapter.getContractBalance() + " expectedWei: " + expectedWei);
            return false;
        } else if (fileHubAdapter.getFileHash().compareTo(expectedFileHash) != 0) {
            System.err.println("wrong file hash. actual: " + fileHubAdapter.getFileHash() + ", expected: " + expectedFileHash);
            return false;
        } else if (!expectedServerAddress.equals(contractServerAddress)) {
            System.err.println("wrong server addr.  actual: " + contractServerAddress + ", expected: " + expectedServerAddress);
            return false;
        } else if (minExpiration.compareTo(fileHubAdapter.getExpirationBlock()) > 0){
            System.err.println("expiration not far enough in the future. min: " + minExpiration + ", contract: " + fileHubAdapter.getExpirationBlock());
            return false;
        } else{
            return true;
        }
    }

    public static boolean verifyTransaction(String transactionDataStr) {

        return true;
    }

    public static String serialize(FileHubAdapter.HashAndSig hashAndSig, BigInteger valueInWei) {
        byte[] sigBytes = hashAndSig.ecdsaSignature.toByteArray();
        byte[] bytes = ByteUtil.merge(
                hashAndSig.hash,
                sigBytes,
                ByteUtil.copyToArray(valueInWei)
        );
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static HashSigValue deserialize(String base64Str) {
        byte[] bytes = Base64.getDecoder().decode(base64Str);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byte[] hashBytes = new byte[32];
        byteBuffer.get(hashBytes, 0, 32);

        byte[] r = new byte[32];
        byteBuffer.get(r, 0, 32);
        byte[] s = new byte[32];
        byteBuffer.get(s, 0, 32);
        ECKey.ECDSASignature ecdsaSignature = ECKey.ECDSASignature.fromComponents(r, s, byteBuffer.get());

        byte[] valueInWeiBytes = new byte[32];
        byteBuffer.get(valueInWeiBytes, 0, 32);
        BigInteger valueInWei = new BigInteger(valueInWeiBytes);

        return new HashSigValue(hashBytes, ecdsaSignature, valueInWei);
    }

}
