package com.github.browep.efh;

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

public class Server {

    public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		System.out.println("Server listening: " + portNumber);

		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket = serverSocket.accept();
				OutputStream clientOutputStream = clientSocket.getOutputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

			System.out.println("connected with: " + clientSocket.toString());

			String contractAddress = bufferedReader.readLine();

			System.out.println("Contract address: " + contractAddress);

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

				fileHubAdapter.sendRedeemTx(redeemTx);
			} else {
				clientSocket.close();
			}


		} catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String sendFile(OutputStream clientOutputStream, BufferedReader bufferedReader) throws IOException {
		String fileName = "/tmp/movie.mp4";

		File file = new File(fileName);
		InputStream inputStream = new FileInputStream(file);

		System.out.println("Sending file: "+ file.getAbsolutePath());
		byte[] bytes = new byte[Constants.CHUNK_SIZE];

		int val = 0;
		boolean txVerified = true;
        String redeemTransactionData = null;
        long totalSent = 0;

        while ((val = inputStream.read(bytes, 0, bytes.length)) > 0 && txVerified) {
            clientOutputStream.write(bytes, 0, val);
            clientOutputStream.flush();
            redeemTransactionData = bufferedReader.readLine();
            txVerified = Verifier.verifyTransaction(redeemTransactionData);
            totalSent += val;
        }

        if (txVerified) {
            System.out.println("Finished sending file. sent: " + totalSent );
        } else {
		    System.err.println("tx not verified: " + redeemTransactionData);
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