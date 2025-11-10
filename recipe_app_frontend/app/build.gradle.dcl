androidApplication {
    namespace = "org.example.app"

    // Note: Testing configuration removed to avoid unsupported 'unitTests' DSL in declarative Gradle.

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))

        // AndroidX and Material UI
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

        // Room for local persistence (runtime usage only in this sample)
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")

        // Lifecycle runtime (for coroutines scope if needed)
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    }
}
