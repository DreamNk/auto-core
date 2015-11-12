package com.ghx.auto.core.support;

import java.net.UnknownHostException;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

public class MongoDBClient {
	
	private EnvConfig envConfig;
	private String section; 
	private MongoTemplate mongoTemplate;
	
    public MongoDBClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	createMongoTemplate();
    }
    
    public Set<String> get_collection_names() {
    	try {
    		return this.mongoTemplate.getCollectionNames();
		} catch (MongoException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		} catch (DataAccessException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		}
    	
    	return null;
    }
    
    private void createMongoTemplate() {
		try {
			this.mongoTemplate = new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI("mongodb://configsvc-user:configsvc123@localhost:27017/configsvc")));
		} catch (UnknownHostException uhe) {
			Assert.fail("Unknown mongo host, error message: " + uhe.getMessage());
		}
    }

}
