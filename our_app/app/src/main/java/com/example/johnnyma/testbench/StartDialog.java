package com.example.johnnyma.testbench;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StartDialog extends AppCompatDialogFragment {
    private Button startButton;
    private Button cancelButton;
    private TextView username_opponent;
    private TextView rank_opponent;
    private ImageView avatar_opponent;
    StartDialogListener listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (StartDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement listener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String opponentUserName = getArguments().getString("opponent_username");
        int opponentRank = getArguments().getInt("opponent_rank");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_start_game, null);
        builder.setView(view);
        startButton = view.findViewById(R.id.start_btn);
        cancelButton = view.findViewById(R.id.cancel_btn);
        username_opponent = view.findViewById(R.id.user_name);
        username_opponent.setText(opponentUserName);
        rank_opponent = view.findViewById(R.id.rank_field);
        rank_opponent.setText("Rank " + opponentRank);
        avatar_opponent = view.findViewById(R.id.avatar);
        switch(opponentRank % 6) {
            case 0: avatar_opponent.setImageResource(R.drawable.penguin_avatar);
            case 1: avatar_opponent.setImageResource(R.drawable.mountain_avatar);
            case 2: avatar_opponent.setImageResource(R.drawable.rocket_avatar);
            case 3: avatar_opponent.setImageResource(R.drawable.frog_avatar);
            case 4: avatar_opponent.setImageResource(R.drawable.thunderbird_avatar);
            case 5: avatar_opponent.setImageResource(R.drawable.cupcake_avatar);
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "start pressed", Toast.LENGTH_SHORT).show();
                listener.startOrCancel(true);
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "cancel pressed", Toast.LENGTH_SHORT).show();
                listener.startOrCancel(false);
                dismiss();
            }
        });
        return builder.create();
    }
    public interface StartDialogListener {
        void startOrCancel(boolean start);
    }
}
