import com.google.protobuf.gradle.id

plugins {
    id("typestream.kotlin-conventions")
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
    google()
}

val grpcVersion: String = libs.versions.grpc.get()
val grpcKotlinVersion: String = libs.versions.grpcKotlin.get()
val protobufVersion: String = libs.versions.protobuf.get()

dependencies {
    protobuf(project(":protos"))
    api(kotlin("stdlib"))

    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("com.google.protobuf:protobuf-java-util:$protobufVersion")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
}

sourceSets {
    val main by getting { }
    main.java.srcDirs("build/generated/source/proto/main/java")
    main.java.srcDirs("build/generated/source/proto/main/grpc")
    main.java.srcDirs("build/generated/source/proto/main/kotlin")
    main.java.srcDirs("build/generated/source/proto/main/grpckt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

val grpcBin : String? = System.getenv("PROTOC_GRPC_BIN")

project.protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
	    if(grpcBin == null) {
                artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
	    }
	    else {
	        path = grpcBin
	    }
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
