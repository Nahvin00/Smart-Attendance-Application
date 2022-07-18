package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class ClazzesLoader extends AsyncTaskLoader<Bundle[]>
{
    private final String token;

    public ClazzesLoader(@NonNull Context context, String token){
        super(context);
        this.token = token;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Bundle[] loadInBackground(){
        Bundle[] clazzes = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL
                    ("https://pres.utem.edu.my/attendance/clazzes").openConnection();


            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);

            if (connection.getResponseCode() / 100 == 2){
                JSONArray response = new JSONArray(new BufferedReader(new InputStreamReader
                        (connection.getInputStream())).lines().collect(Collectors.joining()));
                int length = response.length();
                clazzes = new Bundle[length];

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = response.getJSONObject(i);
                    Bundle clazz = clazzes[i] = new Bundle();

                    clazz.putInt("clazzID", object.getInt("clazzID"));
                    clazz.putString("courseID", object.getString("courseID"));
                    clazz.putString("name", object.getString("name"));
                }
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clazzes;
    }
}
