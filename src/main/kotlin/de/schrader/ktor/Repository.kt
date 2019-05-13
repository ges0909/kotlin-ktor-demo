package de.schrader.ktor

import de.schrader.ktor.Persons.autoIncrement
import de.schrader.ktor.Persons.primaryKey
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<Int>
    val name = varchar("name", 50) // Column<String>
    val age = integer("age") // Column<Int>
}

object Repository {
    fun init() {
        Database.connect(url = "jdbc:h2:~/test;DATABASE_TO_UPPER=false", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Persons)
        }
    }
}
