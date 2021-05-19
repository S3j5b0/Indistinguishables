package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import org.bouncycastle.util.encoders.Hex
import java.security.Security
import java.util.*

private val scanner : Scanner = Scanner(System.`in`);
private val privacy : Privacy = Privacy()
private val rsa : RSA = RSA();

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
    var input = -1;
    while(true){
        print("Please enter either 1 or 0: ")
        input = scanner.nextInt();
        if(input != 0 || input != 1) break;
    }
    val answer: Int = input// Answer from the user
    println("Answer is: $answer")
    // TODO: apply the randomized response scheme on answer
    val diffPrivacyAnswer: Int = privacy.makePrivate(answer, 2.0, 1);
    println("Sending answer: $diffPrivacyAnswer")

    // TODO: RSA-encrypt the diffPrivacyAnswer (Use RSA with ECB mode and PKCS1Padding)
    val toSubmit: ByteArray = rsa.encrypt(diffPrivacyAnswer.toString().toByteArray(), rsa.recoverPublicKey(pubKeyBytes))
    println("Encrypted answer: ${Hex.toHexString(toSubmit)}")

    // Submit the encrypted result to the trustee server
    val cipherSock = socketFact.createSocket(trusteeip, trusteeport)
    UserProtocol().sendCipherText(cipherSock, toSubmit)
    cipherSock.close()
}
