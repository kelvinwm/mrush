package bth.rushhour.makindu.mrush.Setters;


/**
 * Created by lenovo on 1/25/2018.
 */

public class bizner {
    private String title;
    private String currentuser;
    private String shortdesc;
    private double rating;
    private String phone;
    private String image;
    private long timestamp;

    public bizner(String currentuser,String title, String shortdesc, double rating, String phone, String image, long timestamp) {

        this.currentuser = currentuser;
        this.title = title;
        this.shortdesc = shortdesc;
        this.rating = rating;
        this.phone = phone;
        this.image = image;
        this.timestamp = timestamp;
    }
    public bizner() {

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long String) {
        this.timestamp = String;
    }

    public String getCurrentuser() {
        return currentuser;
    }

    public void setCurrentuser(String currentuser) {
        this.currentuser = currentuser;
    }
}
