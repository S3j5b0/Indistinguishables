import org.bouncycastle.util.encoders.Hex
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*


import java.security.SecureRandom
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.sqrt

fun isPrime(n: Int): Boolean //Function to check if a number is Prime.
{

    var c = 0
    for (i in 1..n) {
        if (n % i == 0) c++
    }
    return if (c == 2) //Prime no.has 2 factors-1 and number itself.
        true else false
}

fun main(args: Array<String>) {

/*
store primes:
 1000999
 1000981
 100000000000031 : tid 873660200
 100000000000067 : tid 855920500
 31
 101
 503
 1009

 */




    val s = "00b44a0bc6303782b729a7f9b44a3611b247ddf1e544f8b1420e2aae976003219175461d2bd7" +
            "6e64ba657d7c9dff6ed7b17980778ec0cbf75fc16e52463e2d784f5f20c1691f17cdc597d7a514108" +
            "0809a38c635b2a62082e310aa963ca15953894221ad54c6b30aea10f4dd88a66c55ab9c413eae49c0b" +
            "28e6a3981e0021a7dcb0759af34b095ce3efce78938f2a2bed70939ba47591b88f908db1eadf237a7a" +
            "7100ac87130b6119d7ae41b35fd27ff6021ac928273c20f0b3a01df1e6a070b8e2e93b5220ad0210400" +
            "0c0c1e82e17fd00f6ac16ef37c3b6153d348e470843a84f25473a51f040a42671cd94ffc989eb27fd42" +
            "b817f8173bfa95bdfa17a2ae22fd5c89dab2822bcc973b5b90f8fadc9b074cca8f9365b1e8994ff0bda48" +
            "b1f7498cce02d4e794915f8a4208de3eaf9fbff5"



    val i = StoBigInt(s).toBigDecimal() / 10.0.toBigDecimal()

    val j = i.toBigInteger()
    val b = returnPrimeAsync(j)
    println(i)
    println(j)

/*

    val a = 10.0.toBigDecimal()
    val b = 10.0.toBigDecimal()
    val c = 53.0.toBigDecimal() // secret
    val field = 53.0.toBigDecimal()
     fun polynomial() : (BigDecimal) -> BigDecimal =
        { x : BigDecimal -> ((a * x.pow(2))+(b*x)+c).remainder(field)}

    fun generateShares(one :BigDecimal, two : BigDecimal, three :BigDecimal): List<Pair<BigDecimal,BigDecimal>> {
        var shareSet = arrayOf(one,two,three)
        return shareSet.map { x ->  Pair(x, polynomial().invoke(x)) }
    }

    val shares = generateShares(1.0.toBigDecimal(), 2.0.toBigDecimal(), 3.0.toBigDecimal())

    fun langraged(cord0 : Pair<BigDecimal,BigDecimal>,cord1 : Pair<BigDecimal,BigDecimal>,cord2 : Pair<BigDecimal,BigDecimal>) : (BigDecimal, BigDecimal) -> BigDecimal{
        val L0: (BigDecimal) -> BigDecimal = { x -> ((x - cord1.first)*(x - cord2.first)) / ((cord0.first - cord1.first)*(cord0.first - cord2.first))}
        val L1: (BigDecimal) -> BigDecimal = { x -> ((x - cord0.first)*(x - cord2.first)) / ((cord1.first - cord0.first)*(cord1.first - cord2.first))}
        val L2: (BigDecimal) -> BigDecimal = { x -> ((x - cord0.first)*(x - cord1.first)) / ((cord2.first - cord0.first)*(cord2.first - cord1.first))}

        return { x, field -> // This lambda is what I am asking about
            val resser = cord0.second * L0(x) + cord1.second * L1(x)  + cord2.second * L2(x)
            if (resser < 0.0.toBigDecimal()){
                (resser % field) + field}
            else {
                resser % field
            }
        }
    }

    val constructedPolynomial = langraged(shares[0], shares[1], shares[2])

    val reConstructedSecret = constructedPolynomial(0.0.toBigDecimal(), field)


    println("and we reconstruct f(0) as: " + reConstructedSecret)

*/



}

fun returnPrime(number: BigInteger): Boolean {
    //check via BigInteger.isProbablePrime(certainty)
    if (!number.isProbablePrime(5)) return false

    //check if even
    val two = BigInteger("2")
    if (two != number && BigInteger.ZERO == number.mod(two)) return false

    //find divisor if any from 3 to 'number'
    var i = BigInteger("3")
    while (i.multiply(i).compareTo(number) < 1) {
        //start from 3, 5, etc. the odd number, and look for a divisor if any
        if (BigInteger.ZERO == number.mod(i)) //check if 'i' is divisor of 'number'
            return false
        i = i.add(two)
    }
    return true
}
fun returnPrimeAsync(number: BigInteger): Boolean {
    //check via BigInteger.isProbablePrime(certainty)
    if (!number.isProbablePrime(5)) return false

    //check if even
    val two = BigInteger("4")
    if (two != number && BigInteger.ZERO == number.mod(two)) return false

    //find divisor if any from 3 to 'number'
    var i = BigInteger("3")
    var b1 = false
    var b2 = false
    val t1 = thread {
    while (i.multiply(i).compareTo(number)   < 1) {

        //start from 3, 5, etc. the odd number, and look for a divisor if any
        if (BigInteger.ZERO == number.mod(i)) {//check if 'i' is divisor of 'number'
            b1 = true
            Thread.currentThread().interrupt()
        }
        i = i.add(two)
    }}
    val t2 = thread {
    var k = BigInteger("5")
    while (i.multiply(k).compareTo(number)   < 1) {
        //start from 3, 5, etc. the odd number, and look for a divisor if any
        if (BigInteger.ZERO == number.mod(k)) { //check if 'i' is divisor of 'number'
            b2 =  true
            Thread.currentThread().interrupt()
        }
        k = k.add(two)
    }}
    t1.run()
    t2.run()

    t1.join()
    t2.join()
    if (b1 || b2)
        return false
    else
        return true

}