import org.specs2.mutable.Specification
import java.util.ArrayList
import java.util.Comparator

import scala.collection.JavaConverters._
import utils.Frequencies
import utils.Utils

class UtilsSpec extends Specification {

    """The review 'To my mind, a fine caramel should be creamy and melting. These are grainy.
      The flavor on these was fine, although a little sweet to my taste.'"""" should {
        "splitted correctly" in {

            val review = """To my mind, a fine caramel should be creamy and melting.
                           |These are grainy.
                           |The flavor on these was fine, although a little sweet to my taste.""".stripMargin.replaceAll("\n", " ")

            Utils.splitTextIntoChunks(review, 57) mustEqual (
                Seq("To my mind, a fine caramel should be creamy and melting.",
                    " These are grainy.",
                    " The flavor on these was fine, although a little sweet to my taste."))
        }
    }
}
