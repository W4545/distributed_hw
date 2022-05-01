plugins {
    id("java-library")
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}