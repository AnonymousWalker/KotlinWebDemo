import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val port: Int = 4567
    embeddedServer(
        Netty,
        port = port,
        watchPaths = listOf("KotlinWeb"),
        module = Application::module
    ).start(true)
}