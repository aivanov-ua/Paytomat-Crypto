package com.paytomat.bip44generator;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Alex Ivanov on 2019-02-12.
 */
public class HDPath {

    public static final HDPath ROOT = new HDPath();
    private static final String HARDENED_SYMBOL = "'";
    private static final String PATH_SPLIT_SYMBOL = "/";

    private final HDPath parent;
    private final int pathIndex;
    private final boolean isHardened;

    public static HDPath valueOf(String path) {
        if (path == null || path.isEmpty()) return ROOT;
        String[] subPaths = path.split(PATH_SPLIT_SYMBOL);
        HDPath hdPath = ROOT;
        for (String subPath : subPaths) {
            if (subPath.equals("m")) continue;
            boolean isHardened = subPath.contains(HARDENED_SYMBOL);
            int index = Integer.valueOf(subPath.replace(HARDENED_SYMBOL, ""));
            hdPath = hdPath.getChild(index, isHardened);
        }
        return hdPath;
    }

    private HDPath() {
        this.parent = null;
        this.pathIndex = 0;
        this.isHardened = true;
    }

    private HDPath(HDPath parent, int pathIndex, boolean isHardened) {
        this.parent = parent;
        this.pathIndex = pathIndex;
        this.isHardened = isHardened;
    }

    public HDPath getParent() {
        return parent;
    }

    public HDPath getChild(int pathIndex, boolean isHardened) {
        return new HDPath(this, pathIndex, isHardened);
    }

    public boolean isHardened() {
        return isHardened;
    }

    public int getValue() {
        return pathIndex | (isHardened ? 0x80000000 : 0);
    }

    public List<Integer> getAddressN() {
        ArrayList<Integer> ret = new ArrayList<>();
        HDPath ak = this;
        while (ak.parent != null) {
            ret.add(0, ak.getValue());
            ak = ak.parent;
        }

        return ret;
    }

    byte getDepth() {
        int depth = toString().split(PATH_SPLIT_SYMBOL).length - 1;
        return (byte) Math.max(depth, 0);
    }

    @Override
    public String toString() {
        if (parent == null) return "m";
        return parent.toString() + PATH_SPLIT_SYMBOL + pathIndex + (isHardened ? HARDENED_SYMBOL : "");
    }

}
