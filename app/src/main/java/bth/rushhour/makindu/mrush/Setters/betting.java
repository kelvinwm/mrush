package bth.rushhour.makindu.mrush.Setters;

/**
 * Created by lenovo on 2/22/2018.
 */

public class betting {

    private Long timestamp;
    private String predictor;
    private String game;
    private String results;

    public betting(Long timestamp, String predictor, String game, String results) {
        this.timestamp = timestamp;
        this.predictor = predictor;
        this.game = game;
        this.results = results;
    }

    public betting(){}

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPredictor() {
        return predictor;
    }

    public void setPredictor(String predictor) {
        this.predictor = predictor;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
