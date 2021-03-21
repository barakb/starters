package com.totango.features

import reactor.core.publisher.Mono

@Suppress("unused")
class FeatureServiceImpl(
    private val featureRepository: FeatureRepository,
    private val featuresCache: FeaturesCache
) : FeatureService {

    override fun isFeatureEnabled(serviceId: String, feature: FeatureFlag): Mono<Boolean> =
        featuresCache.getAllFeatures(serviceId).map { it[serviceId] ?: false }

    override fun isFeatureEnabledBlock(serviceId: String, feature: FeatureFlag): Boolean =
        isFeatureEnabled(serviceId, feature).block()!!

    override fun isGlobalFeatureEnabled(feature: FeatureFlag): Mono<Boolean> =
        featuresCache.isGlobalFeatureEnabled(feature)

    override fun isGlobalFeatureEnabledBlock(feature: FeatureFlag): Boolean =
        isGlobalFeatureEnabled(feature).block()!!
}