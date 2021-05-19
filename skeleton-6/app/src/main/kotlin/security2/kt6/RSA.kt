package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import java.security.*
import javax.crypto.Cipher

import java.security.spec.X509EncodedKeySpec

import java.security.KeyFactory

import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec


class RSA {

    constructor(){
        Security.addProvider(BouncyCastleFipsProvider())
    }

    fun generateKey(n: Int): KeyPair {

        val keyGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BCFIPS")
        keyGen.initialize(n)
        val key = keyGen.generateKeyPair()
        return key
    }

    fun recoverPrivateKey(key: ByteArray): PrivateKey {
        return KeyFactory.getInstance("RSA", "BCFIPS").generatePrivate(PKCS8EncodedKeySpec(key));
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