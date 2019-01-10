package com.rolnik.alcoholapp.rests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rolnik.alcoholapp.clientservices.AlcoholClientService;
import com.rolnik.alcoholapp.dto.Alcohol;
import com.rolnik.alcoholapp.testutils.PlainModelCreator;
import com.rolnik.alcoholapp.testutils.RetrofitTestCreator;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.TestObserver;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.fail;

public class AlcoholDaoServiceTest {
    private List<Alcohol> alcohols;
    private MockWebServer mockWebServer;
    private TestObserver<List<Alcohol>> listTestObserver;
    private TestObserver<Alcohol> testObserver;
    private ObjectWriter objectWriter;
    private AlcoholClientService alcoholClientService;

    @Before
    public void setUp(){
        PlainModelCreator plainModelCreator = new PlainModelCreator();
        alcohols = new ArrayList<>();
        mockWebServer = new MockWebServer();
        listTestObserver = new TestObserver<>();
        testObserver = new TestObserver<>();
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        alcohols.add(plainModelCreator.createAlcohol(1));
        alcohols.add(plainModelCreator.createAlcohol(2));
        alcohols.add(plainModelCreator.createAlcohol(3));
    }

    @Test
    public void getAll(){
        mockWebServer.enqueue(new MockResponse().setBody(objectToJson(alcohols)));

        AlcoholRest alcoholDao = RetrofitTestCreator.createTestRest(AlcoholRest.class, mockWebServer);

        alcoholClientService = new AlcoholClientService(alcoholDao);

        alcoholClientService.getAll().subscribe(listTestObserver);

        listTestObserver.assertNoErrors();
    }

    @Test
    public void get(){
        mockWebServer.enqueue(new MockResponse().setBody(objectToJson(alcohols.get(0))));

        AlcoholRest alcoholDao = RetrofitTestCreator.createTestRest(AlcoholRest.class, mockWebServer);

        alcoholClientService = new AlcoholClientService(alcoholDao);

        alcoholClientService.get(0).subscribe(testObserver);

        testObserver.assertNoErrors();
    }



    private String objectToJson(Object object){
        try {
            return objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail();
            return "";
        }
    }

}