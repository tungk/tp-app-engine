package fr.unice.polytech.sacc.tp1;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import fr.unice.polytech.sacc.tp1.model.Article;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DataStoreEngine extends HttpServlet {

    public static String ARTICLE_KIND = "article";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Datastore datastore = DatastoreOptions.defaultInstance().service();
        KeyFactory keyFactory = datastore.newKeyFactory().kind(ARTICLE_KIND);
        IncompleteKey key = keyFactory.kind(ARTICLE_KIND).newKey();

        Article article = new Article("Crumble", 2.4, 1);
        Gson gson = new Gson();

        FullEntity<IncompleteKey> articleEntry = FullEntity.builder(key)
            .set("prop", gson.toJson(article)).build();
        datastore.add(articleEntry);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Datastore datastore = DatastoreOptions.defaultInstance().service();
        Query<Entity> query = Query.entityQueryBuilder().kind(ARTICLE_KIND).build();
        QueryResults<Entity> results = datastore.run(query);

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        while (results.hasNext()) {
            Entity entity = results.next();
            out.format("json: %s; key: %s", entity.getString("prop"), entity.key());
        }
    }

    /**
     * http://localhost:8080/datastore?key={key}
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyStr = request.getParameter("key");

        Datastore datastore = DatastoreOptions.defaultInstance().service();
        Query<Entity> query = Query.entityQueryBuilder().kind(ARTICLE_KIND).build();
        QueryResults<Entity> results = datastore.run(query);

        while (results.hasNext()) {
            Entity entity = results.next();
            Key key = entity.key();
            if (Integer.parseInt(keyStr) == key.id()) {
                datastore.delete(key);
            }
        }
    }
}
