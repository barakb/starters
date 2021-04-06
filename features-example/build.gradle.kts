import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}
group = "com.totango"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":features-starter"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

//    implementation("org.elasticsearch.client:elasticsearch-rest-client:5.6.16")
//    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:5.6.16")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootRun {
    jvmArgs = listOf("-Dspring.output.ansi.enabled=ALWAYS")
}


