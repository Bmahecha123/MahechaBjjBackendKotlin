package com.mahechabjj.userservice.service

interface IEncryptionService {
    fun encryptString(input: String): String
    fun checkPassword(plainPassword: String, encryptedPassword: String): Boolean
}