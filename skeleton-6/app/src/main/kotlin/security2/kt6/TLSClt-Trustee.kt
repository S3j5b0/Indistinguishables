package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import java.security.Security

// idx will be the index of this trustee
fun runTrusteeClient(idx: Int) {
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

    // Request our share from the admin server
    val shareSock = socketFact.createSocket(adminip, adminport)
    val msg = TrusteeClientProtocol().getShare(shareSock, idx)
    shareSock.close()
    val secret = msg.data ?: ByteArray(1)

    // Submit our share to the trustee server
    val decryptSock = socketFact.createSocket(trusteeip, trusteeport)
    TrusteeClientProtocol().sendShare(decryptSock, idx, secret)
    decryptSock.close()
}
