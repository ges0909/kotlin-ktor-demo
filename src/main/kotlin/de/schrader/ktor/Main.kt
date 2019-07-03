package de.schrader.ktor

import io.ktor.locations.Locations
import io.ktor.server.netty.EngineMain

/**
 * programmatic server config
 */
//fun main() {
//    embeddedServer(
//        factory = Netty,
//        port = 8080,
//        module = Application::main
//    ).start(wait = true)
//}

/**
 * config resource file: resources/application.conf
 */
// uses 'application.conf"
//fun main(args: Array<String>) {
//    embeddedServer(Netty,
//        commandLineEnvironment(args)
//    ).start()
//}

/**
 * external config file
 */
fun main() = EngineMain.main(arrayOf("-config=dev.conf"))
