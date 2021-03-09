
//import org.bouncycastle.util.Pack
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import kotlinx.serialization.*
import kotlinx.serialization.cbor.*
import oracle.jrockit.jfr.events.ContentTypeImpl.MILLIS
import org.bouncycastle.util.encoders.Hex
import java.time.Duration
import javax.crypto.Mac;
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.crypto.spec.SecretKeySpec;
import java.time.temporal.ChronoUnit;
import javax.swing.text.StyledEditorKit


val key1 = "key".toByteArray()

fun main(args: Array<String>) {




    val server = ServerSocket(9999)
    println("Server is running on port ${server.localPort}")

    while (true) {
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")

        // Run client in it's own thread.
        thread { ClientHandler(client).run() }
    }

}


@Serializable

data class Payload(val message: String, val tag: String, val date :  Long )
class ClientHandler(client: Socket) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run() {
        running = true
        // Welcome message


        while (running) {
            if (reader.hasNext()) {
                val text = reader.nextLine()
                val inComingPayload = getPayloadFromOuput(text)


                println("plaintext received: " + inComingPayload.message)


                if (verifyDatetime(inComingPayload.date)) {
                    if (canonicalVerify(inComingPayload)) {
                        println("verify is a succes")
                        writer.write(("server approves message" + '\n').toByteArray(Charset.defaultCharset()))
                    } else {
                        println("message not accepted")
                        running = false
                    }
                } else {
                    writer.write(("you might be trying to replay messages" + '\n').toByteArray(Charset.defaultCharset()))
                }

            }
        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }



}

fun verifyDatetime(clientTimeStamp: Long) : Boolean {


    val localTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC )

    return clientTimeStamp - localTime  < 100
}



fun getPayloadFromOuput(serverIn: String) : Payload {
    val asBytes = Hex.decode(serverIn)
    return Cbor.decodeFromByteArray<Payload>(asBytes)
}

fun canonicalVerify(checkThis : Payload): Boolean {
    val checkMac = mac(key1, checkThis.message)
    return Hex.toHexString(checkMac) == checkThis.tag
}
fun mac(secretKey: ByteArray?, message: String): ByteArray? {
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