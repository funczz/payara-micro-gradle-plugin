import org.gradle.api.JavaVersion

object BuildSrcUtil {

    fun javaVersionToJvmTarget(version: JavaVersion): String {
        return version
            .name
            .replace("VERSION_", "")
            .replace("_", ".")
    }

}