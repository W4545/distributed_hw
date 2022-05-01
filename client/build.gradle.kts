plugins {
    id("java")
    id("application")
}

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains:annotations:23.0.0")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("dev.jacaro.school.distributed.Client")
}