import kotlinx.serialization.Serializable

@Serializable
data class Message(val message: String, val HMAC: String)