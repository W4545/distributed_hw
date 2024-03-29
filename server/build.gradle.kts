plugins {
    id("java")
    id("application")
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation(project(":common"))
}

application {
    mainClass.set("dev.jacaro.school.distributed.Server")
    applicationDefaultJvmArgs += "-Xmx6g"
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}