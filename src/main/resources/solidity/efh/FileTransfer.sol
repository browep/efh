pragma solidity ^0.4.8;

// file transfer contract

contract filetransfer {

    address client;
    address server;
    uint256 fileHash;
    uint128 expirationBlock;
    int currentPercent;

    function filetransfer(address _client, address _server, uint256 _fileHash, uint128 _expirationBlock)
        public
        payable
    {
        client = _client;
        server = _server;
        fileHash = _fileHash;
        expirationBlock = _expirationBlock;

    }

    function isRedeemable(bytes32 h, uint8 v, bytes32 r, bytes32 s, uint value) constant returns(bool) {

        // get the address used to sign the hash
        address recoveredAddr = ecrecover(h, v, r, s);

        // hash the value to see if it matches what the passed in hash is
        bytes32 proof = sha3(this, value);

        return recoveredAddr == client;
    }

    function redeem(bytes32 h, uint8 v, bytes32 r, bytes32 s, uint value) public returns (bool) {
        if (isRedeemable(h, v, r, s, value)) {
            if (value >= this.balance) {
                selfdestruct(server);
            } else {
                if(!server.send(value)) throw;
                selfdestruct(client);
            }
        } else {
            throw;
        }
    }

    function clawback() public {
        if (msg.sender != client) throw;
        if (block.number < expirationBlock) throw;

        selfdestruct(client);
    }

    function getClient() constant returns (address) {
        return client;
    }

    function getServer() constant returns (address) {
        return server;
    }

    function getFileHash() constant returns (uint256) {
        return fileHash;
    }

    function getExpirationBlock() constant returns (uint256) {
        return expirationBlock;
    }
}