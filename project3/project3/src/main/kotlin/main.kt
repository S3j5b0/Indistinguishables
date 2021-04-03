import org.bouncycastle.util.encoders.Hex
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

import java.security.SecureRandom
import java.util.*

class main {


}

fun main(args: Array<String>) {


        val q = "000sa333ddd"


        val n = q.trimStart{x -> x == '0' }
        println(n)

    
    val s = "00b44a0bc6303782b729a7f9b44a3611b247ddf1e544f8b1"+
    "420e2aae976003219175461d2bd76e64ba657d7c9dff6ed7"+
            "b17980778ec0cbf75fc16e52463e2d784f5f20c1691f17cd"+
    "c597d7a5141080809a38c635b2a62082e310aa963ca15953"+
    "894221ad54c6b30aea10f4dd88a66c55ab9c413eae49c0b2"+
            "8e6a3981e0021a7dcb0759af34b095ce3efce78938f2a2be"+
            "d70939ba47591b88f908db1eadf237a7a7100ac87130b611"+
    "9d7ae41b35fd27ff6021ac928273c20f0b3a01df1e6a070b"+
            "8e2e93b5220ad02104000c0c1e82e17fd00f6ac16ef37c3b"+
            "6153d348e470843a84f25473a51f040a42671cd94ffc989e"+
            "b27fd42b817f8173bfa95bdfa17a2ae22fd5c89dab2822bc"+
    "c973b5b90f8fadc9b074cca8f9365b1e8994ff0bda48b1f7" +
    "498cce02d4e794915f8a4208de3eaf9fbff5"



    // getting an integer representation of the secret
    val S = StoBigInt(s)
    val rand : Random = Random()
    // Full disclosure: we had no idea how we should pick a field. Unless we should split our scheme into many smaller polynomials,
    // we had to pick a field larger than S. This is how we ended up doing it
    val field = (S.toBigDecimal() * (rand.nextInt(3)+1 + rand.nextDouble()).toBigDecimal()).toBigInteger()

    // I now create the scheme from our input string:
    // a and b are chosen as random values between 0-10. This is an rather arbitrary choice also, as I couldnt find anything about
    // it anywhere
    println("Secret as integer: "  + S)
    val scheme = QuadraticScheme(s, field)
    // generating my own random shares:
    println("__________________________________________")
    val shares = scheme.generateRandomShares(5, 5)
    println("Generating shares:")
    println(shares.forEach{x -> println("element:" + x)})

    println("__________________________________________")

    // Now we try to reconstruct our function

    val reconstructedPolyFunc = lagrange(shares[0], shares[1], shares[2])
    val reconstructedSecret = reconstructedPolyFunc.invoke(0.0.toBigDecimal(), field.toBigDecimal()).toBigInteger()
    println(S)
    println(reconstructedSecret)
    if (reconstructedSecret == S){
        println("reconstructing was a success!")
    } else {
        println("reconstructing was a failure, such dissapointment :( ")

    }
// Now i'll try to recreate a secret from the shares that I am given


val share_1 = "009aca2ca92b1e95bfad348c9014c6adc00d18d29fd5f891" +
    "d0837c9fe18db35cc28d654114d6159dd6664405ead5277e"+
    "24bcdbda9984c28e3b810377744f420e0fc52ada1cafb328"+
            "f6aaa9656d31c73b98af938f784d3d611e7e6f124119e948"+
    "745d15c829d794f47eb76b3fdfc16824ff6d46bcf534b1a2"+
            "d8b3f2de97250f3f3b16f87dba41d54b127c10b2b44d7d54"+
    "c00d89ce91b590c065cc210dd841c8460a7ac535fb0a6e26"+
    "b312695c2635b5a8d311fb4473bee791f35f92dc70524954"+
    "f5f60b98352e4d63b1f8c7357c1e52d696f67b2ff14a988a"+
   "1691352fa0d3401d7d4f806598b651e5e21bc133ad2340a3"+
            "27cef3d47ef2bddc386a98dabcebaa814becb09ce3d8032d"+
            "933664af6b495849030b03e6da89d971e5a45f19ecdef254"+
            "e3549ba5af53ed18b7a013c81b154a06eb76"
 val share_2 = "2131de2d5f973cd76289fe88dfe2cd9e8196b7cfb26b8793"+
            "fb3afa5bc0b965441878cf300f1a39db3525dc4881a4b465"+
    "4bf648b6b812e202d0ea3e7654fe02cadc68f72978093eee"+
           "3731ecd0ff1f7b32e52de1d9e7575315112cbe693205a089"+
            "0ae8a2fe33610e9097ce3c7f819113315686179c226df4be"+
            "a68cca4e466fcf4ca343fa60019ca4914acedc84df0e12c8"+
    "ad4a3d5590f51e321a7f3528dfb7939241e1377c632e9138"+
    "13ac02757d899a19c10900fdd4ed24d1affc0249b3fdd93d"+
            "f4de4f229dffa039af1589f5fe87cf7594cc8379f364a643"+
    "34ecc165aa650cf81edc2fd1791b95128926c9be25b94e60"+
            "fad290d3f5ef79403025d2ead3ae2f77cc054f4dbc3a5183"+
    "d31eeb8626c00512769ad03092f0b2f256fc2b2f8b62506"+
    "59e656c1569d96b164d7e0908f84e99e728"
    val share_4 = "71da02f27e8f8f82fc996469dc254f81b4db7718ca8bc4fe" +
            "86f6700424c9527db41c2403b69ea80fb2902fa720983929" +
            "691c81b88a7c5bf830b5153d749327b9a80422f2a61cd51b" +
            "5c5c7568a059ab38660f135e05ba62a5e6014d3f0ce4b052" +
            "2b81df3231b111c06199faa7cff0387981dd35cc62ea6a45" +
            "1d2c0b12b39f6676adbe82058c3cb4847856477f8f93962c" +
            "4c10fc5fb62906d95bcb7aad486b564d8c3f50cbeb2d21b6" +
            "aa0e8e46d03b9ba75b1adf4fa9d41ca32e977fbdafadaa4f" +
            "38f20020d30c5f26e30ac1ad56993bc246c06fd0bcbb12e5" +
            "40b1fa6292ba403f45f03f9cf446ac4a37bb4aa180a3f262" +
            "466d3129fccca216c1521143cabc818dda793ce26b3b700c" +
            "3b15cd648193771e797863f7782e6b460d593c92b15a90c8" +
            "43d9d09f65ff1b25cda9b758baa456fe15"

    val arg1 = Pair(2.0.toBigDecimal(), StoBigDec(share_1))
    val arg2 = Pair(3.0.toBigDecimal(), StoBigDec(share_2))
    val arg3 = Pair(5.0.toBigDecimal(), StoBigDec(share_4))

    val reconstructedFunction =
        lagrange(arg1,arg2,arg3)

    val foundSecret = reconstructedFunction(0.0.toBigDecimal(), StoBigDec(s))
    println("__________________________________________")

    println("secret reconstructed as: ")
    println(foundSecret)
    println("and as a string:")
    println(bigInttoS(foundSecret.toBigInteger()))
}





