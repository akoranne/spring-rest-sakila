package com.example.app.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.example.app.services.auth.repository",
                "com.example.app.services.catalog.repository",
                "com.example.app.services.customer.repository",
                "com.example.app.services.location.repository",
                "com.example.app.services.payment.repository",
                "com.example.app.services.rental.repository",
                "com.example.app.services.staff.repository",
                "com.example.app.services.store.repository",
        }
)
public class PersistenceConfig {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public SQLQueryFactory sqlQueryFactory(DataSource dataSource) {
        final var provider = new SpringConnectionProvider(dataSource);
        return new SQLQueryFactory(querydslMySQLConfiguration(), provider);
    }

    @Bean
    public MySQLQueryFactory mysqlQueryFactory(DataSource dataSource) {
        final var provider = new SpringConnectionProvider(dataSource);
        return new MySQLQueryFactory(querydslMySQLConfiguration(), provider);
    }

    @Bean
    public com.querydsl.sql.Configuration querydslMySQLConfiguration() {
        final var templates = MySQLTemplates.builder().printSchema().build();
        final var configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    @Bean
    public BlazeJPAQueryFactory blazeJPAQueryFactory() {
        return new BlazeJPAQueryFactory(entityManager, criteriaBuilderFactory());
    }

    @Bean
    public CriteriaBuilderFactory criteriaBuilderFactory() {
        final var config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }
}
