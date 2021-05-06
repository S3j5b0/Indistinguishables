import java.math.BigInteger
import java.util.Random
import java.util.Scanner
fun main(args: Array<String>) {
    // Definitions
    val shamir : Shamir = Shamir();
    val privacy : DiffPrivacy = DiffPrivacy();
    val rsa : RSA = RSA();
    val scanner : Scanner = Scanner(System.`in`);
    // 1.a
    val keypair = rsa.generateKey(2048);
    // 1.b
    val privateKeyEncoded = keypair.private.encoded;
    val field = shamir.genField();
    val shares : List<Shamir.Share> = shamir.shamirSecretSplitter(5, 3, field, privateKeyEncoded);
    // 2.a
    var input = -1;
    while(true){
        print("Please enter either 1 or 0: ")
        input = scanner.nextInt();
        if(input != 0 || input != 1) break;
    }
    val inputAfterPrivacy = privacy.makePrivate(input, 2.0, 1)
    println("Before encryption: $inputAfterPrivacy")
    //2.b
    val encrypted = rsa.encrypt(inputAfterPrivacy.toString().toByteArray(), keypair.public)
    val privateKeyShamir = shamir.shamirRecoverSecret(field, shares);
    val rec = rsa.recoverPrivateKey(privateKeyShamir.toByteArray())
    val decryptedByteArray = rsa.decrypt(encrypted, rec)
    val decrypted = String(decryptedByteArray).toInt()
    println("After encryption: $decrypted")
}