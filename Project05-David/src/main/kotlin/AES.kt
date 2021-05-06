import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Hex
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.security.Security

class AES (val keyS : String){
    init {
        Security.addProvider(BouncyCastleFipsProvider())
    }
    val key = keyFromString(keyS)

    val random = SecureRandom()
    val keylength = 256
    fun dec(ciphertext : ByteArray) : ByteArray {
        val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
        cipher.init(Cipher.DECRYPT_MODE, key)

        return cipher.doFinal(ciphertext)
    }

    fun enc(plaintext : String) : ByteArray{
        val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(plaintext.toByteArray())
    }
    // encrypting, but taking bytes instead of string
    fun encb(plaintext : ByteArray) : ByteArray{
        val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding  ")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(plaintext)
    }
    // decrypting, but working with bytes instead of string
    fun decb(ciphertext : ByteArray) : ByteArray {
        val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding ")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return ciphertext

    }


    fun keyFromString(s : String) : SecretKey{
        val keyspec : SecretKeySpec = SecretKeySpec(Hex.decode(s), "AES")
        return keyspec
    }




}