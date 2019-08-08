package com.katiforis.checkers.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.katiforis.checkers.DTO.UserDto;
import com.katiforis.checkers.DTO.response.RankList;
import com.katiforis.checkers.R;
import com.katiforis.checkers.activities.MenuActivity;
import com.katiforis.checkers.adapter.RankAdapter;
import com.katiforis.checkers.controller.RankController;
import com.katiforis.checkers.util.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RankFragment extends Fragment {
    private static RankFragment INSTANCE;
    RankController rankController;

    public static boolean populated;

    private RecyclerView rankRecyclerView;
    private RankAdapter rankAdapter;
    private List<UserDto> rankList = new ArrayList<>();
    private UserDto currentPlayer;
    private RankList currentRankList;
    private TextView rank, username, level, points;
    private TextView lastUpdate;
    private ImageView playerImage;
    private AdView mAdView;

    public static RankFragment getInstance() {
        if (INSTANCE == null) {
            synchronized(RankFragment.class) {
                INSTANCE = new RankFragment();
            }
        }
        INSTANCE.rankController = RankController.getInstance();
        INSTANCE.rankController.setRankFragment(INSTANCE);
        return INSTANCE;
    }

    public RankFragment() {
        populated = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_rank_layout,  null);

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        rankRecyclerView = (RecyclerView) view.findViewById(R.id.rank_list);
        rankRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        rankRecyclerView.addItemDecoration(new DividerItemDecoration(rankRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        rankAdapter = new RankAdapter(rankList);

        rankRecyclerView.smoothScrollToPosition(0);
        rankAdapter.notifyDataSetChanged();
        rankRecyclerView.setAdapter(rankAdapter);

        playerImage = (ImageView) view.findViewById(R.id.currentPlayerImage);
        username = (TextView) view.findViewById(R.id.username);
        rank = (TextView) view.findViewById(R.id.rank);
        level = (TextView) view.findViewById(R.id.level);
        points = (TextView) view.findViewById(R.id.points);

        lastUpdate = (TextView) view.findViewById(R.id.lastUpdate);

        if(currentRankList != null){
            setRankList(currentRankList);
        }

       return view;
    }

    public void getRankList(){
        if(!populated){
            rankController.getRankList();
            populated = true;
        }else{
            rankController.getRankListIfExpired();
        }
    }

    public void setRankList(RankList rankList){
        Activity activity = getActivity();
        currentRankList = rankList;
        if(activity != null){
            activity.runOnUiThread(() -> {
               List<UserDto> players = currentRankList.getPlayers();
               currentPlayer = currentRankList.getCurrentPlayer();

                Picasso.with(MenuActivity.getAppContext())
                        .load(currentPlayer.getPictureUrl())
                        .placeholder(MenuActivity.getAppContext().getResources().getDrawable(R.drawable.user))
                        .error(R.mipmap.ic_launcher)
                        .transform(new CircleTransform())
                        .into(playerImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                //TODO
                            }
                        });

//                Picasso.with(MenuActivity.getAppContext())
//                           .load(currentPlayer.getPictureUrl())
//                           .error(R.mipmap.ic_launcher)
//                           .into(playerImage);
                   username.setText(currentPlayer.getUsername());
                rank.setText(currentRankList.getCurrentPlayerPosition() + ".");
                level.setText(String.valueOf(currentPlayer.getPlayerDetails().getLevel()));
                points.setText(String.valueOf(currentPlayer.getPlayerDetails().getElo()));

//               if(players.size() >=3){
//                   UserDto first = players.get(0);
//                   UserDto second = players.get(1);
//                   UserDto third = players.get(2);
//                   players.remove(0);
//                   players.remove(0);
//                   players.remove(0);

//                   Picasso.with(MenuActivity.getAppContext())
//                           .load(first.getPictureUrl())
//                           .error(R.mipmap.ic_launcher)
//                           .into(playerImage);
//                   username.setText(first.getUsername());
//
//                   Picasso.with(MenuActivity.getAppContext())
//                           .load(second.getPictureUrl())
//                           .error(R.mipmap.ic_launcher)
//                           .into(playerImage2);
//                   username2.setText(second.getUsername());
//
//                   Picasso.with(MenuActivity.getAppContext())
//                           .load(third.getPictureUrl())
//                           .error(R.mipmap.ic_launcher)
//                           .into(playerImage3);
//                   username3.setText(third.getUsername());
//               }

                this.rankList.clear();
                this.rankList.addAll(players);
                rankAdapter.notifyDataSetChanged();

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String now = sdf.format(currentRankList.getTimestamp());
                lastUpdate.setText("Last update: " + now);
            });
        }
    }
}