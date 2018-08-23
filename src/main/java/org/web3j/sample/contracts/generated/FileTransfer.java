package org.web3j.sample.contracts.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class FileTransfer extends Contract {
    private static final String BINARY = "606060405260405160808061040383398101604090815281516020830151918301516060909301519092905b60008054600160a060020a03808716600160a060020a03199283161790925560018054928616929091169190911790556002829055600380546001608060020a0383166001608060020a03199091161790555b505050505b610371806100926000396000f300606060405236156100675763ffffffff60e060020a600035041663089587cd811461006c5780632526d960146100955780632ba04b7d146100a457806359dc735c146100d757806360aa825a146101005780638493f71f1461011f578063f9ee8f411461013e575b610000565b3461000057610079610171565b60408051600160a060020a039092168252519081900360200190f35b34610000576100a2610181565b005b34610000576100c360043560ff602435166044356064356084356101cd565b604080519115158252519081900360200190f35b3461000057610079610256565b60408051600160a060020a039092168252519081900360200190f35b346100005761010d610266565b60408051918252519081900360200190f35b346100005761010d61027f565b60408051918252519081900360200190f35b34610000576100c360043560ff60243516604435606435608435610286565b604080519115158252519081900360200190f35b600154600160a060020a03165b90565b60005433600160a060020a0390811691161461019c57610000565b6003546fffffffffffffffffffffffffffffffff164310156101bd57610000565b600054600160a060020a0316ff5b565b60006101dc8686868686610286565b1561006757600160a060020a03301631821061020057600154600160a060020a0316ff5b600154604051600160a060020a039091169083156108fc029084906000818181858888f1935050505015156101bd57610000565b600054600160a060020a0316ff5b61024c565b610000565b5b95945050505050565b600054600160a060020a03165b90565b6003546fffffffffffffffffffffffffffffffff165b90565b6002545b90565b60408051600081815260208083018452918301819052825188815260ff88168184015280840187905260608101869052925190928392839260019260808082019392601f198101928190039091019086866161da5a03f11561000057505060408051601f1981015186825291519081900360200190206000549193509150600160a060020a03808416911614801561031d57508088145b8015610337575060015433600160a060020a039081169116145b92505b5050959450505050505600a165627a7a723058205e2e45eb8a1a998ee38318ba4c9cc4d5d8fe280fffa408b40e5298fa835035750029";

    protected FileTransfer(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FileTransfer(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> getServer() {
        Function function = new Function("getServer", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> clawback() {
        Function function = new Function(
                "clawback", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> redeem(byte[] h, BigInteger v, byte[] r, byte[] s, BigInteger value) {
        Function function = new Function(
                "redeem", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(h), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getClient() {
        Function function = new Function("getClient", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> getExpirationBlock() {
        Function function = new Function("getExpirationBlock", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getFileHash() {
        Function function = new Function("getFileHash", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> isRedeemable(byte[] h, BigInteger v, byte[] r, byte[] s, BigInteger _value) {
        Function function = new Function("isRedeemable", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(h), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public static RemoteCall<FileTransfer> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _client, String _server, BigInteger _fileHash, BigInteger _expirationBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_client), 
                new org.web3j.abi.datatypes.Address(_server), 
                new org.web3j.abi.datatypes.generated.Uint256(_fileHash), 
                new org.web3j.abi.datatypes.generated.Uint128(_expirationBlock)));
        return deployRemoteCall(FileTransfer.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static RemoteCall<FileTransfer> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, String _client, String _server, BigInteger _fileHash, BigInteger _expirationBlock) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_client), 
                new org.web3j.abi.datatypes.Address(_server), 
                new org.web3j.abi.datatypes.generated.Uint256(_fileHash), 
                new org.web3j.abi.datatypes.generated.Uint128(_expirationBlock)));
        return deployRemoteCall(FileTransfer.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static FileTransfer load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransfer(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static FileTransfer load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FileTransfer(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
