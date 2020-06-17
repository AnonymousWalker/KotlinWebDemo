import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 4567, watchPaths = listOf("KotlinWeb"), module = Application::module)
    server.start(wait = true)
}