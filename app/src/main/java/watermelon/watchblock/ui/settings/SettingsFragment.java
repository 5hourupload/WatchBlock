package watermelon.watchblock.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import watermelon.watchblock.R;

/**
 * set settings page
 */
public class SettingsFragment extends Fragment
{
    private SharedPreferences sharedpreferences;
    private TextView crimeRadiusLabel;
    private TextView timeLabel;
    private static final String CRIME_RADIUS = "10";
    private static final String TIME_WINDOW = "30";
    private static final String IS_CHECKED = "notifications";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        //set crime radius seekbar label
        SeekBar seekBar = root.findViewById(R.id.distanceSeekBar);
        crimeRadiusLabel = root.findViewById(R.id.crimeRadiusLabel);
        String newText = "Crime Radius: " + seekBar.getProgress() + " miles";
        crimeRadiusLabel.setText(newText);

        //set crime time seekbar label
        SeekBar timeBar = root.findViewById(R.id.timeSeekBar);
        timeLabel = root.findViewById(R.id.timeWindow);
        newText = "Crime Time Window: " + timeBar.getProgress() + " minutes";
        timeLabel.setText(newText);

        //set notification switch
        final Switch notificationSwitch = root.findViewById(R.id.allowNotifications);

        sharedpreferences = this.getActivity()
                .getSharedPreferences("mainprefs", Context.MODE_PRIVATE);

        //update crime time seekbar label
        if (sharedpreferences.contains(TIME_WINDOW)) {
            newText = "Crime Time Window: " + sharedpreferences
                    .getString(TIME_WINDOW, "") + " minutes";
            timeLabel.setText(newText);
            timeBar.setProgress(Integer.parseInt(sharedpreferences
                    .getString(TIME_WINDOW, "")));
        }


        //update crime radius seekbar
        if (sharedpreferences.contains(CRIME_RADIUS)) {
            newText = "Crime Radius: " + sharedpreferences
                    .getString(CRIME_RADIUS, "") + " miles";
            crimeRadiusLabel.setText(newText);
            seekBar.setProgress(Integer.parseInt(sharedpreferences
                    .getString(CRIME_RADIUS, "")));
        }

        //update notification switch
        if(sharedpreferences.contains(IS_CHECKED)) {
            notificationSwitch.setChecked(sharedpreferences.getBoolean(IS_CHECKED, false));
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(IS_CHECKED, isChecked);
                editor.apply();
            }
        });


        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String newText = "Crime Time Window: " + sharedpreferences
                        .getString(TIME_WINDOW, "") + " minutes";
                timeLabel.setText(newText);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(TIME_WINDOW, String.valueOf(seekBar.getProgress()));
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                crimeRadiusLabel.setText("Crime Radius: " + seekBar.getProgress() + " miles");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(CRIME_RADIUS, String.valueOf(seekBar.getProgress()));
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final Button editProfile = root.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}