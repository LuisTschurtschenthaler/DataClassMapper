import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}

group = "com.luistschurtschenthaler"
version = "1.0.0"

mavenPublishing {
    coordinates(
        groupId = "io.github.luistschurtschenthaler",
        artifactId = "data-class-mapper",
        version = "1.0.0"
    )

    pom {
        name.set("Data class mapper")

        url.set("https://github.com/LuisTschurtschenthaler/DataClassMapper")
        description.set("This library can be used to map data classes automatically.")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("luistschurtschenthaler")
                name.set("Luis Tschurtschenthaler")
                email.set("business.luis.tschurtschenthaler@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/LuisTschurtschenthaler/DataClassMapper")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
