package dao;

import models.Foodtype;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {
    private Sql2oFoodtypeDao foodtypeDao;
    private Sql2oRestaurantDao restaurantDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        int originalFoodtypeId = testFoodtype.getId();
        foodtypeDao.add(testFoodtype);
        assertNotEquals(originalFoodtypeId,testFoodtype.getId());
    }

    @Test
    public void addedFoodtypesAreReturnedFromGetAll() throws Exception {
        Foodtype testfoodtype = setupNewFoodtype();
        foodtypeDao.add(testfoodtype);
        assertEquals(1, foodtypeDao.getAll().size());
    }

    @Test
    public void noFoodtypesReturnsEmptyList() throws Exception {
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void deleteByIdDeletesCorrectFoodtype() throws Exception {
        Foodtype foodtype = setupNewFoodtype();
        foodtypeDao.add(foodtype);
        foodtypeDao.deleteById(foodtype.getId());
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        Foodtype otherFoodtype = setupNewFoodtype();
        foodtypeDao.clearAll();
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {
        Restaurant testRestaurant= setUpNewRestaurant();
        Restaurant altRestaurant=setUpAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype=setupNewFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype,testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype,altRestaurant);

        assertEquals(2,foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }

    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype=setupNewFoodtype();
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant=setUpNewRestaurant();
        Restaurant altRestaurant=setUpAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(altRestaurant,testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());

        assertEquals(0, restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()).size());
    }

    // helpers

    public Foodtype setupNewFoodtype(){
        return new Foodtype("Sushi");
    }

    public Restaurant setUpNewRestaurant() {
        return new Restaurant("Carnivore","456 Nairobi","00344","1234567890","res@res.com","res@res.com");
    }
    public Restaurant setUpAltRestaurant() {
        return new Restaurant("Panari","456637 Nairobi","00300","1267890","panari@res.com","panari@res.com");
    }
}