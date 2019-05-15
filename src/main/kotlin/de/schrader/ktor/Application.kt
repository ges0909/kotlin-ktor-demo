package de.schrader.ktor

import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.route.persons
import de.schrader.ktor.service.PersonService
import de.schrader.ktor.service.impl.PersonServiceImpl
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.request.path
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

val appModule = module {
    single<PersonService> { PersonServiceImpl(get()) } // get() resolves PersonRepository
    single { PersonRepository() }
}

fun Application.module() {
    // log.info("Install features")

    install(StatusPages)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Koin) {
        modules(appModule)
    }
    install(CallLogging) {
        level = Level.INFO
        // if filter returns true, the call is logged; if no filters are defined, everything is logged
        filter { call -> call.request.path().startsWith("/person") }
    }
    // install(Locations)

    Database.connect(url = "jdbc:h2:~/test;DATABASE_TO_UPPER=false", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(PersonRepository.DatabaseTable)
    }

    routing {
        persons()
    }
}
