package bth.rushhour.makindu.mrush.Setters;

/**
 * Created by lenovo on 1/12/2018.
 */

public class Specialist {

    private String title;
    private String shortdesc;
    private double rating;
    private String phone;
    private String image;
    private String userName;
    private Long timestamp;

    public Specialist(String userName,String title, String shortdesc, double rating, String phone, String image,long timestamp) {

        this.userName = userName;
        this.title = title;
        this.shortdesc = shortdesc;
        this.rating = rating;
        this.phone = phone;
        this.image = image;
        this.timestamp = timestamp;
    }public Specialist() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
