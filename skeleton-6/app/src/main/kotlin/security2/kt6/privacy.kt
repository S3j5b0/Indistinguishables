package security2.kt6

import kotlin.math.pow

class Privacy {

    fun makePrivate(input : Int, e : Double, eps : Int) : Int {
        return if(Math.random() < 1/(1 + e.pow(eps))) input else 1;
    }
}