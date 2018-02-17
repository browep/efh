package org.efh.filetranfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class Receiver {


	public static Scanner scanner;


	public static void main (String [] args ) throws IOException {


		String fileLocation,ipAddress;
		int portNo;
		scanner=new Scanner(System.in);
		System.out.println("Enter ipAddress of machine(if you are testing this on same machine than enter 127.0.0.1) :");
		ipAddress=scanner.next();

		System.out.println("Enter port number of machine(e.g. '2000') :");
		portNo=scanner.nextInt();
		System.out.println("Please enter file name to download: ");		//you can modify this program to receive file name from server and then you can skip this step
		fileLocation=scanner.next();
		Receiver.receiveFile(ipAddress, portNo, fileLocation);


	}
	public static void receiveFile(String ipAddress,int portNo,String fileName) throws IOException
	{

		int bytesRead=0;
		int current = 0;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		Socket socket = null;
		try {

			//creating connection.
			socket = new Socket(ipAddress,portNo);
			System.out.println("connected.");
			
			// receive file
			byte [] byteArray  = new byte [6022386];					//I have hard coded size of byteArray, you can send file size from socket before creating this.
			System.out.println("Please wait downloading file");
			
			File outputFile = new File("destination/".concat(fileName));
			
			//reading file from socket
			InputStream inputStream = socket.getInputStream();
			fileOutputStream = new FileOutputStream(outputFile);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			bytesRead = inputStream.read(byteArray,0,byteArray.length);					//copying file from socket to byteArray

			current = bytesRead;
			do {
				bytesRead =inputStream.read(byteArray, current, (byteArray.length-current));
				if(bytesRead >= 0) current += bytesRead;
			} while(bytesRead > -1);
			bufferedOutputStream.write(byteArray, 0 , current);							//writing byteArray to file
			bufferedOutputStream.flush();												//flushing buffers
			
			System.out.println("File " + fileName  + " downloaded ( size: " + current + " bytes read)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			if (fileOutputStream != null) fileOutputStream.close();
			if (bufferedOutputStream != null) bufferedOutputStream.close();
			if (socket != null) socket.close();
		}
	}
}
