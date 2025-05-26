rootProject.name = "logo-server"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("palantirJavaFormat", "2.67.0")
            plugin("spotless", "com.diffplug.spotless").version("7.0.3")
            version("antlr4", "4.13.0")
            library("lsp4j", "org.eclipse.lsp4j:org.eclipse.lsp4j:0.21.1")
            version("slf4j", "2.0.17")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-jdk14", "org.slf4j", "slf4j-jdk14").versionRef("slf4j")
            version("junit", "5.10.0")
            library("junit-bom", "org.junit", "junit-bom").versionRef("junit")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library(
                "junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params"
            ).withoutVersion()
            library("truth", "com.google.truth:truth:1.4.4")
            library("mockito-core", "org.mockito:mockito-core:5.17.0")
            library("awaitility", "org.awaitility:awaitility:4.3.0")
            library("guava", "com.google.guava:guava:33.0.0-jre")
        }
    }
}
