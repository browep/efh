package org.efh.filetranfer;

public class FileTransferProcessor {

	public boolean continueTransfer(String input) {
		return verifyTransaction(input);
	}

	private boolean verifyTransaction(String input) {
		return "No".equalsIgnoreCase(input) ? false : true;
	}

}
