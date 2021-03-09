import org.bouncycastle.crypto.*
import org.bouncycastle.crypto.internal.Digest
import javax.crypto.Mac;
import org.bouncycastle.crypto.internal.macs.HMac
import org.bouncycastle.jcajce.util.MessageDigestUtils
import java.security.MessageDigest

fun createHMAC(mesage: String, key: Int){
    var a = Mac.getInstance("HmacSHA256", "BCFIPS")
}