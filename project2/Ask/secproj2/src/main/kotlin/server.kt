
import java.io.BufferedReader
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.stream.Stream
import java.io.PrintWriter




class connect {


}


fun main(args : Array<String>) {
    val server = ServerSocket(5000)
    println("Server is running on port ${server.localPort}")


    val clientSocket = server.accept()

    print("connected to client: "+ clientSocket.inetAddress.toString())

    val out = PrintWriter(clientSocket.getOutputStream(), true)
    val ind = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
    val greeting: String = ind.readLine()

    println(greeting)

}

/*

fun server() {
    val server = ServerSocket(9999)
    println("Server running on port ${server.localPort}")
    val client = server.accept()
    println("Client connected : ${client.inetAddress.hostAddress}")
    val scanner = Scanner(client.inputStream)
    while (scanner.hasNextLine()) {
        println(scanner.nextLine())
        break
    }
    server.close()
}

fun client() {
    val client = Socket("127.0.0.1", 9999)
    client.outputStream.write("Hello from the client!".toByteArray())
    client.close()
}
 */