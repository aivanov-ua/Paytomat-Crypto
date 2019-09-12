package com.paytomat.btc;

import com.paytomat.btc.network.NetworkParams;
import com.paytomat.btc.network.NetworkParamsFactory;
import com.paytomat.core.util.Base58;
import com.paytomat.core.util.ByteSerializer;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static com.paytomat.btc.BitcoinException.CODE_BAD_FORMAT;
import static com.paytomat.btc.BitcoinException.CODE_UNSUPPORTED;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class Address {

    public static final int NUM_ADDRESS_HASH = 20;

    public static Address fromString(String addressStr, String tokenSymbol, boolean isTestnet) {
        return fromString(addressStr, NetworkParamsFactory.getParams(tokenSymbol, isTestnet));
    }

    public static Address fromString(String addressStr, NetworkParams networkParams) {
        if (addressStr == null || addressStr.length() == 0 || networkParams == null) {
            throw new BitcoinException(CODE_BAD_FORMAT, "No data provided");
        }

        byte[] bytes = Base58.decodeChecked(addressStr);
        if (bytes == null || bytes.length <= NUM_ADDRESS_HASH) {
            throw new BitcoinException(CODE_UNSUPPORTED, "Unsupported address " + addressStr);
        }

        byte[] hash = new byte[20];
        System.arraycopy(bytes, bytes.length - NUM_ADDRESS_HASH, hash, 0, NUM_ADDRESS_HASH);
        int headerSize = bytes.length - NUM_ADDRESS_HASH;
        if (headerSize > 4) {
            throw new BitcoinException(CODE_UNSUPPORTED, "Unsupported address " + addressStr);
        }
        byte[] header = new byte[4];
        System.arraycopy(bytes, 0, header, 4 - headerSize, headerSize);
        int networkHeader = ByteBuffer.wrap(header).getInt();

        if (networkHeader == networkParams.getStandardAddressHeader()) {
            return new Address(hash, networkParams, false);
        } else if (networkHeader == networkParams.getMultisigAddressHeader()) {
            return new Address(hash, networkParams, true);
        } else {
            throw new BitcoinException(CODE_UNSUPPORTED, "Unsupported address " + addressStr);
        }
    }

    private final NetworkParams networkParams;
    private final byte[] hash;
    private final boolean p2sh;

    public Address(byte[] hash, NetworkParams networkParams, boolean isP2SH) {
        if (hash == null || networkParams == null)
            throw new BitcoinException(CODE_BAD_FORMAT, "No data provided");
        if (hash.length != NUM_ADDRESS_HASH)
            throw new BitcoinException(CODE_BAD_FORMAT, "Cannot create address from bytes");

        this.networkParams = networkParams;
        this.p2sh = isP2SH;
        this.hash = hash;
    }

    public boolean isP2PKH() {
        return !p2sh;
    }

    private ByteSerializer getAddressBytesWrapped() {
        ByteSerializer serializer = ByteSerializer.create();
        int header;
        if (p2sh) header = networkParams.getMultisigAddressHeader();
        else header = networkParams.getStandardAddressHeader();
        serializer.write(BigInteger.valueOf(header).toByteArray());
        return serializer.write(hash);
    }

    public byte[] getHash160() {
        return hash;
    }

    public String getBytesHex() {
        return getAddressBytesWrapped().toString();
    }

    @Override
    public String toString() {
        return getAddressBytesWrapped().toBase58WithChecksum();
    }

}
