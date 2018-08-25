package com.github.browep.efh.data;

import com.github.browep.efh.FileHubAdapter;
import com.github.browep.efh.data.HashSigValue;
import javassist.bytecode.ByteArray;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.Base64;

public class TransferProcessor {

    private static Logger logger = LoggerFactory.getLogger(TransferProcessor.class);

    public enum VerificationResult {
        OK, TOO_LITTLE_WEI, NOT_REDEEMABLE
    }

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
            logger.error("contract balance too low.  contract balance: " + fileHubAdapter.getContractBalance() + " expectedWei: " + expectedWei);
            return false;
        } else if (fileHubAdapter.getFileHash().compareTo(expectedFileHash) != 0) {
            logger.error("wrong file hash. actual: " + fileHubAdapter.getFileHash() + ", expected: " + expectedFileHash);
            return false;
        } else if (!expectedServerAddress.equals(contractServerAddress)) {
            logger.error("wrong server addr.  actual: " + contractServerAddress + ", expected: " + expectedServerAddress);
            return false;
        } else if (minExpiration.compareTo(fileHubAdapter.getExpirationBlock()) > 0) {
            logger.error("expiration not far enough in the future. min: " + minExpiration + ", contract: " + fileHubAdapter.getExpirationBlock());
            return false;
        } else {
            return true;
        }
    }

    public static VerificationResult verifyTransaction(
            String transactionDataStr,
            FileHubAdapter fileHubAdapter,
            long totalSent, long fileSize,
            BigInteger fileCostInWei
    ) throws Exception {

        HashSigValue hashSigValue = deserialize(transactionDataStr);

        BigInteger weiValueOfBytesSent = getWeiValueOfBytesSent(fileSize, totalSent, fileCostInWei, null);
        if (hashSigValue.valueInWei.compareTo(weiValueOfBytesSent) < 0) {
            logger.error("sent wei: " + hashSigValue.valueInWei);
            logger.error("expected: " + weiValueOfBytesSent);
            return VerificationResult.TOO_LITTLE_WEI;
        }

        return fileHubAdapter.isRedeemable(new FileHubAdapter.HashAndSig(hashSigValue.ecdsaSignature, hashSigValue.hash), hashSigValue.valueInWei) ?
                VerificationResult.OK : VerificationResult.NOT_REDEEMABLE;

    }

    public static boolean isRedeemable(FileHubAdapter fileHubAdapter, String transactionDataStr) throws Exception {
        HashSigValue hashSigValue = deserialize(transactionDataStr);
        return fileHubAdapter.isRedeemable(new FileHubAdapter.HashAndSig(hashSigValue.ecdsaSignature, hashSigValue.hash), hashSigValue.valueInWei);
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
        byte v = byteBuffer.get();
        if (v < 27) v += 27;
        ECKey.ECDSASignature ecdsaSignature = ECKey.ECDSASignature.fromComponents(r, s, v);

        byte[] valueInWeiBytes = new byte[32];
        byteBuffer.get(valueInWeiBytes, 0, 32);
        BigInteger valueInWei = new BigInteger(valueInWeiBytes);

        return new HashSigValue(hashBytes, ecdsaSignature, valueInWei);
    }

    public static BigInteger getWeiValueOfBytesSent(long fileSize, long totalReceivedBytes, BigInteger fileCostInWei, @Nullable Logger logger) {
        BigInteger weiToSend;
        BigDecimal fileSizeBigDecimal = BigDecimal.valueOf(fileSize);
        BigDecimal totalBytesReceivedBigDecimal = BigDecimal.valueOf(totalReceivedBytes);
        BigDecimal fileCostInWeiBigDecimal = new BigDecimal(fileCostInWei);
        BigDecimal percentReceived = totalBytesReceivedBigDecimal
                .divide(fileSizeBigDecimal, 3, RoundingMode.HALF_EVEN);
        weiToSend = percentReceived
                .multiply(fileCostInWeiBigDecimal)
                .toBigInteger();
        if (logger != null) {
            logger.info("percent received:" + percentReceived);
        }
        return weiToSend;
    }

}
