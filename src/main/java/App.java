import com.google.gson.Gson;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import exceptions.ApiException;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        Sql2oFoodtypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson=new Gson();

        String connectionString="jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o=new Sql2o(connectionString,"","");

        restaurantDao=new Sql2oRestaurantDao(sql2o);
        foodtypeDao=new Sql2oFoodtypeDao(sql2o);
        reviewDao=new Sql2oReviewDao(sql2o);
        conn=sql2o.open();

        post("/restaurants/new", "application/json", (request, response) -> {
            Restaurant restaurant=gson.fromJson(request.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            response.status(201);
            return  gson.toJson(restaurant);
        });

        get("/restaurants","application/json",(request, response) -> {
            if(!(restaurantDao.getAll().size()==0)) {
                return gson.toJson(restaurantDao.getAll());
            }
            else {return "{\"message\":\"I'm sorry, but no restaurants are currently listed in the database.\"}";}
        });

        get("/restaurants/:id", "application/json",(request, response) -> {
           int restaurantId=Integer.parseInt(request.params("id"));

           Restaurant restaurantToFind=restaurantDao.findById(restaurantId);

           if(restaurantToFind==null){
               throw new ApiException(404, String.format("No restaurant with the id: \"%s\"exists",request.params()));
           }
           return gson.toJson(restaurantDao.findById(restaurantId));
        });

        post("/restaurants/:restaurantId/reviews/new", "application/json", (request,response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setCreatedat();
            review.setFormattedCreatedAt();
            review.setRestaurantId(restaurantId);
            reviewDao.add(review);
            response.status(201);
            return gson.toJson(review);
        });

        get("/restaurants/:id/sortedReviews","application/json",(request, response) -> {
            int restaurantId=Integer.parseInt(request.params("id"));
            Restaurant restaurantToFind=restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            if(restaurantToFind==null){
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", request.params("id")));
            }

            allReviews=reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(restaurantId);

            return gson.toJson(allReviews);
        });

        post("/restaurants/:restaurantId/foodtype/:foodtypeId","application/json",(request,response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            int foodtypeId = Integer.parseInt(request.params("foodtypeId"));
            Restaurant restaurant =restaurantDao.findById(restaurantId);

            Foodtype foodtype = foodtypeDao.findById(foodtypeId);

//            if(restaurant !=null && foodtype != null){
                foodtypeDao.addFoodtypeToRestaurant(foodtype,restaurant);
                response.status(201);
                return gson.toJson(String.format("Restaurant '%s' and Foodtype '%s' have been associated", restaurant.getName(),foodtype.getName()));
//            }
//            else{
//                throw new  ApiException(404,String.format("Restaurant or Foodtype does not exist"));
//            }
        });


        get("/restaurants/:id/foodtypes", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null){
                // throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
                System.out.println(String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
                return null;
            }
            else if (restaurantDao.getAllFoodtypesByRestaurant(restaurantId).size()==0){
                return "{\"message\":\"I'm sorry, but no foodtypes are listed for this restaurant.\"}";
            }
            else {
                return gson.toJson(restaurantDao.getAllFoodtypesByRestaurant(restaurantId));
            }
        });

        get("/foodtypes/:id/restaurants", "application/json", (req, res) -> {
            int foodtypeId = Integer.parseInt(req.params("id"));
            Foodtype foodtypeToFind = foodtypeDao.findById(foodtypeId);
//            if (foodtypeToFind == null){
//                throw new ApiException(404, String.format("No foodtype with the id: \"%s\" exists", req.params("id")));
//            }
//            else if (foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId).size()==0){
//                return "{\"message\":\"I'm sorry, but no restaurants are listed for this foodtype.\"}";
//            }
//            else {
                return gson.toJson(foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId));
//            }
        });


        post("/foodtypes/new", "application/json",(request,response) -> {
            Foodtype foodtype =gson.fromJson(request.body(),Foodtype.class);
            foodtypeDao.add(foodtype);
            response.status(201);
            return gson.toJson(foodtype);
        });

        get("/foodtypes" ,"application/json",(request, response) -> {
            return  gson.toJson(foodtypeDao.getAll());
        });

        get("/foodtypes/:id", "application/json",(request, response) -> {
            int foodtypeId = Integer.parseInt(request.params("id"));
            return gson.toJson(foodtypeDao.findById(foodtypeId));
        });

        //FILTERS
        exception(ApiException.class, (exc,req,res)->{
            ApiException err = (ApiException) exc;
            Map<String, Object> jsonmap = new HashMap<>();
            jsonmap.put("status",err.getStatusCode());
            jsonmap.put("errorMessage", err.getMessage());
            res.type("application/json"); //after doesn't run in case of an exception
            res.status(err.getStatusCode());
            res.body(gson.toJson(jsonmap));
        });

        after((request, response) ->{
            response.type("application/json");
        } );
    }
}
