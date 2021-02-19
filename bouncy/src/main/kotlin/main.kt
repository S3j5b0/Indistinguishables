import org.bouncycastle.crypto.internal.CipherKeyGenerator
import org.bouncycastle.crypto.internal.KeyGenerationParameters
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class main {


}

val keylength = 16
val random = SecureRandom()
//SecureRandom random = new SecureRandom();
fun main(args : Array<String>) {

    val f = File("C:\\ADPRO\\bouncy\\src\\main\\kotlin\\screenshot.bmp")

    
/*
        Security.addProvider(new BouncyCastleProvider());

        String plaintext = "hello world";
        byte [] ciphertext = encrypt(plaintext);
        String recoveredPlaintext = decrypt(ciphertext);

        System.out.println(recoveredPlaintext);
 */
    Security.addProvider(BouncyCastleFipsProvider())

    val key : SecretKey = genKey()
    val plaintext : String = "Really secret stuff comes h"

    println(plaintext.length)
    println("original plaintext before encryption: "+ plaintext)
    val tup = enc(plaintext, key)
    println("encrypted ciphertext:  " + tup)
    val m = dec(tup, key)

    print("decrypted plaintext: " + m)

}

fun genKey() : SecretKey{
    val keyBytes : ByteArray = ByteArray(keylength)
    random.nextBytes(keyBytes)
    val keyspec : SecretKeySpec = SecretKeySpec(keyBytes, "AES")
    return keyspec

}
fun dec(ciphertext : ByteArray, key: SecretKey) : String {
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding ")
    cipher.init(Cipher.DECRYPT_MODE, key)
    return String(cipher.doFinal(ciphertext))

}
fun enc(plaintext : String, key : SecretKey) : ByteArray{
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding  ")
    val IVstr : String = "0123456789abcdef";
    val IV = IVstr.toByteArray()
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(plaintext.toByteArray())
}
