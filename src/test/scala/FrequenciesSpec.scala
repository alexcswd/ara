import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import java.util.Map
import java.util.concurrent.atomic.LongAdder
import java.util.function.{ Function ⇒ JFunction }

import scala.collection.JavaConverters.seqAsJavaListConverter

import org.specs2.mutable.Specification

import utils.Frequencies

class FrequenciesSpec extends Specification {

    "Threshold'" should {
        "be calculated properly" in {
            Frequencies.getThreshold(getNumbers, 2, getComparator) mustEqual (11)
        }
    }

    "Top 5 frequent users from presented frequency map" should {
        "should be obtained properly and ordered alphabetically" in {
            Frequencies.topFrequentNames(getUsersMap, 3, Frequencies.TOP_USERS_TH_INDEX)
                .map { _.getKey }.toSeq.sorted mustEqual (Seq("bob", "jerry", "mary"))
        }
    }

    // to make Java 8 Function resemble a Scala's closure
    implicit def toJavaFunction[A, B](f: Function1[A, B]) = new JFunction[A, B] {
        override def apply(a: A): B = f(a)
    }

    def getNumbers() = {
        new ArrayList(List(1, 3, 5, 6, 12, 11).asJava)
    }

    def getComparator() = {
        new Comparator[Int] {
            def compare(a: Int, b: Int) = {
                a - b
            }
        }
    }

    def getUsersMap() = {
        val usersFrequencyMap: Map[String, LongAdder] = new HashMap[String, LongAdder]
        usersFrequencyMap.computeIfAbsent("cory", initLongAdder)
        usersFrequencyMap.get("cory").add(1)
        usersFrequencyMap.computeIfAbsent("peter", initLongAdder)
        usersFrequencyMap.get("peter").add(2)
        usersFrequencyMap.computeIfAbsent("bob", initLongAdder)
        usersFrequencyMap.get("bob").add(4)
        usersFrequencyMap.computeIfAbsent("mary", initLongAdder)
        usersFrequencyMap.get("mary").add(3)
        usersFrequencyMap.computeIfAbsent("jerry", initLongAdder)
        usersFrequencyMap.get("jerry").add(5)
        usersFrequencyMap
    }

    val initLongAdder = { userId: String => new LongAdder }
}