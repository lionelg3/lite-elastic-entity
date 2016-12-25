# The lite-elastic-entity project

The lite-elastic-entity project is an entityManager like for using ElasticSearch

## Build

    $ mvn clean package
    
## Requirements
    
* java 8
    
## Configuration    
    
Sample META-INF/elastic.json
    
    {
      "settings": {
        "client.transport.ignore_cluster_name": true,
        "client.transport.ping_timeout": "20s",
        "client.transport.nodes_sampler_interval": "10s",
        "client.transport.sniff": true,
        "cluster.name": "myClusterName"
      },
      "addresses": [
        "host1:9300",
        "host2:9300",
        "host3:9300"
      ],
      "entities": [
        "com.gmail.lionelg3.elastic.object.Article",
        "com.gmail.lionelg3.elastic.object.Book",
        "com.gmail.lionelg3.elastic.object.Author"
      ]
    }
    
The configuration file can be an external json file. Example: 

META-INF/elastic.json    
    
    {
      "files": [
        "src/test/resources/META-INF/elastic_wrong.json",
        "src/test/resources/META-INF/elastic.json"
      ]
    }
    
## Usage

### obtaine the ElasticManager

    ElasticManager em = ElasticManager.getInstance();

### Insert Object

    Article a1 = new Article();
    ...
    em.persist(a1);
    // or
    em.persist(a1, (article) -> {
        System.out.println("Insert OK");
    });
    
### Load Object 
    
    Article article = em.find(Article.class, id);
    // or
    em.find(a2.getId(), Article.class, (article) -> {
        System.out.println("load ok " + article);
    });
    
### Update Object 

    Article article = em.merge(a1);
    // or
    em.merge(a2, (article) -> {
        System.out.println("update ok " + article);
    });
    
### Delete Object

    em.remove(a1);
    // or
    em.remove(a2, (id) -> {
        System.out.println("Delete article " + id);
    });

### Fetch all Objects
   
    List<Article> articles = em.fetchAll(Article.class);
    // or
    em.fetchAll(Article.class, articles -> {
        articles.forEach(System.out::println);
    });
 
### Fetch with constraints 

    HashMap<String, Object> constraints = new HashMap<>();
    constraints.put("texte", "Sample EM");
    constraints.put("titre", "Titre EM");
    List<Article> articles = em.search(constraints, Article.class);
    // or
    em.search(constraints, Article.class, articles -> {
        articles.forEach(System.out::println);
    });
    
### Fetch with JSON

    String q = "{\n" +
        "  \"match\" : {\n" +
        "    \"texte\" : {\n" +
        "      \"query\" : \"Sample EM\"\n" +
        "    }\n" +
        "  }\n" +
        "}";
    List<Article> articles = em.search(q, Article.class);
    // or
    em.search(q, Article.class, (articles) -> {
        articles.forEach(System.out::println);
    });