package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.*;

import static org.junit.Assert.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

public class Sql2oRestaurantDaoTest {
    private static Connection conn;
    private static Sql2oRestaurantDao restaurantDao;
    private static Sql2oFoodtypeDao foodtypeDao;
    private static Sql2oReviewDao reviewDao;

    @BeforeClass
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/jadle_test";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("clearing database");
        restaurantDao.clearAll(); //clear all restaurants after every test
        foodtypeDao.clearAll(); //clear all restaurants after every test
        reviewDao.clearAll(); //clear all restaurants after every test
    }

    @AfterClass
    public static void shutDown() throws Exception{
        conn.close(); // close connection once after this entire test file is finished
        System.out.println("connection closed");
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertNotEquals(0, testRestaurant.getId());
    }

    @Test
    public void addedRestaurantsAreReturnedFromGetAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void noRestaurantsReturnsEmptyList() throws Exception {
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void findByIdReturnsCorrectRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        assertEquals(testRestaurant, restaurantDao.findById(testRestaurant.getId()));
    }

    @Test
    public void updateCorrectlyUpdatesAllFields() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.update(testRestaurant.getId(), "a", "b", "c", "d", "e", "f");
        Restaurant foundRestaurant = restaurantDao.findById(testRestaurant.getId());
        assertEquals("a", foundRestaurant.getName());
        assertEquals("b", foundRestaurant.getAddress());
        assertEquals("c", foundRestaurant.getZipcode());
        assertEquals("d", foundRestaurant.getPhone());
        assertEquals("e", foundRestaurant.getWebsite());
        assertEquals("f", foundRestaurant.getEmail());
    }

    @Test
    public void deleteByIdDeletesCorrectRestaurant() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(1, restaurantDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        Restaurant otherRestaurant = setupRestaurant();
        restaurantDao.clearAll();
        assertEquals(0, restaurantDao.getAll().size());
    }

    @Test
    public void RestaurantReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype=setupNewFoodtype();
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype=otherFoodtype();
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant=setupRestaurant();
        restaurantDao.add(testRestaurant);

        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(testRestaurant,otherFoodtype);

        Foodtype[] foodtypes={testFoodtype,otherFoodtype};

        assertEquals(Arrays.asList(foodtypes),restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()));
    }

    @Test
    public void deleteingFoodtypeAlsoUpdatesJoinTable() throws Exception {
        Foodtype foodtype=setupNewFoodtype();
        Foodtype otherFoodtype=otherFoodtype();

        foodtypeDao.add(foodtype);
        foodtypeDao.add(otherFoodtype);

        Restaurant restaurant=setupRestaurant();
        restaurantDao.add(restaurant);

        foodtypeDao.addFoodtypeToRestaurant(otherFoodtype,restaurant);
        foodtypeDao.addFoodtypeToRestaurant(foodtype,restaurant);

        foodtypeDao.deleteById(foodtype.getId());
        foodtypeDao.deleteById(otherFoodtype.getId());

        assertEquals(0,foodtypeDao.getAllRestaurantsForAFoodtype(foodtype.getId()).size());
    }

    //helpers

    public Restaurant setupRestaurant (){
        return new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
    }

    public Restaurant setupAltRestaurant (){
        return new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
    }

    public Foodtype setupNewFoodtype(){
        return new Foodtype("Sushi");
    }

    public Foodtype otherFoodtype() {
        return new Foodtype("vegan");
    }

}
