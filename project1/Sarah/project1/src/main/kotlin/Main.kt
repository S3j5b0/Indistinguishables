import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.util.encoders.Hex
import java.io.File
import java.security.Key
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


/*  I am in a group with David Martin Carl and Ask Harup Sejsbo,
    and we are called the Indistinguishable */


/**************************   Answer for (d)   **************************/

/*  If we look at the image results of each encryption scheme, we
    see that using AES with ECB mode, padded with PKCS7, the orig-
    inal image is slightly visible, whereas with the AES encryption
    /decryption in GCM-mode with a tag length of 128 bits and an
    initialization vector of 12 bytes length, the images does not
    reveal any information of the original image.

    Therefore, the latter scheme is the best implementation for
    Bob’s remote backup. */


class Main {

}

private const val keyLen = 32
private const val IVLen = 12
private const val tagLen = 128
private val random = SecureRandom()

// creates a random secret key of length 32
private fun defineKey(): Key {
    val keyBytes = ByteArray(keyLen)
    random.nextBytes(keyBytes)
    return SecretKeySpec(keyBytes, "AES")
}

// creates a random initialisation vector
private fun generateIV(len: Int): ByteArray {
    val randomSecureRandom = SecureRandom()
    val iv = ByteArray(len)
    randomSecureRandom.nextBytes(iv)
    return iv
}

// encryption function using AES with ECB mode, padded with PKCS7
private fun encryptAESECBPKCS7(msg: ByteArray): Pair<ByteArray, Key> {
    // create key and perform AES encryption in ECB-mode using the PKCS7 padding scheme
    val c = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
    val key = defineKey()
    c.init(Cipher.ENCRYPT_MODE, key)
    return Pair(c.doFinal(msg), key)
}

// decryption function using AES with ECB mode, padded with PKCS7
private fun decryptAESECBPKCS7(cip: ByteArray, key: Key): ByteArray {
    val m = Cipher.getInstance("AES/ECB/PKCS7Padding", "BCFIPS")
    m.init(Cipher.DECRYPT_MODE, key)
    return m.doFinal(cip)
}

// encryption function using AES with GCM mode, with a tag length of 128 bits,
// an initialization vector of 12 bytes length
private fun encryptAESGCM(msg: ByteArray, iv: ByteArray): Pair<ByteArray, Key> {
    val c = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS")
    val key = defineKey()
    val gcmParameters = GCMParameterSpec(tagLen, iv)
    c.init(Cipher.ENCRYPT_MODE, key, gcmParameters)
    return Pair(c.doFinal(msg), key)
}

// decryption function using AES with GCM mode, with a tag length of 128 bits,
// an initialization vector of 12 bytes length
private fun decryptAESGCM(cip: ByteArray, key: Key, iv: ByteArray): ByteArray {
    val c = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS")
    val gcmParameters = GCMParameterSpec(tagLen, iv)
    c.init(Cipher.DECRYPT_MODE, key, gcmParameters)
    return c.doFinal(cip)
}

// reads the given secret key, IV and decryption string from a file
private fun readDataFromFile(path: String): Triple<Key, ByteArray, ByteArray> {
    val secretFile = File(path).bufferedReader().readLines()
    val secretKey  = SecretKeySpec(Hex.decode(secretFile[0]), "AES")
    val secretIV   = Hex.decode(secretFile[1])
    val secretDec  = Hex.decode(secretFile[2])
    return Triple(secretKey, secretIV, secretDec)
}


fun main() {
    // defining provider Bouncy Castle
    Security.addProvider(BouncyCastleFipsProvider())



    /**************************   Answer for (b)   **************************/

    // loading files for encryption
    val msg = File("src/main/resources/data/msg").readBytes()
    val img = File("src/main/resources/data/screenshot.bmp").readBytes()

    // using AES encryption with ECB mode and PKCS7 padding on string from file
    val (encStr, keyStr) = encryptAESECBPKCS7(msg)
    val decStr = String(decryptAESECBPKCS7(encStr, keyStr))

    // testing reading the msg file
    val msgStr = String(msg)
    val encHex = Hex.toHexString(encStr)

    println("Original  message of b.i: $msgStr")
    println("Encrypted message of b.i: $encHex")
    println("Decrypted message of b.i: $decStr")


    // preprocessing image
    val header = img.sliceArray(0..137)
    val shortImg = img.drop(138).toByteArray()


    // using AES encryption with ECB mode and PKCS7 padding on image
    val (encImgEBC, keyImgEBC) = encryptAESECBPKCS7(shortImg)
    val fullImgEncEBC = header + encImgEBC
    val decImgEBC = decryptAESECBPKCS7(encImgEBC, keyImgEBC)
    val fullImgDecEBC = header + decImgEBC


    // recreate image after encryption and decryption
    File("src/main/resources/data/screenshot_enc_ecb.bmp").writeBytes(fullImgEncEBC)
    File("src/main/resources/data/screenshot_dec_ecb.bmp").writeBytes(fullImgDecEBC)



    /**************************   Answer for (c)   **************************/

    // using AES decryption with GCM mode using data from file
    val (secretKey, secretIV, secretDec) = readDataFromFile("src/main/resources/data/secret")
    val secret = String(decryptAESGCM(secretDec, secretKey, secretIV))
    println("\nThe secret string of question c.i is: $secret")


    // using AES encryption with GCM mode on image
    val iv = generateIV(IVLen)
    val (encImgGCM, keyImgGCM) = encryptAESGCM(shortImg, iv)
    val fullImgEncGCM = header + encImgGCM
    val decImgGCM = decryptAESGCM(encImgGCM, keyImgGCM, iv)
    val fullImgDecGCM = header + decImgGCM


    // recreate images after encryption and decryption
    File("src/main/resources/data/screenshot_enc_gcm.bmp").writeBytes(fullImgEncGCM)
    File("src/main/resources/data/screenshot_dec_gcm.bmp").writeBytes(fullImgDecGCM)
}

