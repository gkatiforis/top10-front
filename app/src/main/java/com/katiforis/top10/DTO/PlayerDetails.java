package com.katiforis.top10.DTO;


public class PlayerDetails extends Game{
    private String username;
    public PlayerDetails(String status) {
        super(status);
    }

    public PlayerDetails(String status, String username) {
        super(status);
        this.username = username;
    }

    public PlayerDetails(String gameId, String status, String username) {
        super(gameId, status);
        this.username = username;
    }

    public PlayerDetails(String status, String gameId, String userId, String username) {
        super(status, gameId, userId);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
