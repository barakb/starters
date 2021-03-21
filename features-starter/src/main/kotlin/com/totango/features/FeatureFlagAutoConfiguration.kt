package com.totango.features

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Suppress("unused")
@Configuration
class FeatureFlagAutoConfiguration {
    @Bean
    fun featureService(
        featureRepository: FeatureRepository,
        featuresCache: FeaturesCache
    ): FeatureService {
        return FeatureServiceImpl(featureRepository, featuresCache)
    }
}