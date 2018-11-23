package com.example.johnnyma.testbench;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingQuestionFragment extends Fragment {
    TextView loadingText;
    TextView roundWinnerText;
    TextView winText;
    ImageView winnerAvatar;
    public LoadingQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingText = view.findViewById(R.id.next_q_msg);
        loadingText.setText(getArguments().getString("next_q_msg"));
        roundWinnerText = view.findViewById(R.id.round_winner);
        roundWinnerText.setText(getArguments().getString("round_winner"));
        winText = view.findViewById(R.id.win_msg);
        winText.setText("won last round!");
        winnerAvatar = view.findViewById(R.id.winner_avatar);
        int avatar = getArguments().getInt("winner_avatar", 0);
        switch(avatar % 6) {
            case 0:
                winnerAvatar.setImageResource(R.drawable.penguin_avatar);
                break;
            case 1:
                winnerAvatar.setImageResource(R.drawable.mountain_avatar);
                break;
            case 2:
                winnerAvatar.setImageResource(R.drawable.rocket_avatar);
                break;
            case 3:
                winnerAvatar.setImageResource(R.drawable.frog_avatar);
                break;
            case 4:
                winnerAvatar.setImageResource(R.drawable.thunderbird_avatar);
                break;
            case 5:
                winnerAvatar.setImageResource(R.drawable.cupcake_avatar);
                break;
        }
    }
}
