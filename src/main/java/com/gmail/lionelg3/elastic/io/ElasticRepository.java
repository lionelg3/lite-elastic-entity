package com.gmail.lionelg3.elastic.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.WrapperQueryBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by lionel on 21/11/2016.
 *
 */
class ElasticRepository<T> {

    private final ElasticAccess _access;
    private final ObjectMapper _mapper;
    private final Class<T> _clazz;
    private String _index;
    private String _type;
    private Client _client;

    ElasticRepository(ElasticAccess access, Class<T> clazz) {
        this._access = access;
        this._mapper = new ObjectMapper();
        this._clazz = clazz;
        this._index = clazz.getPackage().getName();
        this._type = clazz.getSimpleName().toLowerCase();
        if (clazz.isAnnotationPresent(XmlRootElement.class)) {
            XmlRootElement annotation = clazz.getAnnotation(XmlRootElement.class);
            if (!annotation.namespace().contains("default"))
                this._index = annotation.namespace();
            if (!annotation.name().contains("default"))
                this._type = annotation.name();
        }
    }

    private String _map(T t) {
        try {
            return _mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Map error for " + this._clazz, e);        }
    }

    @SuppressWarnings("unchecked")
    private T _unmap(String source) {
        try {
            return (T) _mapper.readValue(source, _clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unmap error for " + this._clazz, e);
        }
    }
    
    public synchronized Client getClient() {
        return (_client != null) ? _client : (Client) _access.getNewClient();
    }

    public String insert(String id, T t) {
        IndexResponse response = getClient().prepareIndex(_index, _type, id)
                .setSource(_map(t)).get();
        return response.getId();
    }

    public T load(String id) {
        GetResponse response = getClient().prepareGet(_index, _type, id).get();
        return _unmap(response.getSourceAsString());
    }

    public String delete(String id) {
        DeleteResponse deleteResponse = getClient().prepareDelete(_index, _type, id)
                .get();
        return deleteResponse.getId();
    }

    public T update(String id, T t) {
        getClient().prepareUpdate(_index, _type, id)
                .setDoc(_map(t))
                .get();
        return t;
    }

    public List<T> search(QueryBuilder queryBuilder) {
        SearchRequestBuilder searchRequestBuilder = getClient()
                .prepareSearch(_index)
                .setTypes(_type)
                .setQuery(queryBuilder);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        List<T> results = new ArrayList<>();
        response.getHits().iterator()
                .forEachRemaining(h -> {
                    results.add(_unmap(h.getSourceAsString()));
                });
        return results;
    }

    public List<T> search(String query) {
        SearchRequestBuilder searchRequestBuilder = getClient()
                .prepareSearch(_index)
                .setTypes(_type)
                .setQuery(new WrapperQueryBuilder(query));
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        List<T> results = new ArrayList<>();
        response.getHits().iterator()
                .forEachRemaining(h -> {
                    results.add(_unmap(h.getSourceAsString()));
                });
        return results;
    }

    public void insert(String id, T t, Consumer<T> f) {
        getClient().prepareIndex(_index, _type, id)
                .setSource(_map(t))
                .execute(new ActionListener<IndexResponse>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        f.accept(t);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Err insert " + id, e);
                    }
                });
    }

    public void load(String id, Consumer<T> f) {
        getClient().prepareGet(_index, _type, id)
                .execute(new ActionListener<GetResponse>() {
                    @Override
                    public void onResponse(GetResponse getResponse) {
                        T t = _unmap(getResponse.getSourceAsString());
                        f.accept(t);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Err load " + id, e);
                    }
                });
    }

    public void delete(String id, Consumer<String> f) {
        getClient().prepareDelete(_index, _type, id)
                .execute(new ActionListener<DeleteResponse>() {
                    @Override
                    public void onResponse(DeleteResponse deleteResponse) {
                        f.accept(id);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Err delete " + id, e);
                    }
                });
    }

    public void update(String id, T t, Consumer<T> f) {
        getClient().prepareUpdate(_index, _type, id)
                .setDoc(_map(t))
                .execute(new ActionListener<UpdateResponse>() {
                    @Override
                    public void onResponse(UpdateResponse updateResponse) {
                        f.accept(t);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Err update " + id, e);
                    }
                });
    }

    public void search(QueryBuilder queryBuilder, Consumer<List<T>> f) {
        SearchRequestBuilder searchRequestBuilder = getClient()
                .prepareSearch(_index)
                .setTypes(_type)
                .setQuery(queryBuilder);
        searchRequestBuilder.execute(new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<T> results = new ArrayList<>();
                response.getHits().iterator()
                        .forEachRemaining(h -> {
                            results.add(_unmap(h.getSourceAsString()));
                        });
                f.accept(results);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Err search " + queryBuilder.toString(), e);
            }
        });
    }

    public void search(String query, Consumer<List<T>> f) {
        SearchRequestBuilder searchRequestBuilder = getClient()
                .prepareSearch(_index)
                .setTypes(_type)
                .setQuery(new WrapperQueryBuilder(query));
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        List<T> results = new ArrayList<>();
        response.getHits().iterator()
                .forEachRemaining(h -> {
                    results.add(_unmap(h.getSourceAsString()));
                });
        f.accept(results);
    }
}
