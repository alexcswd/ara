package com.qualitysoft

import akka.actor.ActorSystem
import akka.actor.Props
import com.qualitysoft.actors.Translator
import com.qualitysoft.actors.WorkersManager
import com.qualitysoft.actors.TranslatorsManager
import com.qualitysoft.actors.Worker

object ReviewsAnalyzer extends App {

    val DATA_FILE_PATH = "resources/Reviews.csv"
    val TOP_NUMBER = 1000

    val CHUNK_SIZE = 10000 // number of lines to handle in one go (by an actor for one work unit)

    val translate = (args.size != 0 && args(0).equals("translate=true"))

    val coresNum = Runtime.getRuntime.availableProcessors()

    val system = ActorSystem("analyzer")

    val mainManager = system.actorOf( Props( new WorkersManager(system, translate) ), "workers-manager" )
    
    val translatorsManager = system.actorOf( Props( new TranslatorsManager(mainManager) ) , "translators-manager")

    for (i <- 1 to coresNum * 3) {
        system.actorOf( Props( new Worker(mainManager, translatorsManager, translate) ), s"worker-$i" )
    }

    if (translate) {
    	for (i <- 1 to 100) {
    		system.actorOf( Props( new Translator(translatorsManager) ), s"translator-$i")
    	}
    }
}