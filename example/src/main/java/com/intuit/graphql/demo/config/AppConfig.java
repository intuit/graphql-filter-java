package com.intuit.graphql.demo.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.intuit.graphql.demo")
@EnableTransactionManagement
@EntityScan("com.intuit.graphql.demo.persistence.entity")
public class AppConfig {
}
