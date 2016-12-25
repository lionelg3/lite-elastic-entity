package com.gmail.lionelg3.elastic.io.pub;

import com.gmail.lionelg3.elastic.io.ElasticManager;
import com.gmail.lionelg3.elastic.object.Article;
import org.elasticsearch.index.query.QueryBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticManagerTest {

    Article a1 = null;
    ElasticManager em;

    @BeforeClass
    public void init() throws UnknownHostException {
        a1 = new Article();
        a1.setId("" + System.currentTimeMillis());
        a1.setTexte("Sample EM");
        a1.setTitre("Titre EM");
        a1.setOrdre(100);
        a1.setDate(new Date());
        a1.setEtat(Article.Etat.MASQUE);

        System.setProperty("elastic.config.file", "META-INF/elastic_sample.json");
        em = ElasticManager.getInstance();
    }

    @Test
    public void insert() {
        String id = em.persist(a1);
        Assert.assertEquals(id, a1.getId());
    }

    @Test(dependsOnMethods = "insert")
    public void load() {
        Article article = em.find(Article.class, a1.getId());
        System.out.println("load ok " + article);
        Assert.assertEquals(article.getTitre(), a1.getTitre());
        Assert.assertEquals(article.getTexte(), a1.getTexte());
    }

    @Test(dependsOnMethods = "load")
    public void update() {
        a1.setEtat(Article.Etat.ARCHIVE);
        a1.setOrdre(101);
        Article article = em.merge(a1);
        System.out.println("update ok " + article);
        Assert.assertEquals(article.getEtat(), a1.getEtat());
    }

    @Test(dependsOnMethods = "update")
    public void search_QueryBuilder() {
        QueryBuilder q = matchQuery(
                "texte",
                "Sample EM"
        );
        System.out.println("QUERY = " + q);
        List<Article> articles = em.search(q, Article.class);
        articles.forEach(System.out::println);
        Assert.assertEquals(articles.size(), 1);
    }

    @Test(dependsOnMethods = "search_QueryBuilder")
    public void search_json() {
        String q = "{\n" +
                "  \"match\" : {\n" +
                "    \"texte\" : {\n" +
                "      \"query\" : \"Sample EM\"\n" +
                //"      \"query\" : \"Sample\",\n" +
                //"      \"operator\" : \"OR\",\n" +
                //"      \"prefix_length\" : 0,\n" +
                //"      \"max_expansions\" : 50,\n" +
                //"      \"fuzzy_transpositions\" : true,\n" +
                //"      \"lenient\" : false,\n" +
                //"      \"zero_terms_query\" : \"NONE\",\n" +
                //"      \"boost\" : 1.0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        System.out.println("QUERY = " + q);
        List<Article> articles = em.search(q, Article.class);
        articles.forEach(System.out::println);
        Assert.assertEquals(articles.size(), 1);
    }

    @Test(dependsOnMethods = "search_json")
    public void search_Constraints() {
        HashMap<String, Object> constraints = new HashMap<>();
        constraints.put("texte", "Sample EM");
        constraints.put("titre", "Titre EM");
        System.out.println("QUERY = " + constraints);
        List<Article> articles = em.search(constraints, Article.class);
        articles.forEach(System.out::println);
        Assert.assertEquals(articles.size(), 1);
    }

    @Test(dependsOnMethods = "search_Constraints")
    public void fetchAll() {
        List<Article> articles = em.fetchAll(Article.class);
        Assert.assertEquals(articles.size(), 1);
    }

    @Test(dependsOnMethods = "fetchAll")
    public void delete() throws IOException {
        em.remove(a1);
    }
}