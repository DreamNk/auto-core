package com.ghx.auto.core.support;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class MongoDBClient {
	
	private EnvConfig envConfig;
	private String section; 
	private MongoTemplate mongoTemplate;
	
    public MongoDBClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	createMongoTemplate();
    }
    
    private void createMongoTemplate() {
		this.mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(""), ""));
	}

}
