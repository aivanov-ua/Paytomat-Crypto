import com.paytomat.tezos.PublicKey;
import com.paytomat.tezos.SecretKey;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class PublicKeyTest {

    private final String TEST_SK = "edskRi6tVmiKNHQ8q3hjXK7HYaU4h6BxdeXeVZznXoasTAcSSd8FdqX7Ntw2zqvWUENG9nAwbU8wrwPi5CQzr9UDKh11tPVwdr";
    private final String TEST_PK = "edpkuAfEJCEatRgFpRGg3gn3FdWniLXBoubARreRwuVZPWufkgDBvR";

    @Test
    public void testPkFromSeed() {
        String seed = "2e8905819b8723fe2c1d161860e5ee1830318dbf49a83bd451cfb8440c28bd6f";
        SecretKey sk = new SecretKey(Hex.decode(seed));
        Assert.assertEquals(TEST_PK, new PublicKey(sk).toString());
    }

    @Test
    public void testPkFromSk() {
        SecretKey sk = new SecretKey(TEST_SK);
        Assert.assertEquals(TEST_PK, new PublicKey(sk).toString());
    }

    @Test
    public void testPkFromStr() {
        Assert.assertEquals(TEST_PK, new PublicKey(TEST_PK).toString());
    }

    @Test
    public void testPkFromBytes() {
        String hex = "451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b";
        Assert.assertEquals(TEST_PK, new PublicKey(Hex.decode(hex)).toString());
    }

    @Test
    public void testPkFromBytesFail() {
        String hex = "41bde83254ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b";
        try {
            Assert.assertEquals(TEST_PK, new PublicKey(Hex.decode(hex)).toString());
            throw new RuntimeException("Should have failed");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testPkFromStrFail() {
        try {
            Assert.assertEquals(TEST_PK, new PublicKey(TEST_PK.replace("ku", "ab")).toString());
            throw new RuntimeException("Should have failed");
        } catch (IllegalArgumentException ignore) {
        }
    }
}
