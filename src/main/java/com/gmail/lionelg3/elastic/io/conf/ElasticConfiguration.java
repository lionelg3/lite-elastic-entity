package com.gmail.lionelg3.elastic.io.conf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticConfiguration {

    private ArrayList files;
    private ArrayList<String> addresses;
    private ArrayList<String> entities;
    private HashMap<String, String> settings;

    public ElasticConfiguration() {
    }

    public ArrayList getFiles() {
        return files;
    }

    public void setFiles(ArrayList files) {
        this.files = files;
    }

    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<String> getEntities() {
        return entities;
    }

    public ArrayList<Class> getClasses() {
        ArrayList<Class> classes = new ArrayList<>();
        entities.forEach(classname -> {
            try {
                classes.add(Class.forName(classname));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return classes;
    }

    public void setEntities(ArrayList<String> entities) {
        this.entities = entities;
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }


    @Override
    public String toString() {
        return "ElasticConfiguration{" +
                "files=" + files +
                ", addresses=" + addresses +
                ", entities=" + entities +
                ", settings=" + settings +
                '}';
    }
}
