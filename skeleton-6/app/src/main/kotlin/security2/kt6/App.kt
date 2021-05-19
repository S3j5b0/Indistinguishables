package security2.kt6

import kotlinx.coroutines.*
import java.math.BigInteger
import java.net.ServerSocket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory

data class Share(val idx: Int, val secret: BigInteger)

// TODO: Get the corresponding keystore here
// Should be done here
fun getKeyStore(keystore: String, password: String): KeyStore {
    val store: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    store.load(keystore.byteInputStream(), password.toCharArray())
    return store
}

// TODO: Get a TLS client socket for the given key store
// not completely done
fun getTLSClientSocket(store: KeyStore): SSLSocketFactory {
    val context: SSLContext = SSLContext.getInstance("TLS");
    val kmf : KeyManagerFactory = KeyManagerFactory.getInstance("X509")
    kmf.init(store);
    context.init(kmf.keyManagers, );
    return context.socketFactory
}

// TODO: Get a TLS server socket for the given keystore/password and listen on the given port
fun getTLSServerSocket(store: KeyStore, password: String, port: Int): ServerSocket {
    val context: SSLContext
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
