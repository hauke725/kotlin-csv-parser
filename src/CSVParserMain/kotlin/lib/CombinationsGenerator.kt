package hinrichs.csvparser.lib

import hinrichs.csvparser.COL_T1
import hinrichs.csvparser.COL_T2
import hinrichs.csvparser.COL_T3


val relMap = arrayOf(
    arrayOf(COL_T1, COL_T1, COL_T1),
    arrayOf(COL_T1, COL_T1, COL_T2),
    arrayOf(COL_T1, COL_T1, COL_T3),
    arrayOf(COL_T1, COL_T2, COL_T2),
    arrayOf(COL_T1, COL_T2, COL_T3),
    arrayOf(COL_T1, COL_T3, COL_T3),
    arrayOf(COL_T2, COL_T2, COL_T2),
    arrayOf(COL_T2, COL_T2, COL_T3),
    arrayOf(COL_T2, COL_T3, COL_T3),
    arrayOf(COL_T3, COL_T3, COL_T3)
)


class CombinationsGenerator(private val input: Array<List<String>>) {

    fun getCombinations(): MutableList<Array<String>> {
        val combinations: MutableList<Array<String>> = mutableListOf()
        relMap.forEach {
            val xCol = it[0]
            val wCol = it[1]
            val yCol = it[2]
            for (x in input[xCol]) {
                for(w in input[wCol]) {
                    if (w == x) {
                        continue
                    }
                    for(y in input[yCol]) {
                        if (y == x || y == w) {
                            continue
                        }
                        combinations.add(arrayOf(x, w, y))
                    }
                }
            }
        }
        return combinations
    }
}