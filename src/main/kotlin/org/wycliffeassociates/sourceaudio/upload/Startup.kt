package org.wycliffeassociates.sourceaudio.upload

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

const val port: Int = 4567

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = port,
        watchPaths = listOf("KotlinWeb"),
        module = Application::appModule
    ).start(true)
}