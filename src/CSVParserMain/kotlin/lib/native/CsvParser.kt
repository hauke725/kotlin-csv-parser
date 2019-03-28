package hinrichs.csvparser.lib.native

import hinrichs.csvparser.lib.*
import hinrichs.csvparser.lib.CsvParser
import kotlinx.cinterop.*
import platform.posix.*

class CsvParser(fileName : String, separator: Char) : CsvParser(separator) {

    private val fileHandle: CPointer<FILE>
    private val bufferLength : Int = 64 * 1024
    private val buffer = nativeHeap.allocArray<ByteVar>(bufferLength)

    init {
        val handle = fopen(fileName, "r")
        if (handle == null) {
            perror("cannot open input file $fileName")
            throw InvalidArgumentException("cannot open input file $fileName")
        }
        fileHandle = handle

        val headerT1: String
        val headerT2: String
        val headerT3: String
        when (val header = parseLine()) {
            EndOfFile -> {
                perror("file $fileName is empty")
                throw InvalidArgumentException("file $fileName is empty")
            }
            is ContentLine -> {
                 headerT1 = header.t1
                 headerT2 = header.t2
                 headerT3 = header.t3
            }
        }


        if (headerT1.trim() != HEADER_T1 || headerT2.trim() != HEADER_T2 || headerT3.trim() != HEADER_T3) {
            val message = "Kopfzeile muss t1, t2, t3 in der Reihenfolge enthalten, gegeben: $headerT1, $headerT2, $headerT3"
            perror(message)
            throw InvalidArgumentException(message)
        }
    }

    override fun getNextLine(): String? {
        return fgets(buffer, bufferLength, fileHandle)?.toKString()
    }

}