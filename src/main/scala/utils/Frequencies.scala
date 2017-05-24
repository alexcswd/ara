package utils

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Map
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.LongAdder
import java.util.function.{ Function ⇒ JFunction }
import scala.collection.JavaConverters._

object Frequencies {

    val userFrequencies = new ConcurrentHashMap[String, LongAdder]
    val productFrequencies = new ConcurrentHashMap[String, LongAdder]
    val wordFrequencies = new ConcurrentHashMap[String, LongAdder]

    val thresholds = new Array[Int](3)

    val TOP_USERS_TH_INDEX = 0
    val TOP_PRODUCTS_TH_INDEX = 1
    val TOP_WORDS_TH_INDEX = 2

    // to make Java 8 Function resemble a Scala's closure
    implicit def toJavaFunction[A, B](f: Function1[A, B]) = new JFunction[A, B] {
        override def apply(a: A): B = f(a)
    }
    
    val initWithLongAdder = { userId: String => new LongAdder }

    def incrementUserActivity(userId: String) {
        userFrequencies.computeIfAbsent( userId, initWithLongAdder ).increment()
    }

    def incrementProductFrequency(productId: String) {
        productFrequencies.computeIfAbsent( productId, initWithLongAdder ).increment()
    }

    def incrementWord(word: String) {
        wordFrequencies.computeIfAbsent( word, initWithLongAdder ).increment()
    }

    def topFrequentUserNames(max: Int) =
        topFrequentNames(userFrequencies, max, TOP_USERS_TH_INDEX)
            .map { _.getKey.split("\\$")(1) }.toSeq.sorted

    def topFrequentProductNames(max: Int) =
        topFrequentNames(productFrequencies, max, TOP_PRODUCTS_TH_INDEX)
            .map { _.getKey }.toSeq.sorted

    def topFrequentWords(max: Int) =
        topFrequentNames(wordFrequencies, max, TOP_WORDS_TH_INDEX)
            .map { _.getKey }.toSeq.sorted

    def topFrequentNames(map: Map[String, LongAdder], max: Int, thIndex: Int) = {
        val freqs = new ArrayList[LongAdder](map.values())
        val comparator = new Comparator[LongAdder] {
            def compare(first: LongAdder, second: LongAdder) = {
                first.intValue - second.intValue
            }
        }
        val threshold = getThreshold(freqs, max, comparator).intValue()
        thresholds(thIndex) = threshold
        map.entrySet().asScala.filter { entry => entry.getValue.intValue() >= threshold }
    }

    def getThreshold[T](freqs: java.util.List[T], top: Int, comparator: Comparator[T]) = {
        Collections.sort(freqs, comparator.reversed()) // reversed to natural order
        freqs.get(top - 1)
    }

    def usersActivityTh = thresholds(TOP_USERS_TH_INDEX)

    def productsFrequencyTh = thresholds(TOP_PRODUCTS_TH_INDEX)

    def wordsFrequencyTh = thresholds(TOP_WORDS_TH_INDEX)
}