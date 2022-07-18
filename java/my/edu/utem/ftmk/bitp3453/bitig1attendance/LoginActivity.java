 package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import my.edu.utem.ftmk.bitp3453.bitig1attendance.databinding.ActivityLoginBinding;

 public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bundle> {

     private ActivityLoginBinding binding;
     private LoaderManager loaderManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        loaderManager = LoaderManager.getInstance(this);

        setContentView(binding.getRoot());

        binding.btnSubmit.setOnClickListener(this::goToUserMenu);
    }

    private void goToUserMenu(View view)
    {
        binding.txtAccountID.setEnabled(false);
        binding.txtPassword.setEnabled(false);
        binding.btnSubmit.setEnabled(false);
        binding.pgbLogin.setVisibility(View.VISIBLE);

        loaderManager.initLoader(0,null,this);
    }

     @NonNull
     @Override
     public Loader<Bundle> onCreateLoader(int id, @Nullable Bundle args) {
         return new LoginLoader(this, binding.txtAccountID.getText().toString(), binding.txtPassword.getText().toString());
     }

     @Override
     public void onLoadFinished(@NonNull Loader<Bundle> loader, Bundle account) {

         binding.txtAccountID.setEnabled(true);
         binding.txtPassword.setEnabled(true);
         binding.btnSubmit.setEnabled(true);
         binding.pgbLogin.setVisibility(View.GONE);

        loaderManager.destroyLoader(loader.getId());

        if (account != null)
        {

            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            sharedPreferences.edit().putString("accountID", account.getString("accountID"))
                    .putString("token", account.getString("token"))
                    .putString("name", account.getString("name"))
                    .putBoolean("lecturer", account.getBoolean("lecturer")).apply();

            Intent intent = new Intent(this, account.getBoolean("lecturer") ? LecturerMenuActivity.class : StudentMenuActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);

        }

        else
            Toast.makeText(this,"Invalid email or passeord",
                       Toast.LENGTH_LONG).show();

     }

     @Override
     public void onLoaderReset(@NonNull Loader<Bundle> loader) {

     }
 }