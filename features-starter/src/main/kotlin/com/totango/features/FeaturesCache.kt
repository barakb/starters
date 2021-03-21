package com.totango.features

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FeaturesCache(private val featureRepository: FeatureRepository) {

    @Cacheable(cacheNames = ["service_features"], sync = true)
    fun getAllFeatures(serviceId: String): Mono<Map<String, Boolean>> =
        featureRepository.getAllFeatures(serviceId).map {
            (it.feature to (it.enabled != 0))
        }.collectList().map { it.toMap() }.cache()

    @Cacheable(cacheNames = ["global_features"], sync = true)
    fun isGlobalFeatureEnabled(feature: FeatureFlag): Mono<Boolean> =
        featureRepository.isEnabled(feature.name)
            .map { it != 0 }
            .defaultIfEmpty(false).cache()
}