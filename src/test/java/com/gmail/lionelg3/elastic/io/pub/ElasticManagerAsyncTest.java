package com.gmail.lionelg3.elastic.io.pub;

import com.gmail.lionelg3.elastic.io.ElasticManager;
import com.gmail.lionelg3.elastic.object.Article;
import org.elasticsearch.index.query.QueryBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by lionel on 27/11/2016.
 *
 */
public class ElasticManagerAsyncTest {
    private static final int PAUSE = 500;

    Article a2 = null;
    ElasticManager em;
    boolean pass = false;

    @BeforeClass
    public void init() throws UnknownHostException {
        a2 = new Article();
        a2.setId("" + System.currentTimeMillis());
        a2.setTexte("Sample EM Async");
        a2.setTitre("Titre EM Async");
        a2.setOrdre(100);
        a2.setDate(new Date());
        a2.setEtat(Article.Etat.MASQUE);

        System.setProperty("elastic.config.file", "META-INF/elastic_sample.json");
        em = ElasticManager.getInstance();
    }

    @AfterMethod
    public void pause() throws InterruptedException {
        Thread.sleep(PAUSE);
        System.out.println("#################################################################");
    }

    @Test
    public void insert() throws InterruptedException {
        pass = false;
        em.persist(a2, (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getId(), a2.getId());
            System.out.println("Insert OK " + article.getId() + " -> " + article);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "insert")
    public void load() throws InterruptedException {
        pass = false;
        em.find(a2.getId(), Article.class, (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getTitre(), a2.getTitre());
            Assert.assertEquals(article.getTexte(), a2.getTexte());
            System.out.println("load ok " + article);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "load")
    public void update() throws InterruptedException {
        pass = false;
        a2.setEtat(Article.Etat.ARCHIVE);
        em.merge(a2, (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getEtat(), a2.getEtat());
            System.out.println("update ok " + article);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "update")
    public void search_QueryBuilder() throws InterruptedException {
        pass = false;
        QueryBuilder q = matchQuery(
                "texte",
                "Sample"
        );
        System.out.println("QUERY = " + q);
        em.search(q, Article.class, (articles) -> {
            articles.forEach(System.out::println);
            Assert.assertEquals(articles.size(), 1);
            articles.stream()
                    .map(Article::getId)
                    .forEach(System.out::println);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "search_QueryBuilder")
    public void search_json() throws InterruptedException {
        pass = false;
        String q = "{\n" +
                "  \"match\" : {\n" +
                "    \"texte\" : {\n" +
                "      \"query\" : \"Sample\"\n" +
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
        em.search(q, Article.class, (articles) -> {
            articles.forEach(System.out::println);
            articles.stream()
                    .map(Article::getId)
                    .forEach(System.out::println);
            Assert.assertEquals(articles.size(), 1);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "search_json")
    public void search_Constraints() throws InterruptedException {
        HashMap<String, Object> constraints = new HashMap<>();
        constraints.put("texte", "Sample EM");
        constraints.put("titre", "Titre EM");
        System.out.println("QUERY = " + constraints);
        em.search(constraints, Article.class, articles -> {
            articles.forEach(System.out::println);
            Assert.assertEquals(articles.size(), 1);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "search_Constraints")
    public void fetchAll() throws InterruptedException {
        em.fetchAll(Article.class, articles -> {
            Assert.assertEquals(articles.size(), 1);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "fetchAll")
    public void delete() throws InterruptedException {
        pass = false;
        em.remove(a2, (id) -> {
            Assert.assertNotNull(id);
            System.out.println("Delete article " + id);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }

    @Test(dependsOnMethods = "delete")
    public void fetchEmpty() throws InterruptedException {
        em.fetchAll(Article.class, articles -> {
            Assert.assertEquals(articles.size(), 0);
            pass = true;
        });
        Thread.sleep(PAUSE);
        Assert.assertTrue(pass);
    }
}