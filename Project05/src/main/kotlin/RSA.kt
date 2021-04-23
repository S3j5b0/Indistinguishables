import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

import org.bouncycastle.asn1.eac.RSAPublicKey

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.Hex
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

import java.security.spec.PKCS8EncodedKeySpec

import java.security.spec.EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import java.security.KeyFactory
import java.util.Base64.getDecoder

class RSA {
    constructor() {
        Security.addProvider(BouncyCastleFipsProvider())
    }



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