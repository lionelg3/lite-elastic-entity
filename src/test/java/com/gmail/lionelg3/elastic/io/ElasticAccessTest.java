package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.io.conf.ElasticConfiguration;
import com.gmail.lionelg3.elastic.io.conf.ElasticConfigurationLoader;
import com.gmail.lionelg3.elastic.io.server.EmbeddedElasticSearchServer;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.client.Client;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by lionel on 27/11/2016.
 *
 */
@Listeners(EmbeddedElasticSearchServer.class)
public class ElasticAccessTest {

    @Test
    public void testConstructor() {
        Exception err = null;
        try {
            ElasticConfiguration configuration = new ElasticConfigurationLoader().loadConfiguration("META-INF/elastic_sample.json");
            ElasticAccess elasticAccess = new ElasticAccess(configuration);
            Client client = elasticAccess.getClient();
            client.prepareGet().get();
        } catch (Exception e) {
            err = e;
        }
        Assert.assertTrue((err instanceof ActionRequestValidationException));
    }
}
