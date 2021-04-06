package com.totango.rsocketavro

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.function.Function


@Service
class MovieService {
    private val scenes: List<MovieScene> = listOf(
        MovieScene(1, "Scene 1"),
        MovieScene(2, "Scene 2"),
        MovieScene(3, "Scene 3"),
        MovieScene(4, "Scene 4"),
        MovieScene(5, "Scene 5")
    )

    fun getScenes(): List<MovieScene> {
        return scenes
    }

    fun getScene(index: Int): MovieScene {
        return scenes[index]
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MovieService::class.java)
    }

}