plugins {
    application
    alias(libs.plugins.spotless)
    antlr
}

application {
    mainClass = "com.mikosik.logoserver.Main"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

dependencies {
    implementation(libs.lsp4j)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.jdk14)
    implementation(libs.guava)
    antlr("org.antlr:antlr4:4.13.0")
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.awaitility)
}

spotless {
    java {
        palantirJavaFormat(libs.versions.palantirJavaFormat.get()).style("GOOGLE")
    }
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }

    antlr4 {
        target("**/*.g4")
        antlr4Formatter()
    }
}
tasks.named("spotlessJava") {
    dependsOn(tasks.named("generateGrammarSource"))
}
tasks.test {
    useJUnitPlatform()
}
