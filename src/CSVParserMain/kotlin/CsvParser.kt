/*
 * Copyright 2010-2019 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.csvparser

import kotlinx.cinterop.*
import platform.posix.*

const val COL_T1 = 0
const val COL_T2 = 1
const val COL_T3 = 2

const val HEADER_T1 = "t1"
const val HEADER_T2 = "t2"
const val HEADER_T3 = "t3"

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

fun parseLine(line: String, separator: Char) : List<String> {
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
    return result
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Aufruf: parser.kexe <file.csv> [ > out.csv ]")
        return
    }
    val fileName = args[0]

    val file = fopen(fileName, "r")
    if (file == null) {
        perror("cannot open input file $fileName")
        return
    }

    val t1List = mutableListOf<String>()
    val t2List = mutableListOf<String>()
    val t3List = mutableListOf<String>()

    try {
        memScoped {
            val bufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(bufferLength)

            val headerLine = fgets(buffer, bufferLength, file)?.toKString()
            if (headerLine == null || headerLine.isEmpty()) {
                perror("Datei ist leer‽")
                return
            }

            val header = parseLine(headerLine, ';')
            val headerT1 = header[COL_T1]
            val headerT2 = header[COL_T2]
            val headerT3 = header[COL_T3]

            if (headerT1.trim() != HEADER_T1 || headerT2.trim() != HEADER_T2 || headerT3.trim() != HEADER_T3) {
//                perror("Kopfzeile muss t1, t2, t3 in der Reihenfolge enthalten, gegeben: $headerT1, $headerT2, $headerT3")
//                return
            }

            while (true) {
                val nextLine = fgets(buffer, bufferLength, file)?.toKString()
                if (nextLine == null || nextLine.isEmpty()) break

                val records = parseLine(nextLine, ';')
                val t1 = records[COL_T1]
                val t2 = records[COL_T2]
                val t3 = records[COL_T3]
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
    } finally {
        fclose(file)
    }

    val combinations = getCombinations(arrayOf(t1List, t2List, t3List))
    for (row in combinations) {
        println(row.joinToString(","))
    }

}

fun getCombinations(input: Array<List<String>>): MutableList<Array<String>> {
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
