package de.schrader.ktor

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
//    embeddedServer(
//        factory = Netty,
//        port = 8080,
//        module = Application::main
//    ).start(wait = true)
    embeddedServer(Netty,
        commandLineEnvironment(args)
    ).start()
}
