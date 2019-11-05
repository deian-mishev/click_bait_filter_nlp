package com.example.accessingdatamongodb;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
class ApplicationConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDB;

    @Override
    protected String getDatabaseName() {
        return mongoDB;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(mongoHost + ":" + mongoPort);
    }
}