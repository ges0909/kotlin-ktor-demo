package de.schrader.ktor

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

const val MIN_USER_ID_LENGTH = 4
const val MIN_PASSWORD_LENGTH = 6

val config: Config = ConfigFactory.load()

val hashKey = hex(config.getString("secret"))

val hmacKey = SecretKeySpec(hashKey, "hmacSHA1")

fun hash(password: String): String {
    val hmac = Mac.getInstance("hmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray((Charsets.UTF_8))))
}

private val userIdPattern = "[a-zA-Z0-9_.]+".toRegex()

internal fun isUserIdValid(userId: String) = userId.matches(userIdPattern)
