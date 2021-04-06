package com.totango.kafkarsocket

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import java.math.BigInteger
import java.util.*


data class Message(val payload: String, val at: Date = Date())
data class SubscriptionRequest(val group: String)
data class Event(val payload: String)

@Controller
class RSocketController {

    val sender = createSender()

    private fun createSender(): KafkaSender<String, String> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        val senderOptions = SenderOptions.create<String, String>(props)
        return KafkaSender.create(senderOptions)
    }


    @MessageMapping("request-response")
    fun requestResponse(message: Message): Message {
        val reply = Message("reply")
        logger.info("requestResponse: $message -> $reply")
        return reply
    }

    @MessageMapping("fire-and-forget")
    fun fireAndForget(request: Message) {
        logger.info("Received fire-and-forget request: $request")
    }

    @MessageMapping("stream")
    fun stream(request: Message): Flux<Message> {
        logger.info("Received stream request: $request")
        return fib()
            .zipWith(naturals(), { a, b -> a to b })
            .map {
                Message("${it.second}: ${it.first}")
            }.doOnCancel {
                logger.info("Received stream request: $request cancelled")
            }.log()
    }


    @MessageMapping("publish")
    fun publish(n: Int) : Int {
        logger.info("publishing: $n events")
        sender.send(Flux.range(1, n)
            .map { i: Int ->
                SenderRecord.create(
                    ProducerRecord(
                        "events",
                        "$i",
                        mapper.writeValueAsString(Event("event: $i created at ${Date()}"))
                    ), i
                )
            }
        ).doOnError { e: Any -> logger.error("Send failed", e) }
            .doOnComplete { logger.info("publishing done") }
            .subscribe()

        logger.info("sent done !")
        return n
    }


    @MessageMapping("subscribe")
    fun subscribe(subscriptionRequest: SubscriptionRequest): Flux<Event> {
        return subscribe(subscriptionRequest.group)
            .log()
    }


    private fun fib(): Flux<BigInteger> =
        Flux.generate({ BigInteger.ZERO to BigInteger.ONE }) { state, sink ->
            sink.next(state.first)
            state.second to (state.first + state.second)
        }

    private fun naturals(): Flux<Long> = Flux.generate({ 1L }) { state, sink ->
        sink.next(state)
        state + 1
    }

    private fun subscribe(group: String, topic: String = "events"): Flux<Event> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.CLIENT_ID_CONFIG] = "sample-consumer"
        props[ConsumerConfig.GROUP_ID_CONFIG] = group
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = "1"

        val receiverOptions = ReceiverOptions.create<String, String>(props)
        val options: ReceiverOptions<String, String> = receiverOptions.subscription(Collections.singleton(topic))
        return KafkaReceiver.create(options).receive()
            .doOnNext{
                it.receiverOffset().commit()
            }
            .map {
                mapper.readValue(it.value())
            }
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RSocketController::class.java)
        private val mapper = jacksonObjectMapper().registerModule(KotlinModule())
    }
}