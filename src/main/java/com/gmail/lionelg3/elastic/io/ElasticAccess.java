package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.io.conf.ElasticConfiguration;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by lionel on 21/11/2016.
 *
 */
class ElasticAccess {

    private final Settings settings;
    private final TransportAddress[] addresses;

    public ElasticAccess(ElasticConfiguration configuration) {
        Settings.Builder builder = Settings.builder();
        configuration.getSettings()
                .forEach(builder::put);
        this.settings = builder.build();

        this.addresses = new TransportAddress[configuration.getAddresses().size()];
        for (int i=0; i<addresses.length; i++) {
            String[] target = configuration.getAddresses().get(i).split(":");
            String host = target[0];
            int port = (target.length == 2 ) ? Integer.parseInt(target[1]) : 9300;
            try {
                this.addresses[i] = new InetSocketTransportAddress(InetAddress.getByName(host), port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public ElasticAccess(Settings settings, TransportAddress ... addresses) {
        this.settings = settings;
        this.addresses = addresses;
    }

    public Object getNewClient() {
        return new PreBuiltTransportClient(this.settings).addTransportAddresses(addresses);
    }
}
