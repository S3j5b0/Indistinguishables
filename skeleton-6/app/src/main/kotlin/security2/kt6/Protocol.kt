package security2.kt6

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import org.bouncycastle.util.encoders.Hex
import java.lang.Exception
import java.math.BigInteger
import java.net.Socket

private val shamir : Shamir = Shamir();
private val rsa : RSA = RSA();

/*
Every communication between clients and servers will be wrapped
in these Message objects.
 */
@Serializable
data class Message(
    val type: String,
    val data: ByteArray?,
    val idx: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (type != other.type) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (idx != other.idx) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + (idx ?: 0)
        return result
    }
}

/*
Following flows/messages exist:
1. Users request public key from admin server.(survey participants)
2. Trustees request a share of the private key from the admin server.
3. Users submit encrypted and privacy-protected answers to trustee server.
4. Trustees send their shares to trustee server.
5. Last trustee gets the decrypted results of the privacy-protected survey.
 */

@ExperimentalSerializationApi
class UserProtocol {
    fun sendCipherText(sock: Socket, data: ByteArray) {
        val out = sock.getOutputStream().bufferedWriter()
        out.write(Cbor.encodeToHexString(Message("sendCipherText", data, null)))
        out.newLine()
        out.flush()
        out.close()
    }
    fun getPublicKey(sock: Socket): Message {
        val out = sock.getOutputStream().bufferedWriter()
        out.write(Cbor.encodeToHexString(Message("getPublicKey", null, null)))
        out.newLine()
        out.flush()
        println("All written")
        val inp = sock.getInputStream().bufferedReader()
        println("Waiting")
        val bytes = inp.readLine()
        out.close()
        inp.close()
        println("Get public key")
        return Cbor.decodeFromHexString(bytes)
    }
}

@ExperimentalSerializationApi
class TrusteeClientProtocol {
    fun getShare(sock: Socket, idx: Int): Message {
        val out = sock.getOutputStream().bufferedWriter()
        out.write(Cbor.encodeToHexString(Message("getShare", ByteArray(1) { idx.toByte() }, null)))
        out.newLine()
        out.flush()
        val inp = sock.getInputStream().bufferedReader()
        val bytes = inp.readLine()
        out.close()
        inp.close()
        return Cbor.decodeFromHexString(bytes)
    }
    fun sendShare(sock: Socket, idx: Int, secret: ByteArray) {
        val out = sock.getOutputStream().bufferedWriter()
        out.write(
            Cbor.encodeToHexString(
                Message(
                    "sendShare",
                    secret,
                    idx
                )
            )
        )
        out.newLine()
        out.flush()
        val inp = sock.getInputStream().bufferedReader()
        val bytes = inp.readLine()
        out.close()
        inp.close()
        println(bytes)
    }
}

@ExperimentalSerializationApi
class TrusteeProtocol {
    private val shares = mutableListOf<Share>()
    private val commits = mutableListOf<ByteArray>()
    private fun newCipherText(data: ByteArray?) {
        println("Got: ${Hex.toHexString(data)}")
        commits.add(data ?: ByteArray(0))
    }
    fun getField(sock: Socket): Message {
        val out = sock.getOutputStream().bufferedWriter()
        out.write(Cbor.encodeToHexString(Message("getField", null, null)))
        out.newLine()
        out.flush()
        val inp = sock.getInputStream().bufferedReader()
        val bytes = inp.readLine()
        out.close()
        inp.close()
        println("Got field")
        return Cbor.decodeFromHexString(bytes)
    }
    private fun readShare(sock: Socket, data: ByteArray?, idx: Int?, field: BigInteger) {
        val dB = data ?: ByteArray(0)
        val iX = idx ?: 0
        shares.add(Share(iX, BigInteger(dB)))
        println(shares)

        println("Trying to decrypt")
        shares.forEach { println("With index: ${it.idx}, secret: ${it.secret}") }
        // TODO: Try to recover the secret from the shares
        try {
            val res: BigInteger = shamir.shamirRecoverSecret(field, shares);
            println("shamir result: " + Hex.toHexString(res.toByteArray()))
            // If successfull try to decrypt the users' answers
            val toSend = decryptAndEvaluate(res.toByteArray())
            val out = sock.getOutputStream().bufferedWriter()
            out.write(toSend.joinToString(","))
            out.newLine()
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun decryptAndEvaluate(key: ByteArray): List<Int> {
        // TODO: Use the private key to RSA-decrypt the users' answers and return a list of integers back
        // Remember these Ints are still privacy-protected by the randomized response scheme.
        // You can find the encrypted answers in the list “commits”.

        try {
            val privateKey = rsa.recoverPrivateKey(key);
            return  commits.map{ String(rsa.decrypt(it, privateKey)).toInt() }
        } catch (e: java.security.spec.InvalidKeySpecException){

            println("error getting privkeyu")
        }

        return listOf<Int>()
    }
    fun handleMessage(sock: Socket, field: BigInteger) {
        val inp = sock.getInputStream().bufferedReader()
        val bytes = inp.readLine()
        val msg = Cbor.decodeFromHexString<Message>(bytes)
        when (msg.type) {
            "sendCipherText" -> newCipherText(msg.data)
            "sendShare" -> readShare(sock, msg.data, msg.idx, field)
            else -> println("Received unknown message.")
        }
        sock.close()
    }
}

@ExperimentalSerializationApi
class AdminProtocol(
    private val pubKey: ByteArray,
    private val shares: List<Share>,
    private val field: BigInteger
) {
    private fun sendPublicKey(sock: Socket) {
        val out = sock.getOutputStream().bufferedWriter()
        println("Sending public key")
        out.write(Cbor.encodeToHexString(Message("publicKey", pubKey, null)))
        out.newLine()
        out.flush()
    }
    private fun sendShare(sock: Socket, data: ByteArray?) {
        val out = sock.getOutputStream().bufferedWriter()
        val i = data?.get(0)?.toInt() ?: 0
        println("Sending share $i")
        out.write(Cbor.encodeToHexString(Message("share", shares[i].secret.toByteArray(), null)))
        out.newLine()
        out.flush()
    }
    private fun sendField(sock: Socket) {
        val out = sock.getOutputStream().bufferedWriter()
        println("Sending field")
        out.write(Cbor.encodeToHexString(Message("field", field.toByteArray(), null)))
        out.newLine()
        out.flush()
    }
    fun handleMessage(sock: Socket) {
        val inp = sock.getInputStream().bufferedReader()
        val bytes = inp.readLine()
        val msg = Cbor.decodeFromHexString<Message>(bytes)
        when (msg.type) {
            "getPublicKey" -> sendPublicKey(sock)
            "getShare" -> sendShare(sock, msg.data)
            "getField" -> sendField(sock)
            else -> println("Received unknown message.")
        }
        sock.close()
    }
}
