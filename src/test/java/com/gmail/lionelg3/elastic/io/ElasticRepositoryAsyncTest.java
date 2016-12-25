package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.object.Article;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by lionel on 22/11/2016
 *
 */
public class ElasticRepositoryAsyncTest {
    private static final int PAUSE = 500;

    ElasticAccess elasticAccess;
    ElasticRepository<Article> articleRepository;

    Article a2 = null;

    @BeforeClass
    public void init() throws UnknownHostException {
        elasticAccess = new ElasticAccess(Settings.EMPTY, new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        articleRepository = new ElasticRepository<>(elasticAccess, Article.class);

        a2 = new Article();
        a2.setId("" + System.currentTimeMillis());
        a2.setTexte("Sample");
        a2.setTitre("Titre 200");
        a2.setOrdre(100);
        a2.setDate(new Date());
        a2.setEtat(Article.Etat.MASQUE);
    }

    @AfterMethod
    public void pause() throws InterruptedException {
        Thread.sleep(PAUSE);
        System.out.println("#################################################################");
    }

    @Test()
    public void insert() {
        articleRepository.insert(a2.getId(), a2, (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getId(), a2.getId());
            System.out.println("Insert OK " + article.getId() + " -> " + article);
        });
    }

    @Test(dependsOnMethods = "insert")
    public void load() {
        articleRepository.load(a2.getId(), (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getTitre(), a2.getTitre());
            Assert.assertEquals(article.getTexte(), a2.getTexte());
            System.out.println("load ok " + article);
        });

    }

    @Test(dependsOnMethods = "load")
    public void update() {
        a2.setEtat(Article.Etat.AFFICHE);
        articleRepository.update(a2.getId(), a2, (article) -> {
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getEtat(), a2.getEtat());
            System.out.println("update ok " + article);
        });
    }

    @Test(dependsOnMethods = "update")
    public void search_QueryBuilder() {
        QueryBuilder q = matchQuery(
                "texte",
                "Sample"
        );
        System.out.println("QUERY = " + q);
        articleRepository.search(q, (articles) -> {
            articles.forEach(System.out::println);
            articles.stream()
                    .map(Article::getId)
                    .forEach(System.out::println);
        });
    }

    @Test(dependsOnMethods = "search_QueryBuilder")
    public void search_json() {
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
        articleRepository.search(q, (articles) -> {
            articles.forEach(System.out::println);
            articles.stream()
                    .map(Article::getId)
                    .forEach(System.out::println);
        });
    }

    @Test(dependsOnMethods = "search_json")
    public void delete() {
        articleRepository.delete(a2.getId(), (id) -> {
            Assert.assertNotNull(id);
            System.out.println("Delete article " + id);
        });
    }
}
