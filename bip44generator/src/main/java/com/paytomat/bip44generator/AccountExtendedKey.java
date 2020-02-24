package com.paytomat.bip44generator;

import com.paytomat.bip44generator.extendedkey.ExtendedKeyInfo;
import com.paytomat.bip44generator.extendedkey.ExtendedKeyInfoFactory;
import com.paytomat.core.util.ByteSerializer;

/**
 * created by Alex Ivanov on 2020-02-24.
 */
public class AccountExtendedKey {

    private final HDNode node;
    private final boolean isP2PKH;

    public AccountExtendedKey(HDNode hdNode) {
        this(hdNode, true);
    }

    public AccountExtendedKey(HDNode hdNode, boolean isP2PKH) {
        this.node = hdNode;
        this.isP2PKH = isP2PKH;
    }

    public AccountExtendedKey(MasterSeed masterSeed, int coinIndex, boolean isP2PKH) {
        HDPath path = HDPath.ROOT
                .getChild(isP2PKH ? 44 : 49, true)
                .getChild(coinIndex, true)
                .getChild(0, true);

        this.node = HDNode.fromSeed(masterSeed.getBytes()).createChildNode(path);
        this.isP2PKH = isP2PKH;
    }

    private ExtendedKeyInfo getExtendedInfo() {
        return ExtendedKeyInfoFactory.getInfo(isP2PKH);
    }

    private byte[] serialize(boolean pub) {
        return ByteSerializer.create(74)
                .write(node.getDepth())
                .write(node.getParentFingerprint())
                .write(node.getIndex())
                .write(node.getChainCode())
                .write(pub ? node.getPublicKey() : node.getPrivateKey33())
                .serialize();
    }

    public String getAccountPrivate() {
        return ByteSerializer.create(78)
                .write(getExtendedInfo().getPrivate())
                .write(serialize(false))
                .toBase58WithChecksum();
    }

    public String getAccountPublic() {
        return ByteSerializer.create(78)
                .write(getExtendedInfo().getPublic())
                .write(serialize(true))
                .toBase58WithChecksum();
    }

}
