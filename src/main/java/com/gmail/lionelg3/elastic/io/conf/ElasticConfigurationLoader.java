package com.gmail.lionelg3.elastic.io.conf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticConfigurationLoader {

    public ElasticConfiguration loadDefaultMetaInfConfiguration() throws IOException {
        return loadConfiguration("META-INF/elastic.json");
    }

    public ElasticConfiguration loadConfiguration(String jsonFilePath) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> resources = classLoader.getResources(jsonFilePath);
        if (resources != null) {
            while (resources.hasMoreElements()) {
                try {
                    URL url = resources.nextElement();
                    return loadConfiguration(url.openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IOException("Fichier de configuration non trouvé.");
    }

    private ElasticConfiguration loadConfiguration(InputStream in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ElasticConfiguration conf = mapper.readValue(in, ElasticConfiguration.class);
        if (conf.getFiles() == null)
            return conf;
        for (String file : conf.getFiles()) {
            try {
                conf = loadConfiguration(new FileInputStream(file));
                if (conf != null)
                    return conf;
            } catch (IOException ioe) {
                //ioe.printStackTrace();
            }
        }
        throw new IOException("Impossible de trouver une configuration fonctionnelle.");
    }
}
