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

    function isRedeemable( bytes32 hash, uint8 v, bytes32 r, bytes32 s) constant returns(bool) {
        address recoveredAddr = ecrecover(hash, v, r, s);
        return recoveredAddr == client;
    }

    function redeem(uint256 percent) public returns (bool) {
        if (msg.sender != client) throw;
        if (percent > 100) throw;

        if (percent == 100 ) {
            selfdestruct(server);
            return true;
        } else {
            uint256 sendAmount = (this.balance / 100) * percent;

            if (!server.send(sendAmount)) throw;

            selfdestruct(client);

            return true;
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