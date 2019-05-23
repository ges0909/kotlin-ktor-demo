package de.schrader.ktor.repository

// import io.ktor.locations.Location
import com.fasterxml.jackson.annotation.JsonInclude

// @Location("/persons")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Person(val id: Int?, val name: String, var age: Int)
