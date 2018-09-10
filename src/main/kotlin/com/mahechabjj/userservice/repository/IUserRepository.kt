package com.mahechabjj.userservice.repository

import com.mahechabjj.userservice.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface IUserRepository : MongoRepository<User, String> {
    fun findUserById(id: String): User
    fun findUserByEmail(email: String): User
}