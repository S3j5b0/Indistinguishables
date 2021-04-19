import org.bouncycastle.util.encoders.Hex
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

import java.security.SecureRandom
import java.util.*



class shamir {

    /**
     * Generate random coefficients for the polynomial.
     */
    fun genCoeff(field: BigInteger, sR: SecureRandom): BigInteger {
        val fN = field.bitLength()
        // Get a random big integer using the given random number generator and with the given bit length.
        // We use the bit length of the field to get a similar sized coefficient.
        var k = BigInteger(fN, sR)

        // The coefficient must be in (0, field).
        while (k == BigInteger.ZERO || k >= field) {
            k = BigInteger(fN, sR)
        }
        return k
    }

    /**
     * Container object for a share.
     * idx: x value
     * secret: y value
     */
    data class Share(val idx: Int, val secret: BigInteger)

    /**
     * Split a secret into numberOfPeers parts and allow thresh parties to recover the secret.
     * The calculation is done in the finite field field.
     * Returns a list of share objects (see above).
     */
    fun shamirSecretSplitter(numberOfPeers: Int, thresh: Int, field: BigInteger, secret: ByteArray): List<Share> {
        // Assume the polynomial to be like
        // a0 + a1 * x^1 + a2 * x^2 + a3 * x^3 + ... + an * x^n
        // Initialize the shares with the secret. That's a0.
        var shares = List(numberOfPeers) { Share(it, BigInteger(1, secret)) }

        val sR = SecureRandom()
        for (degree in 1 until thresh) { // degree is the exponent of the x.
            // Generate a random coefficient for the polynomial. These are the other a's.
            val coef = genCoeff(field, sR)
            shares = shares.map {
                Share(it.idx, it.secret.add(
                    coef.multiply( // a *
                        /**
                         * idx + 1
                         * We cannot use 0 (it would leak the secret),
                         * but I wanted the index shares to start with 0 (as usual in programs),
                         * hence the choice of i+1.
                         */
                        BigInteger.valueOf(it.idx+1L)
                            .pow(degree) // ^degree
                    ).mod(field) // Remember to respect the finite field.
                ).mod(field)) // Remember to respect the finite field.
            }
        }

        return shares
    }
    fun lagrangeDyna(points : List<Pair<BigDecimal, BigDecimal>>) : (BigDecimal, BigDecimal) -> BigDecimal{ //  : List<(BigDecimal) -> BigDecimal>
        fun reduceSingle( x : BigDecimal,currentIdx : Int ) : BigDecimal{
            val top = points.foldRightIndexed(1.0.toBigDecimal()) {idx, item, acc -> if (idx == currentIdx) acc else {
                acc * (x - item.first)
            }}
            val bottom = points.foldRightIndexed(1.0.toBigDecimal()){idx, item, acc -> if (idx == currentIdx) acc else {
                acc * (points[currentIdx].first - item.first)
            }}
            return top/bottom
        }


        val funcs =  points.map <Pair<BigDecimal, BigDecimal>, (BigDecimal) -> BigDecimal> { pair  -> {
                x ->
            reduceSingle(x, points.indexOf(pair)) * pair.second
        }
        }

        return { x, field ->
            val res = funcs.fold(0.0.toBigDecimal()){acc, func -> func(x) + acc}
            if (res < 0.0.toBigDecimal()){
                (res % field) + field}
            else {
                res % field
            }
        }

    }
}