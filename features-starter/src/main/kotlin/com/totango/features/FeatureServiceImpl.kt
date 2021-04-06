package com.totango.features

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono


@Suppress("unused")
class FeatureServiceImpl(
    private val featuresCache: FeaturesCache
) : FeatureService {

    override fun isFeatureEnabled(serviceId: String, feature: FeatureFlag): Mono<Boolean> =
        featuresCache.getAllFeatures(serviceId).map { value(it, serviceId) }

    override fun isFeatureEnabledBlock(serviceId: String, feature: FeatureFlag): Boolean =
        isFeatureEnabled(serviceId, feature).block()!!

    override fun isGlobalFeatureEnabled(feature: FeatureFlag): Mono<Boolean> =
        featuresCache.isGlobalFeatureEnabled(feature)

    override fun isGlobalFeatureEnabledBlock(feature: FeatureFlag): Boolean =
        isGlobalFeatureEnabled(feature).block()!!

    private fun value(map: Map<String, Boolean>, key: String): Boolean {
        val ret = map[key]
        return if (ret == null) {
            logger.error("Request of non-existing feature $key")
            false
        } else {
            ret
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FeatureServiceImpl::class.java)
    }

}