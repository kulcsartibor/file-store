package com.inew.filestore.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
 * @author Tibor Kulcsar
 * <p>
 * Date: 4/7/2018
 * @since
 */
@Configuration
public class MongoConfig extends AbstractMongoConfiguration
{
	@Value("${jsa.mongo.address}")
	private String mongoAddress;

	@Value("${jsa.mongo.database}")
	private String mongoDatabase;

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Override
	protected String getDatabaseName() {
		return mongoDatabase;
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(mongoAddress);
	}

	@Bean(name = "mongoClient")
	public MongoClient getMongoClient() {
		return new MongoClient(mongoAddress);
	}
}
