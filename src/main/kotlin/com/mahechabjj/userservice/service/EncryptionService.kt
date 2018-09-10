package com.mahechabjj.userservice.service

import org.jasypt.util.password.StrongPasswordEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EncryptionService : IEncryptionService {
    private lateinit var strongEncryptor: StrongPasswordEncryptor

    @Autowired
    fun setStrongEncryptor(strongPasswordEncryptor: StrongPasswordEncryptor) {
        strongEncryptor = strongPasswordEncryptor
    }

    override fun encryptString(input: String): String {
        return strongEncryptor.encryptPassword(input)
    }

    override fun checkPassword(plainPassword: String, encryptedPassword: String): Boolean {
        return strongEncryptor.checkPassword(plainPassword, encryptedPassword)
    }
}