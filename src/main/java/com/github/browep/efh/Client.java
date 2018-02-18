package com.github.browep.efh;

import java.io.*;
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

            File outputFile = File.createTempFile("transfer", ".mp4", new File("/tmp"));
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] bytes = new byte[1024];

            int val = 0;
            long totalBytes = 0;

            System.out.println("Receiving the file");
            while ((val = in.read(bytes, 0, bytes.length)) > 0) {
                fos.write(bytes, 0, bytes.length);
                fos.flush();
                totalBytes += val;
                System.out.println("received: " + totalBytes);

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