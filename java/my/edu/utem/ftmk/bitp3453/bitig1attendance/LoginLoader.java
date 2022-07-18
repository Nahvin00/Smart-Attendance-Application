package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class LoginLoader extends AsyncTaskLoader<Bundle> {

    private String accountID, password;

    public LoginLoader(@NonNull Context context, String accountID, String password) {
        super(context);

        this.accountID = accountID;
        this.password = password;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Bundle loadInBackground() {
        Bundle account = null;
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL
                    ("https://pres.utem.edu.my/attendance/login").openConnection();
            JSONObject request = new JSONObject();
            request.put("accountID", accountID);
            request.put("password", password);

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(request.toString().getBytes());

            if (connection.getResponseCode() / 100 == 2){
                JSONObject response = new JSONObject(new BufferedReader(new InputStreamReader
                        (connection.getInputStream())).lines().collect(Collectors.joining()));
                account = new Bundle();
                account.putString("accountID", response.getString("accountID"));
                account.putString("token", response.getString("token"));
                account.putString("name", response.getString("name"));
                account.putBoolean("lecturer", response.getBoolean("lecturer"));
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
