package com.gmail.lionelg3.elastic.object;

import javax.xml.bind.annotation.XmlID;

/**
 * Created by lionel on 22/11/2016
 *
 */
public class HomePage {

    @XmlID
    private String url;

    private String name;

    public HomePage() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomePage project = (HomePage) o;

        if (url != null ? !url.equals(project.url) : project.url != null) return false;
        return name != null ? name.equals(project.name) : project.name == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HomePage{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
