package com.totango.features

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("SERVICE_FEATURE_SET")
data class Feature(@Column("FEATURE") val feature: String,
                   @Column("SERVICE_ID") val serviceId: String?,
                   @Column("IS_FEATURE_ENABLED") val enabled: Int = 1,
                   @Column("ID") @Id val id: Long? = null)