@file:Suppress("unused")

package com.totango

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext
import org.springframework.web.reactive.function.client.ExchangeStrategies

// https://paolo-dedominicis96.medium.com/reactive-spring-data-elasticsearch-with-spring-boot-dbcfdc9edb3d
// https://piotrminkowski.com/2019/10/25/reactive-elasticsearch-with-spring-boot/
// https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.reactive.repositories
// https://github.com/spring-projects/spring-data-elasticsearch/blob/master/src/main/asciidoc/reference/reactive-elasticsearch-repositories.adoc
// https://dzone.com/articles/spring-boot-2-fluxes-from-elasticsearch-to-control


// *** https://www.nurkiewicz.com/2018/01/spring-reactor-and-elasticsearch-from.html


@Configuration
class ElasticsearchConfig {
    @Bean
    fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo(elassandraHostAndPort)
            .withWebClientConfigurer { webClient ->
                val exchangeStrategies: ExchangeStrategies = ExchangeStrategies.builder()
                    .codecs { configurer ->
                        configurer.defaultCodecs()
                            .maxInMemorySize(-1)
                    }
                    .build()
                webClient.mutate().exchangeStrategies(exchangeStrategies).build()
            }
            .build()
        return ReactiveRestClients.create(clientConfiguration)
    }

    @Bean
    fun elasticsearchConverter(): ElasticsearchConverter {
        return MappingElasticsearchConverter(elasticsearchMappingContext())
    }

    @Bean
    fun elasticsearchMappingContext(): SimpleElasticsearchMappingContext {
        return SimpleElasticsearchMappingContext()
    }

    @Bean
    fun reactiveElasticsearchOperations(): ReactiveElasticsearchOperations {
        return ReactiveElasticsearchTemplate(reactiveElasticsearchClient(), elasticsearchConverter())
    }

    @Value("\${spring.data.elasticsearch.client.reactive.endpoints}")
    private val elassandraHostAndPort: String? = null
}
