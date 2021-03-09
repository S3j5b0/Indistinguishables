import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor

fun main() {
    val address = "127.0.0.1"
    val port = 9999

    val client = Client(address, port)
    client.run()
}

class Client(address: String = "127.0.0.1", port: Int = 9999){
    private val connection: Socket = Socket(address, port)
    private val writer: OutputStream = connection.getOutputStream()

    private val reader: Scanner = Scanner(connection.getInputStream())

    fun run(){
        thread { read() }
        while(true){
            val input = readLine() ?: ""
            //write(input)
            write2(Message("message", "hmac"))
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun write2(message: Message){
        writer.write(Cbor.encodeToByteArray(message))
        println("sent")
    }

    private fun read() {
        while(true) {
            println(reader.nextLine())
        }
    }
}