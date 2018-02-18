package com.github.browep.efh;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);


        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                InputStream in = socket.getInputStream();
        ) {

            FileHubAdapter fileHubAdapter = new FileHubAdapter(Constants.CLIENT_PRIV_KEY);
            fileHubAdapter.deploy(Constants.CLIENT_ADDR, Constants.SERVER_ADDR, Constants.FILE_HASH_STR, Constants.INITIAL_WEI_VALUE);

            out.println(fileHubAdapter.getContractAddress());

            File outputFile = File.createTempFile("transfer", ".mp4", new File("/tmp/client_dl"));
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] bytes = new byte[Constants.CHUNK_SIZE];

            int val = 0;
            long totalReceivedBytes = 0;

            int redeemPercent = 0;

            System.out.println("Receiving the file");
            while ((val = in.read(bytes, 0, bytes.length)) > 0) {
                fos.write(bytes, 0, val);
                fos.flush();
                totalReceivedBytes += val;
                System.out.println("received: " + totalReceivedBytes +"/" + Constants.FILE_SIZE + " creating transaction.");
                redeemPercent = BigDecimal.valueOf(totalReceivedBytes)
                        .divide(BigDecimal.valueOf(Constants.FILE_SIZE), 3, RoundingMode.HALF_EVEN)
                        .multiply(BigDecimal.valueOf(100))
                        .intValue();
                String redeemTx = fileHubAdapter.createRedeemTx(redeemPercent );
                System.out.println("sending: " + redeemTx);
                System.out.println("percent: " + redeemPercent);
                out.println(redeemTx);

            }

            System.out.println("Received file: " + outputFile.getCanonicalPath());

            fos.flush();
            fos.close();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}