package bth.rushhour.makindu.mrush.Setters;

/**
 * Created by lenovo on 3/8/2018.
 */

public class Links {

    String newsheader;
    String mainnews;
    String sportsheader;
    String biasharalink;
    String biasharadesc;


    public Links(String newsheader, String mainnews, String sportsheader) {
        this.newsheader = newsheader;
        this.mainnews = mainnews;
        this.sportsheader = sportsheader;
    }

    public Links(String biasharalink, String biasharadesc) {
        this.biasharalink = biasharalink;
        this.biasharadesc = biasharadesc;
    }

    public Links() {
    }

    public String getNewsheader() {
        return newsheader;
    }

    public void setNewsheader(String newsheader) {
        this.newsheader = newsheader;
    }

    public String getMainnews() {
        return mainnews;
    }

    public void setMainnews(String mainnews) {
        this.mainnews = mainnews;
    }

    public String getSportsheader() {
        return sportsheader;
    }

    public void setSportsheader(String sportsheader) {
        this.sportsheader = sportsheader;
    }

    public String getBiasharalink() {
        return biasharalink;
    }

    public void setBiasharalink(String biasharalink) {
        this.biasharalink = biasharalink;
    }

    public String getBiasharadesc() {
        return biasharadesc;
    }

    public void setBiasharadesc(String biasharadesc) {
        this.biasharadesc = biasharadesc;
    }
}
