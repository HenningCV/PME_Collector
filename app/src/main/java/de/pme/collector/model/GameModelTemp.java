package de.pme.collector.model;


public class GameModelTemp {
    private final String gameTitle;
    private final String gamePublisher;
    private final int image;

    // Constructor
    public GameModelTemp(String gameTitles, String gamePublishers, int image) {
        this.gameTitle = gameTitles;
        this.gamePublisher = gamePublishers;
        this.image = image;
    }


    public String getGameTitle() {
        return gameTitle;
    }

    public String getGamePublisher() {
        return gamePublisher;
    }

    public int getImage() {
        return image;
    }
}