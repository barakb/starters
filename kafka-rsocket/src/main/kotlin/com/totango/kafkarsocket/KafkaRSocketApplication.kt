package com.totango.kafkarsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaRsocketApplication

fun main(args: Array<String>) {
	runApplication<KafkaRsocketApplication>(*args)
}
