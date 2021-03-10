package com.imma.utils

object EnvConstants {
    const val ENV_MODE: String = "ktor.mode"
    const val ENV_MODE_DEV: String = "dev"
    const val ENV_MODE_PROD: String = "prod"
    const val TOKEN_EXPIRE_MINUTES: String = "ktor.auth.token.expire.minutes"
    const val JWT_DOMAIN = "ktor.jwt.domain"
    const val JWT_AUDIENCE = "ktor.jwt.audience"
    const val JWT_REALM = "ktor.jwt.realm"
    const val JWT_HMAC256_SECRET = "ktor.jwt.hmac256.secret"

    const val MONGO_HOST: String = "ktor.mongo.host"
    const val MONGO_PORT: String = "ktor.mongo.port"
    const val MONGO_NAME: String = "ktor.mongo.name"

    const val SNOWFLAKE_WORKER: String = "ktor.snowflake.worker"
    const val SNOWFLAKE_DATA_CENTER: String = "ktor.snowflake.dataCenter"
}