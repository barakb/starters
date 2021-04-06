package com.totango

import com.totango.features.FeatureFlag
import com.totango.features.FeatureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.lang.Thread.sleep


@SpringBootApplication
class Application

@Suppress("unused")
@Component
class Runner(private val service: FeatureService) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        service.isFeatureEnabled("880", FeatureFlag.ACCOUNT_ACTIVITY_AGG_UNESCAPED_QUOTES)
            .doOnNext {
                logger.info("1st time, feature enabled = $it")
            }.block()
        service.isFeatureEnabled("880", FeatureFlag.ACCOUNT_ACTIVITY_AGG_UNESCAPED_QUOTES)
            .doOnNext {
                logger.info("2nd time, feature enabled = $it")
            }.block()

        logger.info("calling isFeatureEnabledBlock")
        service.isFeatureEnabledBlock("880", FeatureFlag.ACCOUNT_ACTIVITY_AGG_UNESCAPED_QUOTES)

    }


    fun slowLoadBy(i: Int): Int {
        sleep(10)
        return i
    }

    fun hasLowRisk(i: Int): Boolean {
        sleep(10)
        return i % 2 == 0
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Runner::class.java)
    }
}


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
