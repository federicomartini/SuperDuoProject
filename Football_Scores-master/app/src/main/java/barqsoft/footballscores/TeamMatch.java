package barqsoft.footballscores;


public class TeamMatch {

    private String homeTeam;
    private String awayTeam;
    private String homeScores;
    private String awayScores;
    private double matchId;
    private String data;

    public TeamMatch() {

    }

    public void setHomeTeam(String team) {
        this.homeTeam = team;
    }

    public void setAwayTeam(String team) {
        this.awayTeam = team;
    }

    public void setHomeScores(String scores) {
        this.homeScores = scores;
    }

    public void setAwayScores(String scores) {
        this.awayScores = scores;
    }

    public void setMatchId(double matchId) {
        this.matchId = matchId;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHomeTeam() {
        return this.homeTeam;
    }

    public String getAwayTeam() {
        return this.awayTeam;
    }

    public String getHomeScores() {
        return this.homeScores;
    }

    public String getAwayScores() {
        return this.awayScores;
    }

    public double getMatchId() {
        return this.matchId;
    }

    public String getData() {
        return this.data;
    }

}
