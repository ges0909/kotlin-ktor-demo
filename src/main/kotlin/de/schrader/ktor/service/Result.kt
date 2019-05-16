package de.schrader.ktor.service

import io.ktor.http.HttpStatusCode

sealed class Result<T>(val value: T?)
class Failure(val status: HttpStatusCode) : Result<Unit>(null)
