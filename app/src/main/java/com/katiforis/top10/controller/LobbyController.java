package com.katiforis.top10.controller;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.katiforis.top10.DTO.response.Lobby;
import com.katiforis.top10.DTO.response.ResponseState;
import com.katiforis.top10.activities.MenuActivity;
import com.katiforis.top10.conf.Const;
import com.katiforis.top10.fragment.LobbyFragment;
import com.katiforis.top10.stomp.Client;

import org.json.JSONException;
import org.json.JSONObject;

import ua.naiksoftware.stomp.dto.StompMessage;


public class LobbyController extends MenuController{

    private static LobbyController INSTANCE = null;

    private LobbyFragment lobbyFragment;

    private LobbyController(){ }

    public static LobbyController getInstance() {
        if (INSTANCE == null) {
            synchronized(LobbyController.class) {
                INSTANCE = new LobbyController();
            }
        }
        return INSTANCE;
    }

    public LobbyFragment getLobbyFragment() {
        return lobbyFragment;
    }

    public void setLobbyFragment(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
    }

    @Override
    public void onReceive(StompMessage stompMessage){
        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(stompMessage.getPayload());

        Log.i(Const.TAG, "Receive: " + jo.toString());

        JsonObject message = jo.getAsJsonObject("body");
        String messageStatus =  message.get("status").getAsString();

        Log.i(Const.TAG, "Receive: " + messageStatus);
         if(messageStatus.equalsIgnoreCase(ResponseState.LOBBY.getState())){
            Lobby lobby = gson.fromJson(message, Lobby.class);
            lobbyFragment.populateLobby(lobby);
        }
    }

    public void getLobby(){
        addTopic(MenuActivity.userId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("playerId", MenuActivity.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Client.getInstance().send(Const.GET_LOBBY, jsonObject.toString());
    }
}