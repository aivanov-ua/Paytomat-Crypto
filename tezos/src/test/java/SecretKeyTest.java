import com.paytomat.tezos.SecretKey;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class SecretKeyTest {

    private final String TEST_PK = "edskRi6tVmiKNHQ8q3hjXK7HYaU4h6BxdeXeVZznXoasTAcSSd8FdqX7Ntw2zqvWUENG9nAwbU8wrwPi5CQzr9UDKh11tPVwdr";

    @Test
    public void testPkFromSeed() {
        String seed = "2e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f";
        Assert.assertEquals(TEST_PK, new SecretKey(Hex.decode(seed)).getWif());
    }

    @Test
    public void testPkFromPkStr() {
        Assert.assertEquals(TEST_PK, new SecretKey(TEST_PK).getWif());
    }

    @Test
    public void testPk64() {
        String hex = "2e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b";
        Assert.assertEquals(TEST_PK, new SecretKey(Hex.decode(hex)).getWif());
    }

    @Test
    public void testPk68() {
        String hex = "2bf64e072e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b";
        Assert.assertEquals(TEST_PK, new SecretKey(Hex.decode(hex)).getWif());
    }

    @Test
    public void testPk64Fail() {
        String hex = "2bf64e072e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e";
        Assert.assertNotEquals(TEST_PK, new SecretKey(Hex.decode(hex)).getWif());
    }

    @Test
    public void testPk68Fail() {
        try {
            String hex = "2af64e072e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b";
            Assert.assertNotEquals(TEST_PK, new SecretKey(Hex.decode(hex)).getWif());
            throw new RuntimeException("Should have failed");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testPk69Fail() {
        try {
            String hex = "2bf64e072e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8eaa";
            Assert.assertEquals(TEST_PK, new SecretKey(Hex.decode(hex)).getWif());
            throw new RuntimeException("Should have failed");
        } catch (IllegalArgumentException ignored) {
        }
    }
}
