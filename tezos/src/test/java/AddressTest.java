import com.paytomat.tezos.Address;
import com.paytomat.tezos.PublicKey;
import com.paytomat.tezos.SecretKey;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-04-08.
 */
public class AddressTest {

    private final String TEST_SK = "edskRi6tVmiKNHQ8q3hjXK7HYaU4h6BxdeXeVZznXoasTAcSSd8FdqX7Ntw2zqvWUENG9nAwbU8wrwPi5CQzr9UDKh11tPVwdr";
    private final String TEST_PK = "edpkuAfEJCEatRgFpRGg3gn3FdWniLXBoubARreRwuVZPWufkgDBvR";
    private final String TEST_ADDRESS = "tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn";

    private final SecretKey TEST_SEC_KEY = new SecretKey(TEST_SK);
    private final PublicKey TEST_PUB_KEY = new PublicKey(TEST_PK);

    @Test
    public void testAddressFromPk() {
        Assert.assertEquals(TEST_ADDRESS, new Address(TEST_PUB_KEY).toString());
    }

    @Test
    public void testAddressFromSk() {
        Assert.assertEquals(TEST_ADDRESS, new Address(TEST_SEC_KEY).toString());
    }

    @Test
    public void testAddressFromString() {
        Assert.assertEquals(TEST_ADDRESS, new Address(TEST_ADDRESS).toString());
    }

    @Test
    public void testAddressFromBytes() {
        Assert.assertEquals(TEST_ADDRESS, new Address(Hex.decode("c83b9266f93eb6c67f32827cd86c8f374d7532af")).toString());
    }
}
