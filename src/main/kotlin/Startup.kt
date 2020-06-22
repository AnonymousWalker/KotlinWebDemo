package org.wycliffeassociates.sourceaudio.upload

import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.config.ApplicationConfig
import io.ktor.config.HoconApplicationConfig
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.loadCommonConfiguration
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

//fun main(args: Array<String>) {
//    embeddedServer(Netty, configure =  {
//
//    }){
//
//    }.start(true)
//}