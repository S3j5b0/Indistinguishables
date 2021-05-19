package security2.kt6

import java.math.BigInteger

import java.security.SecureRandom
import java.util.*


class Shamir {

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

    fun genField(): BigInteger {
        println("Editors note, if taking to long, comment out line 30 and comment back in line 31 to 33 in Shamir.kt")
        val rng =  Random(92)
        return BigInteger(10000, 1, rng)
        //return BigInteger(
        //    "14153592185032072044983875333986521895483182934195151005015591460336503793554673726751284182268116980536527510316936909081979879221269444708115759866000714600311181445468222279109336585086539330899230822616363464389290685508288362640091111393917536539929928506593344762901654467551114247439388262734984366977669817479190397082953890516426984443597768434842333150063342892541913642305931344037525085878684507569078833491664085479989674908120023739182502462637044883387425625895089265505529660983121475176553964800193934618683811774382772953931920215651603691788069592378297142070033060874473778505709070663106416477222317425939277394375722876882305863467726583186810499387406752350936100169485124420739474380561665620668920263055929475872139359715895693988142599452485579083667015539523312770115230579612915095105519018579139163897596607210944936191794030190392874097873502638425846963090588916412892137490250713100123224742965673037792338308119752401021323838567204438632248074986868259131737463826568260019960267436566393834209548305402657535705797800295995387398499104306654445070869536859219148992396321575139687908819477111453380839868892441420515655909756167496159081070049958605346886074539619821003316206531792307175968176138550702743383690840239962629390099735541151489219886090418877307038553235198378253608025628513379652655427138440791117745667738147618229952005297743864877219393203691919974057945994630134857321492575443897184776589320002042074875327947343826014743818426082803350706788599980798991331726317380003655262741653920484036993922693071007476642322497523463927276125219839357541862362281547601568913050650034072762688373091844869120444908054408631304460872793144309451141711211344729083518671824276198346554668202195942589831276815256889260252176175154719021223028592101942481001578835842167906266369240860219208536775422457510802554736645291318535205165908796498777002213488563960183587837648504472679455497717294423376174462636091096395981505517234549600995451325476031329329908630794577223172753322025703066852975785887829291488909757396762329519279695791576674061331952507850501824541700919345962364494768095503866655685435359152892125126126089269471893629126243312219877890692193901420546063808269180362619959244756878914882926923025401112320212486401203779338298996486461421559999992604690026857698135060064281725217549451024303646387514189620034591662590370380756548265796263359711128118263355988465354265971599967853072799232428000320345339202550778933250489184847470765266974411862202520671989317197064314447503102591404009134197588962382444536798699725596017164370372106936962179931322947409626225683011161490970744610174739663905927157937184487474413926813209789977141947135372156568341204362145001962293828476148035799334302455602412006338684213712949149529605103072342912984548392502827307675839451915163426114549086093077656619375636660027075750973552984355570181321481539968397395603972440158057495774371871728975932899251358959958139408599163671217308990866100463593627717"
        //)
    }

    /**
     * Container object for a share.
     * idx: x value
     * secret: y value
     */
    //data class Share(val idx: Int, val secret: BigInteger)

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

    /**
     * Takes a finite field and a list of share objects.
     * Calculates the secret based on the shares.
     * I do not check whether there are enough shares to recover the secret.
     * You should only supply as many as needed.
     */
    fun shamirRecoverSecret(field: BigInteger, shares: List<Share>): BigInteger {
        var wV = BigInteger.ZERO
        shares.forEach {
            var num = BigInteger.ONE //Initialize the numerator to the neutral element (1).
            var denom = BigInteger.ONE //Initialize the numerator to the neutral element (1).
            // Skip the case when i == j using filter and then…
            shares.filter { j -> j.idx != it.idx }.forEach { j ->
                // calculate the numerator
                num = num.multiply(
                    BigInteger.ZERO
                        // Remember that I chose idx+1 for the x values.
                        .subtract(BigInteger.valueOf(j.idx+1L))
                        .mod(field) // Respect the finite field.
                ).mod(field) // Respect the finite field.
                // calculate the denominator
                denom = denom.multiply(
                    // Remember that I chose idx+1 for the x values.
                    BigInteger.valueOf(it.idx+1L)
                        // Remember that I chose idx+1 for the x values.
                        .subtract(BigInteger.valueOf(j.idx+1L))
                        .mod(field) // Respect the finite field.
                ).mod(field) // Respect the finite field.
            }
            // Division in a finite field is the same as multiplication with the inverse element
            val weight = num.multiply(denom.modInverse(field)).mod(field)
            // The weight is the corresponding delta term.
            // Finally, add everything together.
            wV = wV.add(
                weight
                    .multiply(it.secret) // Respect the finite field.
                    .mod(field) // Respect the finite field.
            ).mod(field)
        }
        return wV
    }

    fun lagrangeDyna(points : List<Pair<BigInteger, BigInteger>>) : (BigInteger, BigInteger) -> BigInteger{ //  : List<(BigDecimal) -> BigDecimal>
        fun reduceSingle( x : BigInteger,currentIdx : Int ) : BigInteger{
            val top = points.foldRightIndexed(1.toBigInteger()) {idx, item, acc -> if (idx == currentIdx) acc else {
                acc * (x - item.first)
            }}
            val bottom = points.foldRightIndexed(1.toBigInteger()){idx, item, acc -> if (idx == currentIdx) acc else {
                acc * (points[currentIdx].first - item.first)
            }}
            return top/bottom
        }


        val funcs =  points.map <Pair<BigInteger, BigInteger>, (BigInteger) -> BigInteger> { pair  -> {
                x ->
            reduceSingle(x, points.indexOf(pair)) * pair.second
        }
        }

        return { x, field ->
            val res = funcs.fold(0.toBigInteger()){acc, func -> func(x) + acc}
            if (res < 0.toBigInteger()){
                (res % field) + field}
            else {
                res % field
            }
        }

    }
}