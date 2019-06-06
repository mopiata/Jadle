package dao;

import static org.junit.Assert.*;

import com.sun.jdi.PrimitiveValue;
import models.Review;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.Arrays;

public class Sql2oReviewDaoTest {
    private Connection conn;
    private Sql2oReviewDao reviewDao;
    private Sql2oRestaurantDao restaurantDao;

    @Before
    public void setUp() throws Exception {
        String connectionString="jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o=new Sql2o(connectionString,"","");
        restaurantDao=new Sql2oRestaurantDao(sql2o);
        reviewDao=new Sql2oReviewDao(sql2o);
        conn=sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void timeStampIsReturnedCorrectly() throws Exception {
        Review testReview=setUpReview();
        reviewDao.add(testReview);

        long creationTime = testReview.getCreatedat();
        long savedTime=reviewDao.getAll().get(0).getCreatedat();

        String formattedCreationTime=testReview.getFormattedCreatedAt();
        String formattedSavedTime=reviewDao.getAll().get(0).getFormattedCreatedAt();
        assertEquals(formattedCreationTime, formattedSavedTime);
    }

    @Test
    public void reviewsAreReturnedInCorrectOrder() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("foodcoma!", "Captain Kirk", 3, testRestaurant.getId());
        reviewDao.add(testReview);

        try{
            Thread.sleep(2000);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testSecondReview = new Review("passable", "Mr Spock", 1, testRestaurant.getId());
        reviewDao.add(testSecondReview);

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testThirdReview = new Review("bloody good grub!", "Scotty", 4, testRestaurant.getId());
        reviewDao.add(testThirdReview);

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }

        Review testFourthReview = new Review("I prefer home cooking", "Mr. Sulu", 2, testRestaurant.getId());
        reviewDao.add(testFourthReview);

        assertEquals(4, reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()).size()); //it is important we verify that the list is the same size.
        assertEquals("I prefer home cooking", reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(testRestaurant.getId()).get(0).getContent());
    }

    //helpers

    public Restaurant setupRestaurant (){
        return new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
    }

    public Restaurant setupAltRestaurant (){
        return new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
    }

    public Review setUpReview(){
        Restaurant testRestaurant=setupRestaurant();
        restaurantDao.add(testRestaurant);
        return new Review("Awesome ambiance","Christine",4,testRestaurant.getId());
    }
}