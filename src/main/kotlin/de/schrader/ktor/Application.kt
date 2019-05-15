package de.schrader.ktor

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.schrader.ktor.controller.persons
import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.service.PersonService
import de.schrader.ktor.service.PersonServiceImpl
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

fun Application.main() {
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

    Database.connect(hikari())
    transaction {
        SchemaUtils.create(PersonRepository.DatabaseTable)
    }

    routing {
        persons()
    }
}

private val appModule = module {
    single<PersonService> { PersonServiceImpl(get()) } // get() resolves PersonRepository
    single { PersonRepository() }
}

private fun hikari(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.h2.Driver"
    config.jdbcUrl = "jdbc:h2:~/test"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
}
