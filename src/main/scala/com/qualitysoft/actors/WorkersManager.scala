package com.qualitysoft.actors

import scala.io.Source

import com.qualitysoft.ReviewsAnalyzer

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.actorRef2Scala
import utils.ResultsPrinter

class WorkersManager(val system: ActorSystem, val translate: Boolean) extends Actor {

    var workers = List.empty[ActorRef]
    var pendingUpdates = Set.empty[Int]

    val start = System.nanoTime()
    val lines = Source.fromFile(ReviewsAnalyzer.DATA_FILE_PATH).getLines.drop(1) // drop the header

    var chunkId = 1
    var linesNumber = 0
    var eof = false
    var translationCompleted = !translate

    def receive = {
        case RequestTask           => assignTask(sender())
        case TaskCompleted(taskId) => taskCompleted(taskId)
        case TranslationCompleted  => translationCompleted = true
    }

    // Receive Message Wrappers
    /* RMW */
    def assignTask(worker: ActorRef) { addWorker(worker); sendLines }

    /* RMW */
    def taskCompleted(taskId: Int) { pendingUpdates = pendingUpdates - taskId; checkFullComplete }

    def addWorker(worker: ActorRef) = { workers = worker :: workers }

    def checkFullComplete {
        if (workCompleted) {
            system.terminate()
            ResultsPrinter.print(linesNumber, (System.nanoTime() - start))
        }
    }

    def workCompleted = eof && pendingUpdates.isEmpty && translationCompleted

    def sendLines() {
        if (!workers.isEmpty && !eof) {
            val chunkLines = lines.take(ReviewsAnalyzer.CHUNK_SIZE).toSeq
            eof = chunkLines.size != ReviewsAnalyzer.CHUNK_SIZE
            linesNumber += chunkLines.size
            workers.head ! HandleLines(chunkLines, chunkId, eof)
            workers = workers.tail
            pendingUpdates = pendingUpdates + chunkId
            chunkId += 1
        }
    }
}

case object RequestTask // request for lines to process from a worker actor 
case class TaskCompleted(id: Int) // a data chunk handling is completed
case object TranslationCompleted // translation completion signal

