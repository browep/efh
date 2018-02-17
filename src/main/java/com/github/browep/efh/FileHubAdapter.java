package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.sample.Application;

import java.io.IOException;

public class FileHubAdapter {

    private static final Logger log = LoggerFactory.getLogger(FileHubAdapter.class);

    public FileHubAdapter() throws IOException {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        log.info("Connected to Ethereum client version: "
                + web3j.web3ClientVersion().send().getWeb3ClientVersion());

    }
}
