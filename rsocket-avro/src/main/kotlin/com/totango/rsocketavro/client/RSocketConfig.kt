package com.totango.rsocketavro.client

import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.WebsocketClientTransport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.codec.Decoder
import org.springframework.core.codec.Encoder
import org.springframework.http.MediaType
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import reactor.util.retry.Retry
import java.time.Duration


@Configuration
class RSocketConfig {
    @Bean
    fun rSocketStrategies(): RSocketStrategies {
        return RSocketStrategies.builder()
            .encoders { encoders: MutableList<Encoder<*>> ->
                encoders.add(
                    Jackson2CborEncoder()
                )
            }
            .decoders { decoders: MutableList<Decoder<*>> ->
                decoders.add(
                    Jackson2CborDecoder()
                )
            }
            .build()
    }

    @Bean
    fun getRSocketRequester(builder: RSocketRequester.Builder): RSocketRequester {
        return builder
            .rsocketConnector { rSocketConnector: RSocketConnector ->
                rSocketConnector.reconnect(
                    Retry.fixedDelay(2, Duration.ofSeconds(2))
                )
            }
            .dataMimeType(MediaType.APPLICATION_CBOR)
//            .transport(TcpClientTransport.create(6565))
            .transport(WebsocketClientTransport.create(6565))
    }
}