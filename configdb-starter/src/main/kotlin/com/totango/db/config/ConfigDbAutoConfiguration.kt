package com.totango.db.config

import io.r2dbc.spi.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder
import org.springframework.boot.autoconfigure.r2dbc.EmbeddedDatabaseConnection
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.MySqlDialect
import org.springframework.r2dbc.core.DatabaseClient


@Suppress("unused")
@Configuration
@AutoConfigureBefore(
    R2dbcAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    R2dbcRepositoriesAutoConfiguration::class
)
class ConfigDbAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "r2dbc.config.db")
    @Qualifier("config")
    fun properties(): R2dbcProperties {
        return R2dbcProperties()
    }

    @Bean
    @Qualifier("config")
    fun connectionFactory(@Qualifier("config") r2dbcProperties: R2dbcProperties): ConnectionFactory {
        return ConnectionFactoryBuilder.of(r2dbcProperties) { EmbeddedDatabaseConnection.NONE }.build()
    }

    @Bean
    @Qualifier("config")
    fun configEntityTemplate(
        @Qualifier("config") connectionFactory: ConnectionFactory,
        @Qualifier("config") databaseClient: DatabaseClient
    ): R2dbcEntityOperations {
        val strategy = DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE)
        return R2dbcEntityTemplate(databaseClient, strategy)
    }

    @Bean
    @Qualifier("config")
    fun configureDatabaseClient(@Qualifier("config") connectionFactory: ConnectionFactory): DatabaseClient {
        return DatabaseClient.builder()
            .connectionFactory(connectionFactory)
            .build()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConfigDbAutoConfiguration::class.java)
    }
}