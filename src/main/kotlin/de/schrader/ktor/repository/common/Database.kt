package de.schrader.ktor.repository.common

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.schrader.ktor.repository.PersonRepository
import de.schrader.ktor.repository.auth.UserRepository
import org.jetbrains.exposed.sql.Database
import org.koin.core.KoinComponent
import org.koin.core.inject

object Database : KoinComponent {

    private val userRepository: UserRepository by inject()
    private val personRepository: PersonRepository by inject()

    fun init() {
        /*val db = */ Database.connect(hikari())
//        transaction {
//            addLogger(StdOutSqlLogger)
//        }
        userRepository.createTable()
        personRepository.createTable()
    }
}

private fun hikari(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.h2.Driver"
    config.jdbcUrl = "jdbc:h2:mem:test;DATABASE_TO_UPPER=FALSE"
    // config.jdbcUrl = "jdbc:h2:~/test;DATABASE_TO_UPPER=FALSE"
    config.maximumPoolSize = 3
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.validate()
    return HikariDataSource(config)
}
