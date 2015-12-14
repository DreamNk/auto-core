package com.ghx.auto.core.support;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;

public class MongoDBClient {
	
	private EnvConfig envConfig;
	private String section; 
	private MongoOperations mongoOperations;
	
    public MongoDBClient(EnvConfig envConfig, String section) {
    	this.envConfig = envConfig;
    	this.section = section;
    	createMongoTemplate();
    }
    
    public Set<String> get_collection_names() {
    	try {
    		return this.mongoOperations.getCollectionNames();
		} catch (MongoException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		} catch (DataAccessException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		}
    	
    	return null;
    }
    
    public long get_collection_count(String collectionName, String query) {
    	try {
    		return mongoOperations.count(new BasicQuery(query), collectionName);
		} catch (MongoException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		} catch (DataAccessException me) {
			Assert.fail("Unable to get the collection names, error message: " + me.getMessage());
		}
    	
    	return 0; //should not happen
    	
    }
    
    public MongoDBClient verify_collection_count(String collectionName, String query, long expectedCount) {
    	long actualCount = get_collection_count(collectionName, query);
    	Assert.assertEquals(actualCount, expectedCount);
    	
    	return this;
    }
    
    public MongoDBClient verify_collection_count_greater_than_zero(String collectionName, String query) {
    	long actualCount = get_collection_count(collectionName, query);
    	Assert.assertTrue(actualCount > 0, "Collection count is not greater than zero, query is: " + query);
    	
    	return this;
    }
    
    public MongoDBClient verify_distinct_field_values_from_collection(String collectionName, String fieldName, String queryFilter, Object[] expectedValues) {
    	Collection actualList = null;
    	Collection expectedList = Arrays.asList(expectedValues);
    	Collection diffList;
    	
    	try {
    		actualList = mongoOperations.getCollection(collectionName).distinct("component", BasicDBObject.parse(queryFilter));
		} catch (MongoException me) {
			Assert.fail("Unable to verify, error message: " + me.getMessage());
		} catch (DataAccessException me) {
			Assert.fail("Unable to verify, error message: " + me.getMessage());
		}
    	
    	diffList = CollectionUtils.disjunction(actualList, expectedList);
    	
    	Assert.assertTrue(diffList.size() == 0, "Expected and Actual List values did not match, diff is: " + diffList);
    	
    	return this;
    	
    }
    
    private void createMongoTemplate() {
		try {
			this.mongoOperations = new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI(envConfig.getConfigParamValue(this.section, "uri"))));
		} catch (UnknownHostException uhe) {
			Assert.fail("Unknown mongo host, error message: " + uhe.getMessage());
		}
    }

}