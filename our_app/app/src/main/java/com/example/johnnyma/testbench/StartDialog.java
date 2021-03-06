package com.example.johnnyma.testbench;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class StartDialog extends DialogFragment {
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
        String opponentAlias = getArguments().getString("opponent_alias");
        int opponentRank = Integer.parseInt(getArguments().getString("opponent_rank"));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_start_game, null);
        builder.setView(view);
        startButton = view.findViewById(R.id.start_btn);
        cancelButton = view.findViewById(R.id.cancel_btn);
        username_opponent = view.findViewById(R.id.user_name);
        username_opponent.setText(opponentAlias);
        rank_opponent = view.findViewById(R.id.rank_field);
        rank_opponent.setText("Rank " + Integer.toString(opponentRank));
        avatar_opponent = view.findViewById(R.id.avatar);
        switch(opponentRank % 6) {
            case 0: {
                avatar_opponent.setImageResource(R.drawable.penguin_avatar);
                break;
            }
            case 1: {
                avatar_opponent.setImageResource(R.drawable.mountain_avatar);
                break;
            }
            case 2: {
                avatar_opponent.setImageResource(R.drawable.rocket_avatar);
                break;
            }
            case 3: {
                avatar_opponent.setImageResource(R.drawable.frog_avatar);
                break;
            }
            case 4: {
                avatar_opponent.setImageResource(R.drawable.thunderbird_avatar);
                break;
            }
            case 5: {
                avatar_opponent.setImageResource(R.drawable.cupcake_avatar);
                break;
            }
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.startOrCancel(true);
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.startOrCancel(false);
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
        }
    }

    public interface StartDialogListener {
        void startOrCancel(boolean start);
    }
}
