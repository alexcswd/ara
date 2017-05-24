package utils

import com.qualitysoft.ReviewsAnalyzer

object ResultsPrinter {

    def print(linesNumber: Long, nsDuration: Long) {

        val topUsers = Frequencies.topFrequentUserNames(ReviewsAnalyzer.TOP_NUMBER)
        val topProducts = Frequencies.topFrequentProductNames(ReviewsAnalyzer.TOP_NUMBER)
        val topWords = Frequencies.topFrequentWords(ReviewsAnalyzer.TOP_NUMBER)
        val topUsersSize = topUsers.size
        val topProductsSize = topProducts.size
        val topWordsSize = topWords.size

        println(s"===== TOP $topUsersSize ACTIVE USERS ======")
        topUsers.foreach { println }

        println(s"===== TOP $topProductsSize COMMENTED PRODUCTS ======")
        topProducts.foreach { println }

        println(s"===== TOP $topWordsSize USED WORDS ======")
        topWords.foreach { println }

        log(topUsersSize, topProductsSize, topWordsSize, linesNumber, nsDuration)
    }

    def log(topUsersSize: Int, topProductsSize: Int, topUsedWordsSize: Int, linesNumber: Long, time: Long) {
        println(s"Handled $linesNumber lines in $time nanoseconds")
        println(s"${Frequencies.productFrequencies.size()} distinct commented products")
        println(s"${Frequencies.userFrequencies.size()} distinct commenting users")
        println(s"${Frequencies.wordFrequencies.size()} distinct words used in comments")
        println(s"${topUsersSize} of top commenting users")
        println(s"threshold for user's activity is ${Frequencies.usersActivityTh}")
        println(s"${topProductsSize} of top commented products")
        println(s"threshold for product's comments frequency is ${Frequencies.productsFrequencyTh}")
        println(s"${topUsedWordsSize} of top words used")
        println(s"threshold for word usage frequency is ${Frequencies.wordsFrequencyTh}")
    }
}