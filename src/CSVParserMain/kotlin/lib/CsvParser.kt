package hinrichs.csvparser.lib

import hinrichs.csvparser.COL_T1
import hinrichs.csvparser.COL_T2
import hinrichs.csvparser.COL_T3

const val HEADER_T1 = "t1"
const val HEADER_T2 = "t2"
const val HEADER_T3 = "t3"

sealed class Line

data class ContentLine(val content : List<String>): Line() {
    val t1 = content[COL_T1]
    val t2 = content[COL_T2]
    val t3 = content[COL_T3]
}

object EndOfFile : Line()

abstract class CsvParser(private val separator: Char) {

    fun parseLine(): Line {
        val line = getNextLine()
        if (line === null) {
            return EndOfFile
        }
        val result = mutableListOf<String>()
        val builder = StringBuilder()
        var quotes = 0
        for (ch in line) {
            when {
                ch == '\"' -> {
                    quotes++
                    builder.append(ch)
                }
                (ch == '\n') || (ch ==  '\r') -> {
                    if (quotes % 2 == 0) {
                        result.add(builder.toString())
                        builder.setLength(0)
                    }
                }
                (ch == separator) && (quotes % 2 == 0) -> {
                    result.add(builder.toString())
                    builder.setLength(0)
                }
                else -> builder.append(ch)
            }
        }
        return ContentLine(result)
    }

    protected abstract fun getNextLine(): String?
}