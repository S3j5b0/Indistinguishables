class shamir {

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
}