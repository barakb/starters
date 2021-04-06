package com.totango.rsocketavro


import com.github.avrokotlin.avro4k.Avro
import com.totango.rsocketavro.model.MovieScene
import com.totango.rsocketavro.model.TicketRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@SpringBootApplication
class RSocketAvroApplication

fun main(args: Array<String>) {
    runApplication<RSocketAvroApplication>(*args)
}

// https://github.com/avro-kotlin/avro4k
// https://blog.ippon.tech/kafka-tutorial-4-avro-and-schema-registry

@Suppress("unused")
@Component
class Runner(private var rSocketRequester: RSocketRequester) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        logger.info("running app")
        val sp = StockPurchase()
        val schema = Avro.default.schema(StockPurchase.serializer())
        logger.info("schema: ${schema.toString(true)}")

        val avroRecord = Avro.default.toRecord(StockPurchase.serializer(), sp)
        logger.info("avroRecord $avroRecord")

        val sp1 = Avro.default.fromRecord(StockPurchase.serializer(), avroRecord)
        logger.info("sp1 $sp1 , sp1==sp ${sp1 == sp}")


//        val message = Message("goods")
//        val messageBytes = writeData(message, Message.serializer())
//        val genericRecode = readData(messageBytes, Message.serializer())
//        genericRecode.put("desc",genericRecode["payload"])
//        val message1 = Avro.default.fromRecord(Message1.serializer(), genericRecode)
//        logger.info("message1: $message1")

        playMovie()
            .take(15)
            .last()
            .flatMap {
                ticketCancel(it.second.requestId)
            }
            .subscribe()
    }


    fun ticketCancel(requestId: UUID = UUID.randomUUID()) =
        rSocketRequester
            .route("ticket.cancel")
            .data(TicketRequest(requestId))
            .send()

    fun ticketPurchase() =
        rSocketRequester
            .route("ticket.purchase")
            .data(TicketRequest(UUID.randomUUID()))
            .retrieveMono(TicketRequest::class.java)
            .doOnNext { logger.info("${it.requestId} : ${it.status}") }

    fun playMovie(): Flux<Pair<MovieScene, TicketRequest>> {
        val ticket: Mono<TicketRequest> =
            ticketPurchase().cache()

        return ticket.flatMapMany { ticketRequest ->
            rSocketRequester
                .route("movie.stream")
                .data(ticketRequest)
                .retrieveFlux(MovieScene::class.java)
                .zipWith(ticket.repeat()) { a, b -> a to b }
        }.doOnNext {
            logger.info("Playing : ${it.first.sceneDescription}")
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Runner::class.java)
    }
}

