package security2.kt6

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
import java.math.BigInteger
import java.net.ServerSocket
import java.security.KeyPair
import java.security.Security

class AdminServer {

    private val rsa : RSA = RSA();
    private val admin: AdminProtocol
    private val sSock: ServerSocket

    fun run() {
        // Accept and handle incoming connections
        while (true) {
            println("Accepting connection")
            val con = sSock.accept()
            admin.handleMessage(con)
        }
    }

    init {
        // Add BouncyCastle
        Security.addProvider(BouncyCastleFipsProvider())
        Security.addProvider(BouncyCastleJsseProvider())
        // https://superuser.com/questions/126121/how-to-create-my-own-certificate-chain
        // https://jamielinux.com/docs/openssl-certificate-authority/sign-server-and-client-certificates.html
        println("Admin starting")

        // Load the certificate store of the server
        val serverStore = getKeyStore("server.jks", "passwd")

        // Startup of the server
        val port = 5005
        sSock = getTLSServerSocket(serverStore, "passwd", port)!!

        // Initializing data for our protocol:
        // TODO: Generate RSA keypair here
        // ...
        val keypair : KeyPair = rsa.generateKey(2048)
        val pub: ByteArray = keypair.public.encoded; // public key
        val priv: ByteArray = keypair.private.encoded;// private key
        println("RSA keys generated")

        // Generate a suitable prime field to hold the private key
        println("Generating field")
        // TODO: Generate a suitable field here
        val field: BigInteger
        // Split the private key into 10 shares and allow reconstruction by 3 peers
        println("Splitting secret")
        // TODO: Split the private key into the shares
        val shares: List<Share>
        // Add the shares, public key and field to the server
        admin = AdminProtocol(pub, shares, field)
    }
}
