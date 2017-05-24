package com.qualitysoft.actors

import utils.Frequencies

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala

class Worker(val workersManager: ActorRef, val translatorsManager: ActorRef, val translate: Boolean) extends Actor {

    override def preStart = registerToGetLines

    def receive = {
        case HandleLines(lines, id, last) => handleLines(lines, id, last)
    }

    // Receive Message Wrappers
    /* RMW */
    def handleLines(lines: Seq[String], id: Int, last: Boolean) {
        handle(lines)
        taskCompleted(id)
        if (!last) registerToGetLines else translationIsAboutToComplete
    }

    // Send Message Wrappers
    /* SMW */
    def registerToGetLines = { workersManager ! RequestTask }

    /* SMW */
    def taskCompleted(id: Int) = { workersManager ! TaskCompleted(id) }

    /* SMW */
    def translationIsAboutToComplete = { translatorsManager ! CompletingTranslation }

    /* SMW */
    def translate(review: Review) = { translatorsManager ! TranslationRequest(review) }

    def handle(lines: Seq[String]) = lines.foreach { handleLine }

    def handleLine(line: String) {
        val data = line.split(",")
        val (productId, userId, profileName, text) = (data(1), data(2), data(3), data(9))
        Frequencies.incrementUserActivity(s"$userId$$$profileName")
        Frequencies.incrementProductFrequency(productId)
        clean(text).split("\\W").filter(_.length > 0)
            .map(_.toLowerCase)
            .foreach { Frequencies.incrementWord }

        if (translate) translate(Review(productId, userId, text))
    }

    def clean(text: String) = text // some points to improve

}

case class HandleLines(lines: Seq[String], id: Int, last: Boolean) // lines to handle by a worker actor
case class TranslationRequest(review: Review)
case object CompletingTranslation

case class Review(productId: String, userId: String, text: String)
