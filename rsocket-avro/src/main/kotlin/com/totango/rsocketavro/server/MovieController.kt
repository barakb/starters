package com.totango.rsocketavro

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Controller
class MovieController(private val movieService: MovieService) {

    @MessageMapping("ticket.cancel")
    fun cancelTicket(request: Mono<TicketRequest>) {
        // cancel and refund asynchronously
        request
            .doOnNext { t: TicketRequest -> t.status = TicketStatus.TICKET_CANCELLED }
            .doOnNext { t: TicketRequest ->
                logger.info("cancelTicket :: ${t.requestId.toString()} : ${t.status}")
            }
            .subscribe()
    }

    @MessageMapping("ticket.purchase")
    fun purchaseTicket(request: Mono<TicketRequest>): Mono<TicketRequest>? {
        return request
            .doOnNext { t: TicketRequest -> t.status = TicketStatus.TICKET_ISSUED }
            .doOnNext { (requestId, status) ->
                logger.info("purchaseTicket :: $requestId : $status")
            }
    }

    @MessageMapping("movie.stream")
    fun playMovie(request: Mono<TicketRequest>): Flux<MovieScene> {
        return request
            .map { if (it.status == TicketStatus.TICKET_ISSUED) movieService.getScenes() else emptyList() }
            .flatMapIterable { it }
            .cast(MovieScene::class.java)
            .delayElements(Duration.ofSeconds(1))
    }

    @MessageMapping("tv.movie")
    fun playMovie(sceneIndex: Flux<Int>): Flux<MovieScene> {
        return sceneIndex
            .map { it - 1 } // list is 0 based index
            .map(movieService::getScene)
            .delayElements(Duration.ofSeconds(1))
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MovieService::class.java)
    }

}