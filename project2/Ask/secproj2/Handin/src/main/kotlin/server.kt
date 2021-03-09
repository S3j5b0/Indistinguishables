import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray

fun main() {
    val server = ServerSocket(9999)
    println("Server is running on port ${server.localPort}")

    while(true){
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")
        thread { Handler(client).run() }
    }

    println("Server");
}

class Handler(client: Socket) {
    private val writer: OutputStream = client.getOutputStream()
    private val reader: Scanner = Scanner(client.getInputStream())

    fun run(){
        write("Welcome to Carls server!")

        while(true){
            val text = reader.nextLine()
            println(text.javaClass.name)
            val mes = Cbor.decodeFromByteArray<Message>(text.toByteArray())
            write("message back: $text")
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }
}