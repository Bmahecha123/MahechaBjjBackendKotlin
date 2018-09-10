package com.mahechabjj.userservice.small.config

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import com.mongodb.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate

@Profile("UnitTests")
@Configuration
class MongoConfig : AbstractMongoConfiguration() {
    override fun getDatabaseName(): String {
        return "users"
    }

    @Bean
    override fun mongoClient(): MongoClient {
        return fongo().mongo
    }

    @Bean
    fun fongo(): Fongo {
        return Fongo(databaseName)
    }
}