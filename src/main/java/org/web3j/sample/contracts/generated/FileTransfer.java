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
    private static final String BINARY = "60606040526040516080806103c483398101604090815281516020830151918301516060909301519092905b60008054600160a060020a03808716600160a060020a03199283161790925560018054928616929091169190911790556002829055600380546001608060020a0383166001608060020a03199091161790555b505050505b610332806100926000396000f300606060405236156100675763ffffffff60e060020a600035041663089587cd811461006c5780632526d9601461009557806359dc735c146100a457806360aa825a146100cd5780638493f71f146100ec578063db006a751461010b578063ef1509f01461012f575b610000565b346100005761007961015f565b60408051600160a060020a039092168252519081900360200190f35b34610000576100a261016f565b005b34610000576100796101bb565b60408051600160a060020a039092168252519081900360200190f35b34610000576100da6101cb565b60408051918252519081900360200190f35b34610000576100da6101e4565b60408051918252519081900360200190f35b346100005761011b6004356101eb565b604080519115158252519081900360200190f35b346100005761011b60043560ff60243516604435606435610283565b604080519115158252519081900360200190f35b600154600160a060020a03165b90565b60005433600160a060020a0390811691161461018a57610000565b6003546fffffffffffffffffffffffffffffffff164310156101ab57610000565b600054600160a060020a0316ff5b565b600054600160a060020a03165b90565b6003546fffffffffffffffffffffffffffffffff165b90565b6002545b90565b60008054819033600160a060020a0390811691161461020957610000565b606483111561021757610000565b826064141561022e57600154600160a060020a0316ff5b506001546040516064600160a060020a033081163191909104850292169082156108fc029083906000818181858888f1935050505015156101ab57610000565b600054600160a060020a0316ff5b5b50919050565b60408051600081815260208083018452918301819052825187815260ff87168184015280840186905260608101859052925190928392600192608080840193601f19830192908190039091019086866161da5a03f115610000575050604051601f190151600054600160a060020a03808316911614925090505b509493505050505600a165627a7a723058208e1466eae32702f4e59494ba496bbeb2b339551f10ccfdc232117f0bd875c1a10029";

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

    public RemoteCall<TransactionReceipt> redeem(BigInteger percent) {
        Function function = new Function(
                "redeem", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(percent)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> isRedeemable(byte[] hash, BigInteger v, byte[] r, byte[] s) {
        Function function = new Function("isRedeemable", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
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
