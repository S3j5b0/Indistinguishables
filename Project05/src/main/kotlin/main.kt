import org.bouncycastle.util.encoders.Hex
import java.math.BigInteger
import java.util.Random
import Shamir
class main {
}


fun main(args: Array<String>) {

    val rsa = RSA()
    val keypair = rsa.generateKey(2048)

    val pubK = keypair.public
    val secK = keypair.private
    println(pubK)
    println(secK)
    val KeyAsInteger = BigInteger(secK.encoded)

    println("________________________________________________________________________________")
    println(KeyAsInteger)
    println(KeyAsInteger.bitLength())

    val four = 6.toBigInteger()

    val rng =  Random(42)
   // val field = BigInteger(9735,1,rng)

    val field = BigInteger(9726, 1, rng)




    val shamir = Shamir()

    shamir.shamirSecretSplitter(10, 5, field, secK.encoded)

    //hamir.shamirSecretSplitter()'




}