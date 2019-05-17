package de.schrader.ktor

sealed class Thing<T>
class Some<T>(val value: T) : Thing<T>()
class None<T>(val info: String = "") : Thing<T>()
