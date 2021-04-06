package com.totango

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHost
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import reactor.core.publisher.Mono
import org.elasticsearch.common.xcontent.XContentType

import org.elasticsearch.action.index.IndexRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.MonoSink
import java.lang.Exception


data class Doc(val userName: String, val json: String)

@Suppress("unused")
@Configuration
class ESRestClientConfiguration{
    @Bean
    fun restClient(): RestClient = RestClient.builder(HttpHost("localhost", 9200))
        .setRequestConfigCallback { config ->
            config
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
        }
        //.setMaxRetryTimeoutMillis(5_000)
        .build()

    @Bean
    fun highLevelClient(restClient: RestClient) = RestHighLevelClient(restClient)
}


class ESReactiveClient(private val client: RestHighLevelClient) {

    fun indexDoc(doc: Doc): Mono<IndexResponse> = Mono.create { sink: MonoSink<IndexResponse> ->
        val indexRequest = IndexRequest("people")
        indexRequest.source(mapper.writeValueAsString(doc), XContentType.JSON)
        client.indexAsync(indexRequest, object : ActionListener<IndexResponse> {
            override fun onResponse(indexResponse: IndexResponse) {
                sink.success(indexResponse)
            }

            override fun onFailure(e: Exception) {
                sink.error(e)
            }
        })
    }
    companion object{
        private val mapper =  ObjectMapper()
    }
}