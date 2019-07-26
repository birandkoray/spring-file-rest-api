package com.example.demo.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory() , mappingMongoConverter());
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient();
    }

    @Override
    protected String getDatabaseName() {
        return "bence-yolun-yol";
    }
}
