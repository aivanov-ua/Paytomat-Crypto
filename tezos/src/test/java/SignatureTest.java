import com.paytomat.tezos.PublicKey;
import com.paytomat.tezos.SecretKey;
import com.paytomat.tezos.Signature;
import com.paytomat.tezos.model.SignatureResult;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

/**
 * created by Alex Ivanov on 2019-04-10.
 */
public class SignatureTest {

    private final String TEST_SK = "edskRi6tVmiKNHQ8q3hjXK7HYaU4h6BxdeXeVZznXoasTAcSSd8FdqX7Ntw2zqvWUENG9nAwbU8wrwPi5CQzr9UDKh11tPVwdr";


    @Test
    public void testSignature() {
        SecretKey sk = new SecretKey(TEST_SK);
        PublicKey pk = new PublicKey(sk);
        byte[] message = "My Test Message".getBytes();

        SignatureResult signed = Signature.signForgedOperation(message, sk);
        Assert.assertEquals("9ffd46aad8378e53a03365a52296347a5fd3c0997764b4ddd38d279384ae08908a3cbe08a631d6e222d7029ea234e067dd09bdaa9f072c5be835cb73cdb22308", signed.signature);
        Assert.assertEquals("4d792054657374204d6573736167659ffd46aad8378e53a03365a52296347a5fd3c0997764b4ddd38d279384ae08908a3cbe08a631d6e222d7029ea234e067dd09bdaa9f072c5be835cb73cdb22308", signed.sBytes);
        Assert.assertEquals("edsigttjmcUZtQFKEQxrkzcN5Dq9ouZTYHpRNhywBX72gGQsg3g8KnEnVpsKVWVaQZTWJPbC8mcNCk4UaeRdUKnHDBHiMRsig5M", signed.edsig);

        Assert.assertTrue(Signature.verify(Hex.decode(signed.signature), pk, Hex.decode(signed.hashedOperation)));
    }
}
