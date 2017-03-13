package com.gmail.lionelg3.elastic.io;

import com.gmail.lionelg3.elastic.object.Article;
import com.gmail.lionelg3.elastic.object.Book;
import com.gmail.lionelg3.elastic.object.HomePage;
import com.gmail.lionelg3.elastic.object.Product;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by lionel on 29/11/2016.
 *
 */
public class ElasticIdResolverTest {

    private ElasticIdResolver idResolver = new ElasticIdResolver();

    @Test
    public void useGetIdMethod() {
        Article a1 = new Article();
        a1.setId("" + System.currentTimeMillis());

        String id = idResolver.getId(a1);

        Assert.assertNotNull(id);
        System.out.println("id = " + id);
    }

    @Test
    public void useXmlIDAnnotatedMethod() {
        Book b1 = new Book();
        b1.setIsbn("" + System.currentTimeMillis());

        String id = idResolver.getId(b1);

        Assert.assertNotNull(id);
        System.out.println("id = " + id);
    }

    @Test
    public void useXmlIDAnnotateAttribute() {
        HomePage p1 = new HomePage();
        p1.setUrl("https://github.com/lionelg3/");
        p1.setName("Lionel G3 - homepage");

        String id = idResolver.getId(p1);

        Assert.assertNotNull(id);
        System.out.println("id = " + id);
    }

    @Test
    public void useIdAnnotateAttribute() {
        Product p1 = new Product();
        p1.setOid("123456");
        p1.setName("A new Hope");
        p1.setDescription("A long time ago");

        String id = idResolver.getId(p1);

        Assert.assertNotNull(id);
        Assert.assertEquals("123456", id);
        System.out.println("id = " + id);
    }

    @Test
    public void failIdResolution() {
        String c1 = "Sample";

        String id = idResolver.getId(c1);

        Assert.assertNull(id);
        System.out.println("id = " + id);
    }
}
