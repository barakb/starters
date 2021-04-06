package com.totango.rsocketavro.server

import com.totango.rsocketavro.model.MovieScene
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class MovieService {
    private val scenes: List<MovieScene> =
        MutableList(99) {MovieScene(it + 1, "Scene ${it + 1}") }.toList()

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