package com.totango

import com.totango.features.FeatureFlag
import com.totango.features.FeatureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class Application {
}

@Suppress("unused")
@Component
class Runner(service: FeatureService){

    init{
        logger.info("Runner ------------- ")
        service.isFeatureEnabled("880", FeatureFlag.ACCOUNT_ACTIVITY_AGG_UNESCAPED_QUOTES)
            .doOnNext{
                logger.info("feature enabled = $it")
            }.block()
    }

    companion object{
        private val logger: Logger = LoggerFactory.getLogger(Runner::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
