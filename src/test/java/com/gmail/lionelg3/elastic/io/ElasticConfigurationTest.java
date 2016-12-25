package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.io.conf.ElasticConfiguration;
import com.gmail.lionelg3.elastic.io.conf.ElasticConfigurationLoader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticConfigurationTest {

    public static ElasticConfigurationLoader LOADER;

    @BeforeClass
    public static void init() {
        LOADER = new ElasticConfigurationLoader();
    }

    @Test()
    public void loadSpecificFile() {
        Exception err = null;
        ElasticConfiguration conf = null;
        try {
            conf = LOADER.loadConfiguration("META-INF/elastic.json");
        } catch (IOException e) {
            err = e;
        }
        Assert.assertNull(err, "An exception is emmit");
        Assert.assertNotNull(conf, "Configuration nulle");

        Assert.assertEquals(conf.getAddresses().size(), 3);
        Assert.assertEquals(conf.getEntities().size(), 3);
        Assert.assertEquals(conf.getSettings().get("client.transport.ignore_cluster_name"), "true");
    }

    @Test
    public void loadDefaultFile() {
        Exception err = null;
        ElasticConfiguration conf = null;
        try {
            conf = LOADER.loadDefaultMetaInfConfiguration();
        } catch (IOException e) {
            err = e;
        }
        Assert.assertNull(err, "An exception is emmit");
        Assert.assertNotNull(conf, "Configuration nulle");

        Assert.assertEquals(conf.getAddresses().size(), 3);
        Assert.assertEquals(conf.getEntities().size(), 3);
        Assert.assertEquals(conf.getSettings().get("client.transport.ignore_cluster_name"), "true");
    }

    @Test
    public void loadReferencedFile() {
        Exception err = null;
        ElasticConfiguration conf = null;
        try {
            conf = LOADER.loadConfiguration("META-INF/elastic_pointer.json");
        } catch (IOException e) {
            err = e;
        }
        Assert.assertNull(err, "An exception is emmit");
        Assert.assertNotNull(conf, "Configuration nulle");

        Assert.assertEquals(conf.getAddresses().size(), 3);
        Assert.assertEquals(conf.getEntities().size(), 3);
        Assert.assertEquals(conf.getSettings().get("client.transport.ignore_cluster_name"), "true");
    }
}
