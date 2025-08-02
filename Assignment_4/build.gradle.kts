plugins {
    kotlin("jvm") version "2.1.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("net.bytebuddy:byte-buddy:1.14.10")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.10")
}

tasks.test {
    useJUnitPlatform()

    // Add JVM arguments to support dynamic agent loading
    jvmArgs("-XX:+EnableDynamicAgentLoading")

    // Optional: Set experimental Byte Buddy support
    systemProperty("net.bytebuddy.experimental", "true")
}
kotlin {
    jvmToolchain(22)
}