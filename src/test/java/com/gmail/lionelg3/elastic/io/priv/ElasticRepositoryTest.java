package com.gmail.lionelg3.elastic.io.priv;

import com.gmail.lionelg3.elastic.io.ElasticAccess;
import com.gmail.lionelg3.elastic.io.ElasticRepository;
import com.gmail.lionelg3.elastic.io.server.EmbeddedElasticSearchServer;
import com.gmail.lionelg3.elastic.object.Article;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * Created by lionel on 22/11/2016.
 *
 */
@Listeners(EmbeddedElasticSearchServer.class)
public class ElasticRepositoryTest {
    private static final int PAUSE = 50;

    ElasticAccess elasticAccess;
    ElasticRepository<Article> articleRepository;

    Article a1 = null;

    @BeforeClass
    public void init() throws UnknownHostException {
        elasticAccess = new ElasticAccess(Settings.EMPTY, new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        articleRepository = new ElasticRepository<>(elasticAccess, Article.class);

        a1 = new Article();
        a1.setId("" + System.currentTimeMillis());
        a1.setTexte("Sample");
        a1.setTitre("Exemple");
        a1.setOrdre(100);
        a1.setDate(new Date());
        a1.setEtat(Article.Etat.MASQUE);
    }

    @AfterMethod
    public void pause() throws InterruptedException {
        Thread.sleep(PAUSE);
        System.out.println("#################################################################");
    }

    @Test()
    public void insert() {
        String id = articleRepository.insert(a1.getId(), a1);
        System.out.println("insert ok = " + id);
        Assert.assertEquals(id, a1.getId());
    }

    @Test(dependsOnMethods = "insert")
    public void load() {
        Article article = articleRepository.load(a1.getId());
        System.out.println("load ok " + article);
        Assert.assertEquals(article.getTitre(), a1.getTitre());
        Assert.assertEquals(article.getTexte(), a1.getTexte());
    }

    @Test(dependsOnMethods = "load")
    public void update() {
        a1.setEtat(Article.Etat.ARCHIVE);
        Article article = articleRepository.update(a1.getId(), a1);
        System.out.println("update ok " + article);
        Assert.assertEquals(article.getEtat(), a1.getEtat());
    }

    @Test(dependsOnMethods = "update")
    public void search_QueryBuilder() {
        QueryBuilder q = matchQuery(
                "texte",
                "Sample"
        );
        System.out.println("QUERY = " + q);
        List<Article> articles = articleRepository.search(q);
        articles.forEach(System.out::println);
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
        List<Article> articles = articleRepository.search(q);
        articles.forEach(System.out::println);
    }

    @Test(dependsOnMethods = "search_json")
    public void delete() {
        articleRepository.delete(a1.getId());
    }
}