// f(x) = ax^2 + bx + c
//f(x) = 10x^2 + 20x + 21426848149916227113669602
class quadratic(val A: Int, val B: Int, val C : BigInteger, val fieldSize : BigInteger) {

    val a = A.toDouble().toBigDecimal()
    val b = B.toDouble().toBigDecimal()
    val c = C.toBigDecimal()



    override fun toString() : String{
    return this.a.toString() + "* x ^2 +" + this.b.toString() + " * x + " + this.c.toString()
    }

    private fun getter() : (BigDecimal) -> BigDecimal =
        { x : BigDecimal -> ((this.a * x.pow(2))+(this.b*x)+this.c).remainder(fieldSize.toBigDecimal())}

    fun YforX(x : Int) : BigDecimal {
        val valueeeeee = getter().invoke(x.toDouble().toBigDecimal())

        return valueeeeee
    }



}

fun StoBigInt(field: String) : BigInteger{
    val b = Hex.decode(field)
    return  BigInteger(b)
}
fun StoBigDec(field: String) : BigDecimal{
    return  StoBigInt(field).toBigDecimal()
}

fun bigInttoS(bigInt : BigInteger) : String{
    val backtoB = bigInt.toByteArray()

   return Hex.toHexString(backtoB).trimStart{x -> x == '0'}

}


class QuadraticScheme(val S : String, field : BigInteger) {

// not really sure if this is overkill, im just scared to reuse randomness
val sharesPRG = SecureRandom()
val aPRG = SecureRandom()
val bPRG = SecureRandom()
val a = aPRG.nextInt(10)
val b = bPRG.nextInt(10)
val c = StoBigInt(S)
val quadFunc = quadratic(a,b,c, field)


    fun generateRandomShares(k :Int, bound : Int ): List<Pair<BigDecimal,BigDecimal>> {
        var shareSet = mutableSetOf<Int>()
        while(shareSet.size < k ) {
            val randval = sharesPRG.nextInt(bound) + 1

            shareSet.add(randval)
        }
        return shareSet.map { x ->  Pair(x.toDouble().toBigDecimal(), quadFunc.YforX(x)) }

    }
    fun generateSpecificShares(one :Int, two : Int, three :Int): List<Pair<BigDecimal,BigDecimal>> {
        var shareSet = arrayOf(one,two,three)
        return shareSet.map { x ->  Pair(x.toDouble().toBigDecimal(), quadFunc.YforX(x)) }

    }

}

fun lagrange(cord0 : Pair<BigDecimal,BigDecimal>,cord1 : Pair<BigDecimal,BigDecimal>,cord2 : Pair<BigDecimal,BigDecimal>) : (BigDecimal, BigDecimal) -> BigDecimal{
    val L0: (BigDecimal) -> BigDecimal = { x -> ((x - cord1.first)*(x - cord2.first)) / ((cord0.first - cord1.first)*(cord0.first - cord2.first))}
    val L1: (BigDecimal) -> BigDecimal = { x -> ((x - cord0.first)*(x - cord2.first)) / ((cord1.first - cord0.first)*(cord1.first - cord2.first))}
    val L2: (BigDecimal) -> BigDecimal = { x -> ((x - cord0.first)*(x - cord1.first)) / ((cord2.first - cord0.first)*(cord2.first - cord1.first))}

    return { x, field ->
        val resser = cord0.second * L0(x) + cord1.second * L1(x)  + cord2.second * L2(x)
        if (resser < 0.0.toBigDecimal()){
            (resser % field) + field}
        else {
            resser % field
        }
    }
}


/*
fun reconstruct(pairs : List<Pair<Double, Double>>, field: Int) : Double {
    val (xs, ys) = pairs.toList().unzip()
    var res = 0.0
    for (i in ys.indices) {
        res += (ys[i] * lagrange(xs, i))
    }
    return if (res < 0.0) {
        println("res < 0.0: "  + res)
        (res % field) + field
    } else{
        println("res > 0.0: " + res)
        res % field
}}

fun lagrange(xs : List<Double>, i: Int) : Double {
    var res = 1.0
    for (j in xs.indices) {
        if (i == j) continue
        res *= (0 - xs[j]) / (xs[i] - xs[j])
    }
    return res
}*/

