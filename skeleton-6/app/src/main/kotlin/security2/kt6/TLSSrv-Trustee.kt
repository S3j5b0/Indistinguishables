package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import java.math.BigInteger
import java.security.Security

fun runTrusteeServer() {
    // Add BouncyCastle
    Security.addProvider(BouncyCastleFipsProvider())
    Security.addProvider(BouncyCastleJsseProvider())
    // https://superuser.com/questions/126121/how-to-create-my-own-certificate-chain
    // https://jamielinux.com/docs/openssl-certificate-authority/sign-server-and-client-certificates.html

    // Load the certificate store of the server
    val serverStore = getKeyStore("server.jks", "passwd")

    // Startup of the server
    val port = 5006

    // Initialize the trustee server
    val trustee = TrusteeProtocol()

    // Fetch field value from admin server
    val adminIP = "localhost"
    val adminPort = 5005
    val trustStore = getKeyStore("client.jks", "passwd")
    val socketFact = getTLSClientSocket(trustStore)
    val fieldSock = socketFact.createSocket(adminIP, adminPort)
    val msg = trustee.getField(fieldSock)
    fieldSock.close()
    val field = BigInteger(msg.data ?: ByteArray(1))

    // Create the trustee server socket
    val sSock = getTLSServerSocket(serverStore, "passwd", port)

    // Accept and handle incoming connections
    while (true) {
        val con = sSock.accept()
        trustee.handleMessage(con!!, field)
    }
}
