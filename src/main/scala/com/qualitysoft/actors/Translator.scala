package com.qualitysoft.actors

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.http.impl.client.HttpClientBuilder

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import utils.JsonUtil
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.entity.ContentType

class Translator(manager: ActorRef) extends Actor {

    val ENDPOINT_URL = "https://api.google.com/translate"
    val INPUT_LANG = "en"
    val OUTPUT_LANG = "fr"

    val httpClient = HttpClientBuilder.create().build();

    override def preStart = requestTextToTranslate

    def receive = {
        case Translate(text, id) => completeTranslation(text, id)
    }

    /* RMW (i.e. Receive Message Wrapper) */
    def completeTranslation(text: String, taskId: Int) = {
        manager ! Translation(translate(text), taskId)
        requestTextToTranslate
    }

    /* SMW (i.e. Send Message Wrapper) */
    def requestTextToTranslate = { manager ! RequestTextToTranslate }

    def translate(text: String) = {

        val requestBody = JsonUtil.toJson(JsonRequest(INPUT_LANG, OUTPUT_LANG, text))

        // make HTTP POST request
        val post = new HttpPost(ENDPOINT_URL)
        post.setHeader("Accept", "application/json")
        post.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON))

        // send POST request
        val response = httpClient.execute(post)

        // get the translation
        val inputStream = response.getEntity.getContent

        val responseText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        JsonUtil.fromJson[JsonResponse](responseText).text
    }

}

case class Translate(text: String, id: Int)
case class Translation(text: String, id: Int)
case object RequestTextToTranslate

case class JsonRequest(input_lang: String, output_lang: String, text: String)
case class JsonResponse(text: String)

