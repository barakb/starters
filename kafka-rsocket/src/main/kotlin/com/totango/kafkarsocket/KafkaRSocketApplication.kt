package com.totango.kafkarsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaRSocketApplication

fun main(args: Array<String>) {
	runApplication<KafkaRSocketApplication>(*args)
}
