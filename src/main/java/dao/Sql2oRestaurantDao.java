package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.*;

import java.util.ArrayList;
import java.util.List;

public class Sql2oRestaurantDao implements RestaurantDao {
    private Sql2o sql2o;

    public Sql2oRestaurantDao(Sql2o sql2o){
        this.sql2o=sql2o;
    }

    @Override
    public void add (Restaurant restaurant){
        String sql="INSERT INTO restaurants (name, address, zipcode, phone, website, email) VALUES (:name,:address, :zipcode, :phone, :website, :email);";

        try(Connection con=sql2o.open()){
            int id = (int) con.createQuery(sql,true)
                    .bind(restaurant)
                    .executeUpdate()
                    .getKey();

            restaurant.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Restaurant> getAll(){
        String sql="SELECT * FROM restaurants";

        try(Connection con=sql2o.open()){
            return con.createQuery(sql)
                    .executeAndFetch(Restaurant.class);
        }
    }

    @Override
    public Restaurant findById(int id){
        String sql="SELECT * FROM restaurants WHERE id=:id";

        try(Connection con=sql2o.open()){
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Restaurant.class);
        }
    }

    @Override
    public void update(int id, String name, String address, String zipcode, String phone, String website, String email){
        String sql="UPDATE restaurants SET name=:name, address=:address, zipcode=:zipcode, phone=:phone, website=:website, email=:email WHERE id=:id;";

        try(Connection con=sql2o.open()){
            con.createQuery(sql)
                    .addParameter("id",id)
                    .addParameter("name",name)
                    .addParameter("address",address)
                    .addParameter("zipcode",zipcode)
                    .addParameter("phone",phone)
                    .addParameter("website",website)
                    .addParameter("email", email)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id){
        String sql="DELETE FROM restaurants WHERE id=:id;";
        String deleteJoin = "DELETE from restaurants_foodtypes WHERE restaurantid = :restaurantId";

        try(Connection con=sql2o.open()){
            con.createQuery(sql)
                    .addParameter("id",id)
                    .executeUpdate();
            con.createQuery(deleteJoin)
                    .addParameter("restaurantId",id)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void clearAll(){
        String sql="DELETE FROM restaurants;";

        try(Connection con=sql2o.open()){
            con.createQuery(sql)
                    .executeUpdate();
        }catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public void addRestaurantToFoodtype(Restaurant restaurant, Foodtype foodtype){
        String sql = "INSERT INTO restaurants_foodtypes (restaurantid, foodtypeid) VALUES (:restaurantId, :foodtypeId)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("restaurantId", restaurant.getId())
                    .addParameter("foodtypeId", foodtype.getId())
                    .executeUpdate();
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
    }

    @Override
    public List<Foodtype> getAllFoodtypesByRestaurant(int restaurantId) {
        ArrayList<Foodtype> foodtypes = new ArrayList<>();

        String joinQuery = "SELECT foodtypeid FROM restaurants_foodtypes WHERE restaurantid = :restaurantId";

        try (Connection con = sql2o.open()) {
            List<Integer> allFoodtypesIds = con.createQuery(joinQuery)
                    .addParameter("restaurantId", restaurantId)
                    .executeAndFetch(Integer.class);
            for (Integer foodId : allFoodtypesIds){
                String foodtypeQuery = "SELECT * FROM foodtypes WHERE id = :foodtypeId";
                foodtypes.add(
                        con.createQuery(foodtypeQuery)
                                .addParameter("foodtypeId", foodId)
                                .executeAndFetchFirst(Foodtype.class));
            }
        } catch (Sql2oException ex){
            System.out.println(ex);
        }
        return foodtypes;
    }

}
