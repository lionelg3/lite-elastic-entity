package com.gmail.lionelg3.elastic.io.server;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by lionel on 13/03/2017.
 *
 */
public class EmbeddedElasticSearchServer implements ISuiteListener {

    private static EmbeddedElastic server;

    public static void start() throws IOException, InterruptedException {
        System.out.println("Starting Elastic Server....");
        server = EmbeddedElastic.builder()
                .withElasticVersion("5.0.0")
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9300)
                .withStartTimeout(1, MINUTES)
                .build()
                .start();
        System.out.println("Elastic Server ready");

    }

    public static void stop() throws IOException, InterruptedException {
        System.out.println("Stopping Elastic Server....");
        server.stop();
    }


    @Override
    public void onStart(ISuite iSuite) {
        try {
            start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ISuite iSuite) {
        try {
            stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
