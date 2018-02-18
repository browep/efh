package org.efh.filetranfer;

public class FileTransferProcessor {

	public boolean continueTransfer(String input) {
		System.out.println("Verifying transaction");
		return verifyTransaction(input);
	}

	private boolean verifyTransaction(String input) {
		return true;
	}

}
