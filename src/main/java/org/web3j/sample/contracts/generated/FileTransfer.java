package org.web3j.sample.contracts.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
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
    private static final String BINARY = "60606040526040516080806104f283398101604090815281516020830151918301516060909301519092905b60008054600160a060020a03808716600160a060020a03199283161790925560018054928616929091169190911790556002829055600380546001608060020a0383166001608060020a03199091161790555b505050505b610460806100926000396000f300606060405236156100885763ffffffff60e060020a600035041663089587cd811461008d57806320965255146100b65780632526d960146100d55780632ba04b7d146100e457806359dc735c146101175780635b332af6146101405780635c5d625e1461016957806360aa825a146101885780638493f71f146101a7578063f9ee8f41146101c6575b610000565b346100005761009a6101f9565b60408051600160a060020a039092168252519081900360200190f35b34610000576100c3610209565b60408051918252519081900360200190f35b34610000576100e2610210565b005b346100005761010360043560ff6024351660443560643560843561025c565b604080519115158252519081900360200190f35b346100005761009a6102e5565b60408051600160a060020a039092168252519081900360200190f35b346100005761009a6102f5565b60408051600160a060020a039092168252519081900360200190f35b34610000576100c3610305565b60408051918252519081900360200190f35b34610000576100c361030c565b60408051918252519081900360200190f35b34610000576100c3610325565b60408051918252519081900360200190f35b346100005761010360043560ff6024351660443560643560843561032c565b604080519115158252519081900360200190f35b600154600160a060020a03165b90565b6006545b90565b60005433600160a060020a0390811691161461022b57610000565b6003546fffffffffffffffffffffffffffffffff1643101561024c57610000565b600054600160a060020a0316ff5b565b600061026b868686868661032c565b1561008857600160a060020a03301631821061028f57600154600160a060020a0316ff5b600154604051600160a060020a039091169083156108fc029084906000818181858888f19350505050151561024c57610000565b600054600160a060020a0316ff5b6102db565b610000565b5b95945050505050565b600054600160a060020a03165b90565b600754600160a060020a03165b90565b6005545b90565b6003546fffffffffffffffffffffffffffffffff165b90565b6002545b90565b604080516020818101835260009182905282518084018452601c81527f19457468657265756d205369676e6564204d6573736167653a0a33320000000081830152835180845280830185528401839052835189815260ff891681840152808501889052606081018790529351929390926001926080808401939192601f198101928190039091019087866161da5a03f11561000057505060408051601f1981015160078054600160a060020a0392831673ffffffffffffffffffffffffffffffffffffffff19909116179081905586835292519182900360200190912060055560068590556000549181169116148015610427575060055487145b91505b50959450505050505600a165627a7a723058207bc6f9aa1eb3a7178d55890e42fe3a614eca796d510b1344e3bf22d8c40da27e0029";

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

    public RemoteCall<BigInteger> getValue() {
        Function function = new Function("getValue", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteCall<String> getRecoveredAddr() {
        Function function = new Function("getRecoveredAddr", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<byte[]> getProof() {
        Function function = new Function("getProof", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
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

    public RemoteCall<TransactionReceipt> isRedeemable(byte[] h, BigInteger v, byte[] r, byte[] s, BigInteger _value) {
        Function function = new Function(
                "isRedeemable", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(h), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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
