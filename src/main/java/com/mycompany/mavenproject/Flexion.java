package com.mycompany.mavenproject;

/**
 *
 * @author Koutoulakis
 */
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.flexionmobile.codingchallenge.integration.Integration;
import com.flexionmobile.codingchallenge.integration.IntegrationTestRunner;
import com.flexionmobile.codingchallenge.integration.Purchase;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Flexion implements Integration{

    public static String BASE_URL = "http://sandbox.flexionmobile.com/javachallenge/rest/developer/";
    public static String DEV_ID = "ioanniskoutoulakis";
    public static String ALL = "all";
    public static String BUY = "buy";
    public static String CONSUME = "consume";
    // Using jackson and MrBeanModule so that we don't have to implement
    // the Purchase interface just for sending and receiving data.
    public ObjectMapper mapper = new ObjectMapper().registerModule(new MrBeanModule());

    public static void main(String[] args) throws Exception {
        System.out.println("Running tests...");
        IntegrationTestRunner tests = new IntegrationTestRunner();
        tests.runTests(new Flexion());
        System.out.println("Done.");
    }
    
    // Generates the url for all of our cases.
    public static String generateURL(String action, String item) {
        return BASE_URL + DEV_ID + "/" + action + "/" + item;
    }

    public Purchase postBuy (String url) {
        String responseString = this.doPost(url);
        try {
            return this.mapper.readValue(responseString, Purchase.class);
        } catch (IOException ex) {
            System.err.println("responseString was = " + responseString);
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
    public void postConsume (String url) {
        this.doPost(url);
    }

    public String doPost (String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException ex) {
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        HttpEntity resBody = response.getEntity();
        String responseString = null;

        try {
            responseString = EntityUtils.toString(resBody, "UTF-8");
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseString;
    }

    private String doGet(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException ex) {
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            JSONObject jsonObj = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
            if (jsonObj.has("purchases")) {
                return jsonObj.get("purchases").toString();
            } else {
                return null;
            }
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Purchase> getPurchases() {
        String url = generateURL(ALL, "");
        String responseString = this.doGet(url);
        
        List<Purchase> purchases = null;
        try {
            purchases = mapper.readValue(responseString, new TypeReference<List<Purchase>>(){});
        } catch (IOException ex) {
            Logger.getLogger(Flexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return purchases;
    }

    @Override
    public Purchase buy(String string) {
        String url = generateURL(BUY, string);
        return this.postBuy(url);
    }

    @Override
    public void consume(Purchase prchs) {
        if(prchs == null) {
            return;
        }
        String url = generateURL(CONSUME, prchs.getId());
        this.postConsume(url);
    }
}
