package de.schrader.ktor.domain

import org.jetbrains.exposed.sql.Table

object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<Int>
    val name = varchar("name", 50) // Column<String>
    val age = integer("age") // Column<Int>
}
