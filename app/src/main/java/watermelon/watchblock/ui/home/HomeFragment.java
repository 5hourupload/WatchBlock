package watermelon.watchblock.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import watermelon.watchblock.R;

import static watermelon.watchblock.MainActivity.coinId;
import static watermelon.watchblock.MainActivity.uuid;

public class HomeFragment extends Fragment
{

    double latti;
    double longi;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // check if permissions have been granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // request user for location access
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else // get phone's location
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
            {
                latti = location.getLatitude();
                longi = location.getLongitude();
            }
            else
            {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Unable to retrieve location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        Button buttonSolo = root.findViewById(R.id.submitTipButton);

        final EditText description = root.findViewById(R.id.crimeDescription);
        TextView latlong = root.findViewById(R.id.lat_long);
        latlong.setText("Latitude: " + latti + "\nLongitude: " + longi);
        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date now = new Date();
                Long longTime = now.getTime() / 1000;
                int duration = 2000;

                try
                {
                    boolean success = createAsset(description.getText().toString(),Double.toString(latti),Double.toString(longi),longTime);
                    if (success)
                    {
                        Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Crime reported.", 2000);
                        mySnackbar.show();
                        WebView webView = new WebView(getActivity().getApplicationContext());
                        webView.getSettings().setJavaScriptEnabled(true);
                        description.setText("");
                        webView.loadUrl("https://www.cabq.gov/police/file-a-police-report-online");
                        getActivity().setContentView(webView);
                    }
                    else
                    {
                        Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Submission failed, please try again later", duration);
                        mySnackbar.show();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
       return root;
    }

    private boolean createAsset(String desc, String lat, String lon, long time) throws Exception {

        desc = desc.replace('\n',' ');
        String url = "https://test.devv.io/create-asset";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        String urlParameters = "{ \"uuid\": \"" + uuid + "\", \"coin_id\":" +
                "\"" + coinId+ "\", \"properties\": {\"crimeDescription\": \""+desc+"\"," +
                "\"lat\": \""+lat+"\",\"long\": \""+lon+"\",\"time\": "+time+"}}";
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = urlParameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        return con.getResponseCode() == 200;

    }

}