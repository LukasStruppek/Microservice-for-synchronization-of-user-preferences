package de.privacy_avare.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

@EnableCouchbaseRepositories
@Configuration
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

	@Override
	protected List<String> getBootstrapHosts() {
		return Collections.singletonList("127.0.0.1");
	}

	@Override
	protected String getBucketName() {
		return "beer-sample";
	}

	@Override
	protected String getBucketPassword() {
		return "";
	}
}
