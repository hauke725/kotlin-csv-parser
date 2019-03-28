package hinrichs.csvparser

import hinrichs.csvparser.lib.CombinationsGenerator
import hinrichs.csvparser.lib.ContentLine
import hinrichs.csvparser.lib.EndOfFile
import hinrichs.csvparser.lib.native.CsvParser

const val COL_T1 = 0
const val COL_T2 = 1
const val COL_T3 = 2

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Aufruf: parser.kexe <file.csv> [ > out.csv ]")
        return
    }
    val fileName = args[0]
    val parser = CsvParser(fileName, ';')

    val t1List = mutableListOf<String>()
    val t2List = mutableListOf<String>()
    val t3List = mutableListOf<String>()

    loop@ while (true) {
        when (val nextLine = parser.parseLine()) {
            EndOfFile -> break@loop
            is ContentLine -> {
                val t1 = nextLine.t1
                val t2 = nextLine.t2
                val t3 = nextLine.t3

                if (t1.isNotEmpty()) {
                    t1List.add(t1)
                }
                if (t2.isNotEmpty()) {
                    t2List.add(t2)
                }
                if (t3.isNotEmpty()) {
                    t3List.add(t3)
                }
            }
        }
    }

    val generator = CombinationsGenerator(arrayOf(t1List, t2List, t3List))
    val combinations = generator.getCombinations()
    for (row in combinations) {
        println(row.joinToString(","))
    }
}
