
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.gradle.java.test.fixtures)
    alias(libs.plugins.node.gradle)
    alias(libs.plugins.gradle.idea)
    id("maven-publish")
    id("com.gradleup.shadow") version "9.2.2"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["shadow"])

            groupId = rootProject.group.toString()
            artifactId = "compiler-plugin"
            version = rootProject.version.toString()
        }
    }
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    testFixtures {
        java.setSrcDirs(listOf("test-fixtures"))
    }
    test {
        java.setSrcDirs(listOf("test", "test-gen"))
        resources.setSrcDirs(listOf("testData"))
    }
}

idea {
    module.generatedSourceDirs.add(projectDir.resolve("test-gen"))
}

val testArtifacts: Configuration by configurations.creating

val annotationsRuntimeClasspath by configurations.dependencyScope("annotationsRuntimeClasspath") {
    isTransitive = false
}
val annotationsJvmRuntimeClasspath by configurations.resolvable("annotationsJvmRuntimeClasspath") {
    extendsFrom(annotationsRuntimeClasspath)
}

dependencies {
    compileOnly(libs.kotlin.compiler)
    compileOnly("net.bytebuddy:byte-buddy:1.18.8")
    runtimeOnly("net.bytebuddy:byte-buddy:1.18.8")
    implementation("net.bytebuddy:byte-buddy:1.18.8")
    implementation("net.bytebuddy:byte-buddy-agent:1.18.8")

    testFixturesApi(libs.kotlin.test.junit5)
    testFixturesApi(libs.kotlin.test.framework)
    testFixturesApi(libs.kotlin.compiler)
    testFixturesRuntimeOnly(libs.junit)
}

shadow {
    dependencies {
        implementation("net.bytebuddy:byte-buddy:1.18.8")
        implementation("net.bytebuddy:byte-buddy-agent:1.18.8")
    }
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }

    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
}

kotlin {
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
