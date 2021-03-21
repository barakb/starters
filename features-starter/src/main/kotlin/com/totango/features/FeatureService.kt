package com.totango.features

import reactor.core.publisher.Mono

interface FeatureService {
    fun isFeatureEnabled(serviceId: String, feature: FeatureFlag) : Mono<Boolean>
    fun isFeatureEnabledBlock(serviceId: String, feature: FeatureFlag) : Boolean
    fun isGlobalFeatureEnabled(feature: FeatureFlag) : Mono<Boolean>
    fun isGlobalFeatureEnabledBlock(feature: FeatureFlag) : Boolean
}