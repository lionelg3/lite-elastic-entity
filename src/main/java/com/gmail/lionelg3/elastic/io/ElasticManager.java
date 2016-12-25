package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.io.conf.ElasticConfiguration;
import com.gmail.lionelg3.elastic.io.conf.ElasticConfigurationLoader;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticManager {

    private static ElasticManager INSTANCE;
    private ElasticConfiguration configuration;
    private ElasticAccess access;
    private HashMap<Class, ElasticRepository<Class>> repositories;
    private ElasticIdResolver idResolver;

    @SuppressWarnings("unchecked")
    private ElasticManager() {
        this.idResolver = new ElasticIdResolver();
        try {
            if (System.getProperty("elastic.config.file") != null)
                configuration = new ElasticConfigurationLoader().loadConfiguration(System.getProperty("elastic.config.file"));
            else
                configuration = new ElasticConfigurationLoader().loadDefaultMetaInfConfiguration();
            access = new ElasticAccess(configuration);
            repositories = new HashMap<>();
            configuration.getClasses().forEach(clazz -> {
                repositories.put(clazz, new ElasticRepository<>(access, clazz));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ElasticManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ElasticManager();
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private <T> ElasticRepository<T> _getRepository(T t) {
        return (ElasticRepository<T>)repositories.get(t.getClass());
    }
    @SuppressWarnings("unchecked")
    private <T> ElasticRepository<T> _getRepository(Class<T> c) {
        return (ElasticRepository<T>) repositories.get(c);
    }

    public <T> String persist(T t) {
        String id = idResolver.getId(t);
        return _getRepository(t).insert(id, t);
    }

    public <T> T find(Class<T> c, String id) {
        return _getRepository(c).load(id);
    }

    public <T> String remove(T t) {
        String id = idResolver.getId(t);
        return _getRepository(t.getClass()).delete(id);
    }

    public <T> T merge(T t) {
        String id = idResolver.getId(t);
        return _getRepository(t).update(id, t);
    }

    public <T> List<T> search(QueryBuilder queryBuilder, Class<T> c) {
        return _getRepository(c).search(queryBuilder);
    }

    public <T> List<T> search(HashMap<String, Object> constraints, Class<T> c) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        constraints.forEach((k, v) -> query.must().add(QueryBuilders.matchQuery(k, v)));
        return _getRepository(c).search(query);
    }

    public <T> List<T> search(String query, Class<T> c) {
        return _getRepository(c).search(query);
    }

    public <T> List<T> fetchAll(Class<T> c) {
        return _getRepository(c).search(QueryBuilders.boolQuery());
    }

    public <T> void persist(T t, Consumer<T> f) {
        String id = idResolver.getId(t);
        _getRepository(t).insert(id, t, f);
    }

    public <T> void find(String id, Class<T> t, Consumer<T> f) {
        _getRepository(t).load(id, f);
    }

    public <T> void remove(T t, Consumer<String> f) {
        String id = idResolver.getId(t);
        _getRepository(t).delete(id, f);
    }

    public <T> void merge(T t, Consumer<T> f) {
        String id = idResolver.getId(t);
        _getRepository(t).update(id, t, f);
    }

    public <T> void search(QueryBuilder queryBuilder, Class<T> t, Consumer<List<T>> f) {
        _getRepository(t).search(queryBuilder, f);
    }

    public <T> void search(String query, Class<T> t, Consumer<List<T>> f) {
        _getRepository(t).search(query, f);
    }

    public <T> void search(HashMap<String, Object> constraints, Class<T> c, Consumer<List<T>> f) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        constraints.forEach((k, v) -> query.must().add(QueryBuilders.matchQuery(k, v)));
        _getRepository(c).search(query, f);
    }

    public <T> void fetchAll(Class<T> c, Consumer<List<T>> f) {
        _getRepository(c).search(QueryBuilders.boolQuery(), f);
    }
}
