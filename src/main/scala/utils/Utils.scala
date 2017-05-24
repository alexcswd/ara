package utils

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import com.github.marklister.collections._
import com.github.marklister.collections.io._
import com.qualitysoft.actors.Review
import scala.collection.Seq
import java.lang.Math._

object Utils {

    def splitTextIntoChunks(text: String, length: Int) = {
        var splitted = Seq[String]()
        val size = text.codePointCount(0, text.length)
        if (size < length) {
            splitted = splitted :+ text
        } else {
            var startIndex = 0
            var curStr = text.substring(startIndex, startIndex + length)
            var fullPointIndex = curStr.lastIndexOf(".")
            while(fullPointIndex != -1) {
                val chunk = text.substring(startIndex, startIndex+fullPointIndex+1)
                println(chunk)
                splitted = splitted :+ chunk
                startIndex += fullPointIndex+1
                curStr = text.substring(startIndex, min(startIndex + length, text.length))
                fullPointIndex = curStr.lastIndexOf(".")
            }
            splitted = splitted :+ text.substring(startIndex)
        }
        splitted
    }
    
    def prepareCsvFile(filename: String, headers: String) {
        val bw = new PrintWriter(new BufferedWriter(new FileWriter(filename, false)))
        bw.write(headers)
        bw.write("\n")
        bw.close()
    }
    
    def flushToCsvFile(seq: Seq[Review], filename: String) {
        val bw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))
        bw.write("\n")
        bw.write(seq.csvIterator.mkString("\n"))
        bw.close()
        
    }
}