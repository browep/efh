package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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

public class Server {
	
	private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

		if (args.length != 1) {
			logger.error("Usage: java Server <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		logger.info("Server listening: " + portNumber);

		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket = serverSocket.accept();
				OutputStream clientOutputStream = clientSocket.getOutputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

			logger.info("connected with: " + clientSocket.toString());

			String contractAddress = bufferedReader.readLine();

			logger.info("Contract address: " + contractAddress);

			FileHubAdapter fileHubAdapter = FileHubAdapter.load(contractAddress, Constants.SERVER_PRIV_KEY);

			boolean contractVerified = Verifier.verifyContract(fileHubAdapter,
					Constants.INITIAL_WEI_VALUE,
					Constants.FILE_HASH_NUM,
					Constants.SERVER_ADDR,
					BigInteger.valueOf(120) // 30 minutes
					);

			if (contractVerified) {
				String redeemTx = sendFile(clientOutputStream, bufferedReader);
				clientSocket.close();

				EthSendTransaction ethSendTransaction = fileHubAdapter.sendRedeemTx(redeemTx);
				logger.info("sent transaction: " + redeemTx);
				logger.info("sent transaction: " + ethSendTransaction);
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

	private static String sendFile(OutputStream clientOutputStream, BufferedReader bufferedReader) throws IOException {
		String fileName = "/Users/paulbrower/movie.mp4";

		File file = new File(fileName);
		InputStream inputStream = new FileInputStream(file);

		logger.info("Sending file: "+ file.getAbsolutePath());
		byte[] bytes = new byte[Constants.CHUNK_SIZE];

		int val = 0;
		boolean txVerified = true;
        String redeemTransactionData = null;
        long totalSent = 0;

		try {
			while ((val = inputStream.read(bytes, 0, bytes.length)) > 0 && txVerified) {
                clientOutputStream.write(bytes, 0, val);
                clientOutputStream.flush();
                redeemTransactionData = bufferedReader.readLine();
                txVerified = Verifier.verifyTransaction(redeemTransactionData);
                totalSent += val;
                Thread.sleep(100);
            }
		} catch (SocketException e) {
			logger.error("client bailed.");
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

		if (txVerified) {
            logger.info("Finished sending file. sent: " + totalSent );
        } else {
		    logger.error("tx not verified: " + redeemTransactionData);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientOutputStream.close();
        bufferedReader.close();

        return redeemTransactionData;

	}

}