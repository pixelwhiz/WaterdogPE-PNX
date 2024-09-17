plugins {
    application
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

repositories {
    mavenLocal()
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.waterdog.dev/main")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    implementation(libs.com.bugsnag.bugsnag)
    implementation(libs.org.bstats.bstats.base)
    implementation(libs.net.cubespace.yamler.core)
    implementation(libs.org.yaml.snakeyaml)
    implementation(libs.com.google.code.gson.gson)
    implementation(libs.it.unimi.dsi.fastutil)

    implementation(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)

    implementation(libs.org.apache.commons.commons.lang3)
    implementation(libs.org.apache.logging.log4j.log4j.api)
    implementation(libs.org.apache.logging.log4j.log4j.core)
    implementation(libs.com.lmax.disruptor)
    implementation(libs.jline.jline)
    implementation(libs.org.jline.jline.terminal)
    implementation(libs.org.jline.jline.terminal.jna)
    implementation(libs.org.jline.jline.reader)
    implementation(libs.net.minecrell.terminalconsoleappender)
//    implementation("org.cloudburstmc.protocol:bedrock-connection:3.0.0.Beta2-SNAPSHOT")
    implementation(libs.org.cloudburstmc.protocol.bedrock.codec)
    implementation(libs.org.cloudburstmc.protocol.bedrock.connection)
    implementation(libs.org.cloudburstmc.netty.netty.transport.raknet)
    implementation(libs.io.netty.netty.transport.native.epoll)
    implementation(libs.io.netty.netty.transport.native.kqueue)
    implementation(libs.com.nimbusds.nimbus.jose.jwt)
}

group = "dev.waterdog.waterdogpe"
version = ""
description = "waterdog"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar{
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
    
    relocate("org.bstats","dev.waterdog.waterdogpe")
}

application {
    mainClass = "dev.waterdog.waterdogpe.WaterdogPE"
}
