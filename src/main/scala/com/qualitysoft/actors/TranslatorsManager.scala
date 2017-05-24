package com.qualitysoft.actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala

import scala.collection.mutable.Map

import utils.Utils

class TranslatorsManager(val mainManager: ActorRef) extends Actor {

    val TRANSLATED_REVIEWS_FILE_NAME = "resources/Reviews_translated.csv"
    val TRANSLATED_REVIEWS_FILE_HEADERS = """"productId","userId","text""""

    val MESSAGE_SIZE = 1000
    val REVIEWS_SEPARATOR = "==="

    var translators = List.empty[ActorRef]
    var pendingTranslations = Set.empty[Int]

    val taskId2reviewsMap = Map[Int, List[Review]]().empty

    var ready2translate = List.empty[List[Review]]

    var reviewsBundle = List.empty[Review]
    var messageSize = 0
    var taskId = 0
    var csvFileInited = false

    var translatedReviews = List.empty[Review]

    var completingTranslation = false

    def receive = {
        case TranslationRequest(review: Review) => reviewToTranslate(review)
        case RequestTextToTranslate             => assignTask(sender())
        case Translation(text, taskId)          => translated(text, taskId)
        case CompletingTranslation              => completingTranslation = true
    }

    // Receive Message Wrappers

    // will buffer the translations request till the joint message is about 1000 chars
    /* RMW */
    def reviewToTranslate(review: Review) {
        val text = review.text
        val reviewSize = text.codePointCount(0, text.length) + 3 // plus separator's size

        if (reviewSize > MESSAGE_SIZE) { // handle large review

            // split the review's text into 1000-char message chunks
            val chunks = Utils.splitTextIntoChunks(text, MESSAGE_SIZE)
            for (i <- 0 until (chunks.size - 1)) {
                reviewsBundle = List(Review(review.productId, review.userId, chunks(i)))
                ready2translate = reviewsBundle :: ready2translate
            }
            reviewsBundle = List(Review(review.productId, review.userId, chunks(chunks.size - 1)))

        } else if (messageSize + reviewSize > MESSAGE_SIZE) { // handle the ready to translate bundle  

            ready2translate = reviewsBundle :: ready2translate
            reviewsBundle = List(review)

        } else { // append to the reviews bundle
            messageSize += reviewSize
            reviewsBundle = review :: reviewsBundle
        }
    }

    /* RMW & SMW */
    def assignTask(translator: ActorRef) = { addTranslator(translator); sendTranslationRequest }

    // will reconstruct Review objects from translated message and flush them regularly
    /* RMW  & SMW */
    def translated(text: String, taskId: Int) {
        val reviews = taskId2reviewsMap.get(taskId).get
        val reviewsTexts = text.split(REVIEWS_SEPARATOR)
        var idx = 0
        for (review <- reviews) {
            translatedReviews = Review(review.productId, review.userId, reviewsTexts(idx)) :: translatedReviews
            idx += 1
        }
        if (translatedReviews.size > 1000) {
            if (!csvFileInited) {
                Utils.prepareCsvFile(TRANSLATED_REVIEWS_FILE_NAME, TRANSLATED_REVIEWS_FILE_HEADERS)
                csvFileInited = true
            }
            Utils.flushToCsvFile(translatedReviews, TRANSLATED_REVIEWS_FILE_NAME)
            translatedReviews = List.empty[Review]
        }
        pendingTranslations = pendingTranslations - taskId
        if (completingTranslation && pendingTranslations.isEmpty) {
            mainManager ! TranslationCompleted
            Utils.flushToCsvFile(translatedReviews, TRANSLATED_REVIEWS_FILE_NAME)
        }
    }

    // Send Message Wrappers

    /* SMW */
    def sendTranslationRequest {
        if (!translators.isEmpty) {
            if ((hasAnything2Translate && completingTranslation) || msgIsReady) {
                translators.head ! Translate(getMessage2Translate(msgIsReady), taskId)
                translators = translators.tail
                pendingTranslations = pendingTranslations + taskId
                taskId += 1
            }
        }
    }

    def addTranslator(translator: ActorRef) = { translators = translator :: translators }

    def translationCompleted = pendingTranslations.isEmpty

    def getMessage2Translate(fromBuffer: Boolean) = {
        var reviews: List[Review] = null
        if (fromBuffer) {
            reviews = ready2translate.head
            ready2translate = ready2translate.tail
        } else {
            reviews = reviewsBundle
        }
        val message = new StringBuilder
        for (review <- reviews) {
            message.append(review.text).append(REVIEWS_SEPARATOR)
        }
        taskId2reviewsMap(taskId) = reviews
        message.toString
    }

    def hasAnything2Translate = !reviewsBundle.isEmpty

    def msgIsReady() = !ready2translate.isEmpty
}
