package de.schrader.ktor.model

// import io.ktor.locations.Location

// @Location("/person")
data class Person(val id: Int? = null, val userId: String, val name: String, var age: Int)