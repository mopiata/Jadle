package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Review implements Comparable<Review> {
    private String content;
    private String writtenBy;
    private int rating;
    private int id;
    private int restaurantId; //will be used to connect Restaurant to Review (one-to-many)
    private long createdat;
    private String formattedCreatedAt;

    public Review(String content, String writtenBy, int rating, int restaurantId) {
        this.content = content;
        this.writtenBy = writtenBy;
        this.rating = rating;
        this.restaurantId = restaurantId;
        this.createdat=System.currentTimeMillis();
        setFormattedCreatedAt();
    }

    public long getCreatedat() {
        return createdat;
    }

    public void setCreatedat() {
        this.createdat = System.currentTimeMillis();
    }

    public String getFormattedCreatedAt() {
        Date date=new Date(createdat);
        String datePatternToUse="MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf=new SimpleDateFormat(datePatternToUse);
        return sdf.format(date);
    }

    public void setFormattedCreatedAt() {
        Date date=new Date(this.createdat);
        String datePatternToUse="MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf= new SimpleDateFormat(datePatternToUse);
        this.formattedCreatedAt = sdf.format(date);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWrittenBy() {
        return writtenBy;
    }

    public void setWrittenBy(String writtenBy) {
        this.writtenBy = writtenBy;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return getRating() == review.getRating() &&
                getId() == review.getId() &&
                getRestaurantId() == review.getRestaurantId() &&
                Objects.equals(getContent(), review.getContent()) &&
                Objects.equals(getWrittenBy(), review.getWrittenBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getWrittenBy(), getRating(), getId(), getRestaurantId());
    }

    @Override
    public int compareTo(Review reviewObject) {
        if(this.createdat<reviewObject.createdat){
            return -1;
        }else if(this.createdat>reviewObject.createdat){
            return 1;
        }else {
            return 0;
        }
    }
}
