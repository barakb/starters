package com.totango.features

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("unused")
interface FeatureRepository : ReactiveCrudRepository<Feature, String> {

    @Query("SELECT DEFAULT_FEATURE_ENABLED FROM FEATURE_SET WHERE FEATURE = :feature")
    fun isEnabled(feature: String): Mono<Int>

    @Query(
        "select  FEATURE_SET.FEATURE, IFNULL(IS_FEATURE_ENABLED, DEFAULT_FEATURE_ENABLED) as IS_FEATURE_ENABLED "
                + " from " + "FEATURE_SET" + " as FEATURE_SET left outer join " + "SERVICE_FEATURE_SET" + " as SERVICE_FEATURE_SET "
                + " on FEATURE_SET.FEATURE = SERVICE_FEATURE_SET.FEATURE "
                + " and SERVICE_FEATURE_SET.SERVICE_ID = :serviceId"
    )
    fun getAllFeatures(serviceId: String): Flux<Feature>
}