package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import org.bouncycastle.util.encoders.Hex
import java.security.Security

fun runUser() {
    // Add BouncyCastle
    Security.addProvider(BouncyCastleJsseProvider())
    Security.addProvider(BouncyCastleFipsProvider())
    // Addresses
    val adminip = "localhost"
    val adminport = 5005
    val trusteeip = "localhost"
    val trusteeport = 5006
    // Load the certificate store of the client
    val trustStore = getKeyStore("client.jks", "passwd")

    // Initialize the client
    val socketFact = getTLSClientSocket(trustStore)

    // Request the public key from the admin server
    val pubKeySock = socketFact.createSocket(adminip, adminport)
    val pubKeyMsg = UserProtocol().getPublicKey(pubKeySock)
    pubKeySock.close()
    val pubKeyBytes = pubKeyMsg.data ?: ByteArray(0)

    // Do the differential privacy algorithm
    // TODO: Get the yes/no (1/0) answer from the user
    val answer: Int // Answer from the user
    println("Answer is: $answer")
    // TODO: apply the randomized response scheme on answer
    val diffPrivacyAnswer: Int
    println("Sending answer: $diffPrivacyAnswer")

    // TODO: RSA-encrypt the diffPrivacyAnswer (Use RSA with ECB mode and PKCS1Padding)
    val toSubmit: ByteArray
    println("Encrypted answer: ${Hex.toHexString(toSubmit)}")

    // Submit the encrypted result to the trustee server
    val cipherSock = socketFact.createSocket(trusteeip, trusteeport)
    UserProtocol().sendCipherText(cipherSock, toSubmit)
    cipherSock.close()
}
