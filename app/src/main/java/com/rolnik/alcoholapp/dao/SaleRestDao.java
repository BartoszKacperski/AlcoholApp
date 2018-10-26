package com.rolnik.alcoholapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rolnik.alcoholapp.model.Sale;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import java.text.MessageFormat;
import java.util.List;

public class SaleRestDao implements Dao<Sale> {
    private static SaleRestDao INSTANCE = null;

    private static final String GET_ALL_ADDRESS = RestService.BASE_ADDRESS + "/sales";
    private static final String POST_ADD_OBJECT = GET_ALL_ADDRESS;

    private RestService<Sale> restService;

    private SaleRestDao() {
        restService = new RestService<>(Sale.class);
    }

    public synchronized static SaleRestDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SaleRestDao();
        }

        return INSTANCE;
    }

    @Override
    public List<Sale> getAll() throws HttpClientErrorException {
        return restService.getForAll(GET_ALL_ADDRESS, new ParameterizedTypeReference<List<Sale>>() {
        });
    }

    @Override
    public Sale get(int id) throws HttpClientErrorException {
        return restService.getForObject(GET_ALL_ADDRESS, id);
    }

    @Override
    public Integer add(Sale object) throws HttpClientErrorException {
        return restService.postAddObject(POST_ADD_OBJECT, object);
    }

    @Override
    public void update(Sale object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.putUpdate(URL, object);
    }

    @Override
    public void remove(Sale object) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/" + object.getId();
        restService.deleteRemove(URL, object);
    }

    public List<Sale> getAllWhere(int shopId, int kindId) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + MessageFormat.format("/shop/{0}/kind/{1}", shopId, kindId);

        return restService.getForAll(URL, new ParameterizedTypeReference<List<Sale>>() {});
    }

    public List<Sale> getAllWhereShop(int shopId) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + MessageFormat.format("/shop/{0}", shopId);

        return restService.getForAll(URL, new ParameterizedTypeReference<List<Sale>>() {});
    }

    public List<Sale> getAllWhereKind(int kindId) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + MessageFormat.format("/kind/{0}", kindId);

        return restService.getForAll(URL, new ParameterizedTypeReference<List<Sale>>() {});
    }

    public List<Sale> getUserSales(int userId) throws HttpClientErrorException {
        String URL = GET_ALL_ADDRESS + "/user/" + userId;

        return restService.getForAll(URL, new ParameterizedTypeReference<List<Sale>>() {});
    }
}
