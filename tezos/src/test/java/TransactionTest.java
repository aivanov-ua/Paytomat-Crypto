import com.paytomat.tezos.SecretKey;
import com.paytomat.tezos.TransactionHelper;
import com.paytomat.tezos.model.FeeModel;
import com.paytomat.tezos.model.Tez;
import com.paytomat.tezos.model.Transaction;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * created by Alex Ivanov on 2019-04-11.
 */
public class TransactionTest {

    @Test
    public void testTxPrepare() {
        Map<String, Object> map = TransactionHelper.prepareTransferOperation("tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn",
                "tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn",
                new Tez(1),
                new FeeModel(Tez.ZERO, new Tez("0.011000"), new Tez("0.000300")));

        Assert.assertEquals("transaction", map.get("kind"));
        Assert.assertEquals("0", map.get("fee"));
        Assert.assertEquals("11000", map.get("gas_limit"));
        Assert.assertEquals("300", map.get("storage_limit"));
        Assert.assertEquals("1000000", map.get("amount"));
        Assert.assertEquals("tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn", map.get("destination"));
        Assert.assertEquals("tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn", map.get("source"));
        Assert.assertEquals(7, map.size());
    }

    @Test
    public void testTransactionSerialization() {
        SecretKey sk = new SecretKey("edskRi6tVmiKNHQ8q3hjXK7HYaU4h6BxdeXeVZznXoasTAcSSd8FdqX7Ntw2zqvWUENG9nAwbU8wrwPi5CQzr9UDKh11tPVwdr");

        Map<String, Object> reveal = new HashMap<>();
        reveal.put("public_key", "edpkuAfEJCEatRgFpRGg3gn3FdWniLXBoubARreRwuVZPWufkgDBvR");
        reveal.put("gas_limit", "11000");
        reveal.put("storage_limit", "300");
        reveal.put("kind", "reveal");
        reveal.put("fee", "1267");
        reveal.put("source", "tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn");
        reveal.put("counter", "1217398");

        Map<String, Object> transfer = new HashMap<>();
        transfer.put("amount", "1000000");
        transfer.put("storage_limit", "300");
        transfer.put("gas_limit", "11000");
        transfer.put("kind", "transaction");
        transfer.put("fee", "0");
        transfer.put("destination", "tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn");
        transfer.put("counter", "1217399");
        transfer.put("source", "tz1dtmCcU7Ng29oWrEc5xJcrQfMnKgoqT7mn");

        Map<String, Object>[] operations = new HashMap[2];
        operations[0] = reveal;
        operations[1] = transfer;

        Map<String, Object> map = Transaction.create("PsddFKi32cMJ2qPjf43Qv5GDWLDPZb3T3bF6fLKiF5HtvHNU7aP",
                "PsddFKi32cMJ2qPjf43Qv5GDWLDPZb3T3bF6fLKiF5HtvHNU7aP",
                operations,
                "cb148581711e99b1666281e7a5ad858fb5e872048e91265b34f552ad29463801070000c83b9266f93eb6c67f32827cd86c8f374d7532aff309caa94af855ac0200451bde832454ba73e6e0de313fcf5d1565ec51080edc73bb19287b8e0ab2122b080000c83b9266f93eb6c67f32827cd86c8f374d7532af00cba94af855ac02c0843d0000c83b9266f93eb6c67f32827cd86c8f374d7532af00",
                sk).toMap();

        Assert.assertEquals("edsigu153g2znWM4wx4i5AGFKitNHgC3nBLgXMuY8yVFJcyENmgXgjjNsiG1YvijZ9oM9wjy8Y7AFc73279CsSZJa3VRDUahdtq", map.get("signature"));

    }
}
