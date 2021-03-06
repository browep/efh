package com.github.browep.efh;

import com.github.browep.efh.data.HashSigValue;
import com.github.browep.efh.data.TransferProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.BufferUnderflowException;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private static long sleepMillis;

    public static void main(String[] args) {

        if (args.length != 3) {
            logger.error("Usage: java Server <port number> <sleep millis> <file to serve>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        sleepMillis = Long.parseLong(args[1]);
        String fileName = args[2];

        logger.info("Server listening: " + portNumber);
        logger.info("Serving: " + fileName);

        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket = serverSocket.accept();
             OutputStream clientOutputStream = clientSocket.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            logger.info("connected with: " + clientSocket.toString());

            String contractAddress = bufferedReader.readLine();

            logger.info("Contract address: " + contractAddress);

            FileHubAdapter fileHubAdapter = FileHubAdapter.load(contractAddress, Constants.SERVER_PRIV_KEY);

            boolean contractVerified = TransferProcessor.verifyContract(fileHubAdapter,
                    Constants.INITIAL_WEI_VALUE,
                    Constants.FILE_HASH_NUM,
                    Constants.SERVER_ADDR,
                    BigInteger.valueOf(120) // 30 minutes
            );

            if (contractVerified) {
                String redeemTx = sendFile(clientOutputStream, bufferedReader, fileHubAdapter, new File(fileName));
                clientSocket.close();

                logger.info("sent transaction: " + redeemTx);
            } else {
                clientSocket.close();
            }


        } catch (IOException e) {
            logger.error(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            logger.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendFile(OutputStream clientOutputStream, BufferedReader bufferedReader, FileHubAdapter fileHubAdapter, File file) throws IOException {

        InputStream inputStream = new FileInputStream(file);

        logger.info("Sending file: " + file.getAbsolutePath());
        byte[] bytes = new byte[Constants.CHUNK_SIZE];

        int val = 0;
        TransferProcessor.VerificationResult txVerified = TransferProcessor.VerificationResult.OK;
        String redeemTransactionData = null;
        HashSigValue lastVerifiedHashSigValue = null;
        long totalSent = 0;

        try {
            while ((val = inputStream.read(bytes, 0, bytes.length)) > 0 && txVerified == TransferProcessor.VerificationResult.OK) {
                clientOutputStream.write(bytes, 0, val);
                clientOutputStream.flush();
                redeemTransactionData = bufferedReader.readLine();
                while (bufferedReader.ready()) {
                    redeemTransactionData = bufferedReader.readLine();
                }
                BigInteger suitableWei = Constants.INITIAL_WEI_VALUE;
                try {
                    txVerified = TransferProcessor.verifyTransaction(redeemTransactionData, fileHubAdapter, totalSent, file.length(), suitableWei);
                } catch (BufferUnderflowException e) {
                    logger.error(e.getMessage() + "  " + redeemTransactionData);
                }
                totalSent += val;

                if (txVerified == TransferProcessor.VerificationResult.OK) {
                    try {
                        lastVerifiedHashSigValue = TransferProcessor.deserialize(redeemTransactionData);
                        logger.info("verified: " + lastVerifiedHashSigValue.valueInWei);
                    } catch (BufferUnderflowException e) {
                        logger.error(e.getMessage() + "  " + redeemTransactionData);
                    }
                }

                Thread.sleep(sleepMillis);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // wait a little bit before closing the connection
        logger.info("wait for late transactions");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        // check for one more tx
        while (bufferedReader.ready()) {
            String lastRedeemTransactionData = bufferedReader.readLine();
            try {
                if (TransferProcessor.isRedeemable(fileHubAdapter, redeemTransactionData)) {
                    redeemTransactionData = lastRedeemTransactionData;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (txVerified == TransferProcessor.VerificationResult.OK) {
            logger.info("Finished sending file. sent: " + totalSent);
        } else {
            logger.error(txVerified + ": tx not verified: " + redeemTransactionData);
        }

        if (lastVerifiedHashSigValue != null) {
            logger.info("redeeming: " + lastVerifiedHashSigValue);
            HashSigValue hashSigValue = TransferProcessor.deserialize(redeemTransactionData);
            try {
                fileHubAdapter.redeem(hashSigValue.hash, hashSigValue.ecdsaSignature, hashSigValue.valueInWei);
                logger.info("redeemed: "+ hashSigValue.valueInWei);
            } catch (Exception e) {
                logger.error("trouble redeeming: " + redeemTransactionData, e);
            }
        } else {
            logger.error("nothing to redeem");
        }


        clientOutputStream.close();
        bufferedReader.close();

        return redeemTransactionData;

    }

}