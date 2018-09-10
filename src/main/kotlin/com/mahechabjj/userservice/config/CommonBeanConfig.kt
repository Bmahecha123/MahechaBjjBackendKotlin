package com.mahechabjj.userservice.config

import org.jasypt.util.password.StrongPasswordEncryptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommonBeanConfig {

    @Bean
    fun strongEncryptor(): StrongPasswordEncryptor {
        val encryptor = StrongPasswordEncryptor()
        return encryptor
    }
}