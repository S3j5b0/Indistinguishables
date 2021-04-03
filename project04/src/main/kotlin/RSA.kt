import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class RSA {


    fun generateKey(n: Int): KeyPair {
        val keyGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BCFIPS")
        keyGen.initialize(n)
        val key = keyGen.generateKeyPair()
        return key
    }
    fun encrypt(text: ByteArray, key: PublicKey): ByteArray {
        var cipherText: ByteArray? = null
        //
        // get an RSA cipher object and print the provider
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BCFIPS")

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key)
        cipherText = cipher.doFinal(text)
        return cipherText
    }


    fun decrypt(text: ByteArray, key: PrivateKey): ByteArray {
        var dectyptedText: ByteArray? = null
        // decrypt the text using the private key
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BCFIPS")
        cipher.init(Cipher.DECRYPT_MODE, key)
        dectyptedText = cipher.doFinal(text)
        return dectyptedText
    }
}