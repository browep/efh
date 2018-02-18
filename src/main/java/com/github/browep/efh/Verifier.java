package com.github.browep.efh;

import java.io.IOException;
import java.math.BigInteger;

public class Verifier {

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

    public static boolean verifyTransaction(String transactionData) {
        return true;
    }

}
