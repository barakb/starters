package com.totango.features

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Suppress("unused")
@Configuration
@EnableCaching
class FeatureFlagAutoConfiguration {
    @Bean
    fun featureService(
        featuresCache: FeaturesCache
    ): FeatureService {
        return FeatureServiceImpl(featuresCache)
    }
}