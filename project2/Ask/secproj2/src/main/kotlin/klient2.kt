import kotlinx.serialization.Serializable
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlinx.serialization.*
import kotlinx.serialization.cbor.*
import org.bouncycastle.util.encoders.Hex
import java.time.LocalDateTime
import java.time.ZoneOffset


val key = "key".toByteArray()
val port = 9999
val address = "localhost"
fun main(args: Array<String>) {


    val client = Client(address, port)



    val connected = true
    while (connected) {
        println("write message to get it verified at server")
        val msg = readLine() ?: ""
        val mac = Hex.toHexString(calcHmacSha256(key, msg))
        val payload = Cbor.encodeToByteArray(Payloadk(msg, mac, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC )))
        client.writeB(payload)

        println("\n")


        while (!client.reader.hasNext()){


        }
        val text = client.reader.nextLine()
        println(text)

        println("\n \n ___________________________________________________________________")
    }





}
@Serializable
data class Payloadk(val message: String, val tag: String , val date :  Long)

fun calcHmacSha256(secretKey: ByteArray?, message: String): ByteArray? {
    val msgB = message.toByteArray()
    var hmacSha256: ByteArray? = null
    hmacSha256 = try {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey, "HmacSHA256")
        mac.init(secretKeySpec)
        mac.doFinal(msgB)
    } catch (e: Exception) {
        throw RuntimeException("Failed to calculate hmac-sha256", e)
    }
    return hmacSha256
}
class Client(address: String, port: Int) {
    private val connection: Socket = Socket(address, port)
    private var connected: Boolean = true



    public val reader: Scanner = Scanner(connection.getInputStream())
    private val writer: OutputStream = connection.getOutputStream()



    /***
     * Takes representation of data class and  writes it to server
     */
    fun writeB(message: ByteArray) {
        writer.write((Hex.toHexString(message) + '\n').toByteArray(Charset.defaultCharset()))
    }
     fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    fun read() {
        while (connected)
            println(reader.nextLine())
    }
}