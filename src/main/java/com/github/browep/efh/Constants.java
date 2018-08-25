package com.github.browep.efh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Properties;

public class Constants {
    private static Logger logger = LoggerFactory.getLogger(Constants.class);

    static {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            input = classloader.getResourceAsStream("config.properties");

            prop.load(input);

            logger.info("test.eth_node_url=   " + prop.getProperty("test.eth_node_url"));
            logger.info("test.client_priv_key=" + prop.get("test.client_priv_key"));
            logger.info("test.client_addr=    " + prop.get("test.client_addr"));
            logger.info("test.server_priv_key=" + prop.get("test.server_priv_key"));
            logger.info("test.server_addr=    " + prop.get("test.server_addr"));

            ETH_NODE_URL = prop.getProperty("test.eth_node_url");
            CLIENT_PRIV_KEY = (String) prop.get("test.client_priv_key");
            CLIENT_ADDR = (String) prop.get("test.client_addr");
            SERVER_PRIV_KEY = (String) prop.get("test.server_priv_key");
            SERVER_ADDR = (String) prop.get("test.server_addr");

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }

    }

    public static String ETH_NODE_URL;
    public static String CLIENT_ADDR;
    public static String SERVER_ADDR;
    public static String CLIENT_PRIV_KEY;
    public static String SERVER_PRIV_KEY;
    public static final String FILE_HASH_STR = "84dad89ab80d0843733d41c124c2745d2a4c7577977cce16d7cf1b124aaa09b0";
    public static final BigInteger FILE_HASH_NUM = new BigInteger(FILE_HASH_STR, 16);
    public static final BigInteger INITIAL_WEI_VALUE = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();

    public static final int CHUNK_SIZE = 1024 * 1024 * 10;
}
