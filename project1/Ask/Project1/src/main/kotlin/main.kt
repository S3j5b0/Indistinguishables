import kotlinx.serialization.descriptors.PrimitiveKind
import org.bouncycastle.crypto.internal.CipherKeyGenerator
import org.bouncycastle.crypto.internal.KeyGenerationParameters
import org.bouncycastle.crypto.internal.macs.HMac
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Hex
import java.awt.HeadlessException
import java.io.File
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.io.FileNotFoundException

import java.io.FileOutputStream
import java.security.Key
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec


class main {


}

val keylength = 16
val random = SecureRandom()
//SecureRandom random = new SecureRandom();
fun main(args : Array<String>) {
    Security.addProvider(BouncyCastleFipsProvider())

    // b, i:
    println("b, i: \n")

    val key : SecretKey = genKey()

    println("secret key:" + key)
    val plaintext : String = "Realy secret stuff comes here"

    println(plaintext.length)
    println("original plaintext before encryption: $plaintext")
    val tup = enc(plaintext, key)
    val formattetCipher = Hex.toHexString(tup)
    println("encrypted ciphertext:   $formattetCipher")

    val m = dec(tup, key)

    println("decrypted plaintext: " + m)


    // b, ii
    println("b, ii:\n")

    val file = File("C:\\Users\\Ask\\Git\\Indistinguishables\\bouncy\\src\\main\\resources\\screenshot.bmp")
    val fileBytes = file.readBytes()
    val headerBytes = fileBytes.sliceArray(IntRange(0,137))
    val bodybytes = fileBytes.sliceArray(IntRange(138,fileBytes.size-1))
    val cryptFile = encb(bodybytes, key)
    val newfilePayload = headerBytes + cryptFile

    // print("ciphered file: $formattedCipherFile")
    writeToFile(newfilePayload, "badEncryption")


    // c, i:
    println("c, i \n")
    val key2 : Key = SecretKeySpec(Hex.decode("e6d4d9472cf9b7a92a652fc7e1f3b4124906cff47f42115d77d64709f2177503"), "AES")
    val iv : ByteArray = Hex.decode("000102030405060708090a0b")


    val msg = Hex.decode("a2d21879269610eab7c16250b3b4bd81fc41b99738d7f8f2966ecd0bb2e5682a")
    //  val ciphertext  =  encGCM("12345678123456781234567812345678".toByteArray(), key2, iv)
    val decryptedText = decGCM(msg, key2,iv)


    print("let's see what peter has for us: $decryptedText")

    // c, ii:


    val GSMCipheredFile : ByteArray = encGCM(bodybytes, key2, iv)


    val newGSMFile = headerBytes + GSMCipheredFile

    writeToFile(newGSMFile, "betterencryption")


    /***
    It is quite clear that ECB mode is not safe, since it will encrypt identical pieces of plaintext in the same manner. GCM mode uses a IV that "counts", in such a manner that the same IV is never used twice.
    At least, we can say that ECB leaks a lot, GCM does not leak.

     ***/
}
fun writeToFile(array: ByteArray?, name : String ) {


    try {
        val path = "C:\\\\Users\\\\Ask\\\\Git\\\\Indistinguishables\\\\bouncy\\\\src\\\\main\\\\resources\\\\$name.bmp"
        val stream = FileOutputStream(path)
        stream.write(array)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}

fun keyFromString(s : String) : SecretKey{
    val keyspec : SecretKeySpec = SecretKeySpec(s.toByteArray(), "AES")
    return keyspec
}

fun genKey() : SecretKey{
    val keyBytes : ByteArray = ByteArray(keylength)
    random.nextBytes(keyBytes)
    val keyspec : SecretKeySpec = SecretKeySpec(keyBytes, "AES")
    return keyspec

}
fun dec(ciphertext : ByteArray, key: SecretKey) : String {
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
    cipher.init(Cipher.DECRYPT_MODE, key)

    return String(cipher.doFinal(ciphertext))

}
fun enc(plaintext : String, key : SecretKey) : ByteArray{
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(plaintext.toByteArray())
}
// encrypting, but taking bytes instead of string
fun encb(plaintext : ByteArray, key : SecretKey) : ByteArray{
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding  ")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(plaintext)
}
// decrypting, but working with bytes instead of string
fun decb(ciphertext : ByteArray, key: SecretKey) : ByteArray {
    val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS7Padding ")
    cipher.init(Cipher.DECRYPT_MODE, key)
    return ciphertext

}
fun encGCM(plaintext : ByteArray, key : Key,iv : ByteArray) : ByteArray{
    val cipher : Cipher = Cipher.getInstance("AES/GCM/NOPADDING  ")
    val keyBytes : ByteArray = ByteArray(keylength)
    random.nextBytes(keyBytes)
    val macspec : GCMParameterSpec = GCMParameterSpec(128, iv )
    cipher.init(Cipher.ENCRYPT_MODE, key, macspec)
    return cipher.doFinal(plaintext)
}
// decrypting, but working with bytes instead of string
fun decGCM(ciphertext : ByteArray, key: Key, iv : ByteArray) : String {
    val cipher : Cipher = Cipher.getInstance("AES/GCM/NOPADDING ")
    val macspec : GCMParameterSpec = GCMParameterSpec(128, iv )

    cipher.init(Cipher.DECRYPT_MODE, key, macspec)
    return String(cipher.doFinal(ciphertext))

}