package security2.kt6

import kotlinx.coroutines.*
import java.io.FileInputStream
import java.math.BigInteger
import java.net.ServerSocket
import java.nio.file.Paths
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


data class Share(val idx: Int, val secret: BigInteger)

// TODO: Get the corresponding keystore here
// Should be done here
fun getKeyStore(keystore: String, password: String): KeyStore {
    val store: KeyStore = KeyStore.getInstance("JKS")
    val path = Paths.get("").toAbsolutePath().toString()
    //"/home/carl/Documents/git/github/S3j5b0/Indistinguishables/skeleton-6/app/server.jks")
    val file = FileInputStream(path + "/app/" + keystore);
    store.load(file, password.toCharArray())
    return store
}

// TODO: Get a TLS client socket for the given key store
// should be done
fun getTLSClientSocket(store: KeyStore): SSLSocketFactory {
    val context: SSLContext = SSLContext.getInstance("TLS");
    val tmf : TrustManagerFactory = TrustManagerFactory.getInstance("PKIX")
    tmf.init(store)
    context.init(null, tmf.trustManagers, null);
    return context.socketFactory
}

// TODO: Get a TLS server socket for the given keystore/password and listen on the given port
// should be done here
fun getTLSServerSocket(store: KeyStore, password: String, port: Int): ServerSocket {
    val context: SSLContext = SSLContext.getInstance("TLS");
    val kmf : KeyManagerFactory = KeyManagerFactory.getInstance("PKIX");
    //val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    //tmf.init(store)
    kmf.init(store, password.toCharArray())
    //context.init(kmf.keyManagers, tmf.trustManagers, SecureRandom())
    context.init(kmf.keyManagers, null, null);
    return context.serverSocketFactory.createServerSocket(port)
}

fun main() = runBlocking {
    val adminServer = AdminServer()
    val admin = launch(Dispatchers.Default) {
        println("startup of admin server")
        adminServer.run()
    }
    val trustee = launch(Dispatchers.Default) {
        println("startup of trustee server")
        runTrusteeServer()
    }
    val users = launch {
        println("startup of clients")
        delay(1000 * 3)
        println("clients finished waiting")
        runUser()
        runUser()
        runUser()
    }
    users.join()
    println("submission of trustees")
    runTrusteeClient(2)
    runTrusteeClient(3)
    runTrusteeClient(7)
    admin.cancel()
    trustee.cancel()
}
