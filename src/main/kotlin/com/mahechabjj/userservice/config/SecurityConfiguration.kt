package com.mahechabjj.userservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
open class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    override protected fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll()

        httpSecurity.csrf().disable()
    }
}