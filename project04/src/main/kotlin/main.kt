
import org.bouncycastle.asn1.eac.RSAPublicKey
import java.security.KeyPair
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.util.encoders.Hex
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.Cipher

import java.security.PublicKey
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec

import java.security.spec.EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import java.security.KeyFactory
import java.util.Base64.getDecoder




fun main(args: Array<String>) {

    val constructedPubkey = "ab5c44b756412fcb4e43bcbbc069604eee3594686a3324f793f143a59ea1b027a1" +
            "e8cc3bce090e12310d5ac46acb20b7967ceb86badb949b79e91a5f317ed4b74a8c0e400f302f3e533c255" +
            "b9e6acc76f2a0ab6ab69d48c5a297840ef1a9c5bddce3b94be863bebbd7956675723ad1829b75d0973a06" +
            "8db74468ff04529ef2d2de55f60b00bc0e7b015188ed34e4a50867e96f08c50df7b0883b6269e0340a688" +
            "827806c92c6a7b830e1e27477e7c56923e6936cb0021eb48246051f0ae6ae848598f57950ac705352ead1e" +
            "a0a6028f17ec869a2cd9880becefdf89cf0a8c56fd37decaa46174e784bf70c81e61f7a6bc2d57aeecec1f1" +
            "4021b9d859fe91f326af4a24559cc9335c9bbc9fefd5a40f8c533164714dabe03c4bb6dc3062ca169fb0e54" +
            "d5a0f39ce418e4f3a50cef913c7"

   // val someC = "c788822ec62e737fe41a94ba3138d0154a3a45b15b6a101ed4f7816691bcb03d80167aaf4bbefc31568e967a861a685c14ca2583cf14eb5165e6c8234bedf1973ea7cc6f904a92f26db2e029311c03b98f6747e91743dd5ae8e1e76e724fac4f41cfea739c2aa14d2448fb92951bf2c972a824524a4181f91761ed251d31893016672b249c9b8d1ebf68d69e51360eac498b49e9c4022aa185ba5e04551e47c173d79e54fcc9257a9b47fe363d5c4d1104461d30b01043880672c321e68186d5"
    println("decrypting the shared secret: ")
    val k = "e6d4d9472cf9b7a92a652fc7e1f3b4124906cff47f42115d77d64709f2177503"
    val aesScheme  = AES(k)

    val decrypted = aesScheme.dec(Hex.decode(constructedPubkey))

    println(Hex.toHexString(decrypted))

    val pk = PubKeyFromBytes(decrypted)
    println("And the public key is: ")
    println(pk)
    println("___________________________________________")

    println("And now encrypting \"You got it right \": ")

    val message = "You got it right"

    val rsaScheme = RSA()

    val c = rsaScheme.encrypt(message.toByteArray(), pk)

    println("Ciphertext is: ")

    println(Hex.toHexString(c))



    /*
    Security.addProvider(BouncyCastleFipsProvider())
    val keyPair = generateKey(1024)
    val s = "this is a secret"
    println("our secret is: " + s)
    val sBytes = s.toByteArray()
    println("as bytes: " + sBytes)

    println("generating keys:")
    println(keyPair.private)
    println(keyPair.public)
    println("_____________________________________")
    val C = encrypt(sBytes, keyPair.public)
    println("Encrypting and getting ciphertext: ")
    println(C)
    println(C.javaClass.name)
    println("_____________________________________")
    println("decrypting: ")

    val m = decrypt(C, keyPair.private)

    println("first getting bytearray:")
    println(Hex.toHexString(m))
    println("and now getting our secret back")
    println(String(m))

    println("______________________________________")
    val constructedPubkey = "00ab5c44b756412fcb4e43bcbbc069604eee3594686a3324f793f143a59ea1b027a1" +
            "e8cc3bce090e12310d5ac46acb20b7967ceb86badb949b79e91a5f317ed4b74a8c0e400f302f3e533c255" +
            "b9e6acc76f2a0ab6ab69d48c5a297840ef1a9c5bddce3b94be863bebbd7956675723ad1829b75d0973a06" +
            "8db74468ff04529ef2d2de55f60b00bc0e7b015188ed34e4a50867e96f08c50df7b0883b6269e0340a688" +
            "827806c92c6a7b830e1e27477e7c56923e6936cb0021eb48246051f0ae6ae848598f57950ac705352ead1e" +
            "a0a6028f17ec869a2cd9880becefdf89cf0a8c56fd37decaa46174e784bf70c81e61f7a6bc2d57aeecec1f1" +
            "4021b9d859fe91f326af4a24559cc9335c9bbc9fefd5a40f8c533164714dabe03c4bb6dc3062ca169fb0e54" +
            "d5a0f39ce418e4f3a50cef913c7"*/

   // val bobPubKeySpec = X509EncodedKeySpec(Hex.decode(constructedPubkey))

    //val keyfact : KeyFactory = KeyFactory.getInstance("RSA")

    //val pk = keyfact.generatePublic(bobPubKeySpec)




  //  val tmp0 = Base64.decode(constructedPubkey)
   // RSAPublicKey.getInstance(tmp0)

    //println(tmp0)

   // val decoded: ByteArray = Base64.decode(constructedPubkey)
   // val kf = KeyFactory.getInstance("RSA")
   // val generatedPublic = kf.generatePublic(X509EncodedKeySpec(decoded))


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


fun PrivKeyFromString(key: String): PrivateKey {
    val keyFactory = KeyFactory.getInstance("RSA", "BCFIPS")
    val privateKeySpec: EncodedKeySpec = PKCS8EncodedKeySpec(key.toByteArray())
    return keyFactory.generatePrivate(privateKeySpec)
}


fun PubKeyFromString(key: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA", "BCFIPS" )
    val publicKeySpec: EncodedKeySpec = X509EncodedKeySpec(Base64.decode(key))
    return keyFactory.generatePublic(publicKeySpec)
}

fun PubKeyFromBytes(key: ByteArray): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA", "BCFIPS" )
    val publicKeySpec: EncodedKeySpec = X509EncodedKeySpec(key)
    return keyFactory.generatePublic(publicKeySpec)
}


/*
 X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(bobEncodedPubKey);
 KeyFactory keyFactory = KeyFactory.getInstance("DSA");
 PublicKey bobPubKey = keyFactory.generatePublic(bobPubKeySpec);
 Signature sig = Signature.getInstance("DSA");
 sig.initVerify(bobPubKey);
 sig.update(data);
 sig.verify(signature);
 */