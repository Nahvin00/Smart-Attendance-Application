package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import my.edu.utem.ftmk.bitp3453.bitig1attendance.databinding.ActivityLecturerMenuBinding;

public class LecturerMenuActivity extends AppCompatActivity {

    private Bundle account;
    private ActivityLecturerMenuBinding binding;
    private ActivityResultLauncher<Intent> scheduleEntryLauncher, scanBarcodeLauncher;
    private ScheduleViewModel scheduleViewModel;
    private ScheduleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_menu);

        account = getIntent().getBundleExtra("account");
        String name = account.getString("name");


        ScheduleAdapter adapter = new ScheduleAdapter(this);
        scheduleEntryLauncher = registerForActivityResult(new ActivityResultContracts
                        .StartActivityForResult(),
                this::saveSchedule);

        scanBarcodeLauncher = registerForActivityResult(new ActivityResultContracts
                        .StartActivityForResult(),
                this::saveBarcode);

        RecyclerView rcvSchedules = findViewById(R.id.rcvSchedules);

        binding.btnAddSchedule.setOnClickListener(this::addSchedule);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);


        scheduleViewModel.getSchedules(account.getString("accountID"))
                .observe(this, adapter::setSchedules);

        rcvSchedules.setAdapter(adapter);
        rcvSchedules.setLayoutManager(new LinearLayoutManager(this));

        Snackbar.make(binding.lytLecturerMenu, "Welcome " + name,
                Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar
                .ANIMATION_MODE_SLIDE).show();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                ScheduleViewHolder scheduleViewHolder = (ScheduleViewHolder) viewHolder;
                Schedule schedule = scheduleViewHolder.getSchedule();

                new AlertDialog.Builder(LecturerMenuActivity.this).setMessage
                        ("Are you sure want to delete schedule?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) ->
                                deleteSchedule(schedule))
                        .setNegativeButton(android.R.string.no, ((dialog, which) ->
                                scheduleViewHolder.getBindingAdapter().notifyDataSetChanged())).show();
            }
        }).attachToRecyclerView(rcvSchedules);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("Are you sure want to logout?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.lecturer_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSchedule(Schedule schedule){

        Intent intent = new Intent(this, ScheduleEntryActivity.class);

        intent.putExtra("token", account.getString("token"));
        intent.putExtra("schedule", schedule);
        scheduleEntryLauncher.launch(intent);

    }

    public void scanBarcode(Schedule schedule)
    {
        scanBarcodeLauncher.launch(new Intent(this, BarcodeScannerActivity.class));
    }

    private void saveSchedule(ActivityResult result)
    {
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK && data != null) {

            Schedule schedule = (Schedule) data.getSerializableExtra("schedule");

            if(schedule.getScheduleID() == 0)
            {
                schedule.setLecturerID(account.getString("accountID"));
                if (scheduleViewModel.insertSchedule(schedule) != 0)
                {
                    Snackbar.make(binding.lytLecturerMenu, "Successfully added new schedule",
                            Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar
                            .ANIMATION_MODE_SLIDE).show();
                }
            }
            else{
                if (scheduleViewModel.updateSchedule(schedule) != 0)
                {
                    Snackbar.make(binding.lytLecturerMenu, "Successfully updated the schedule",
                            Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar
                            .ANIMATION_MODE_SLIDE).show();
                }
            }

        }
    }


    private void saveBarcode(ActivityResult result)
    {
        if (result.getResultCode() == RESULT_OK && result.getData() != null)
        {
            Intent data = result.getData();
            String locationID = data.getStringExtra("locationID"),
                    locationName = data.getStringExtra("locationName");
        }

    }


    private void addSchedule(View view)
    {
        Intent intent = new Intent(this, ScheduleEntryActivity.class);

        intent.putExtra("token", account.getString("token"));
        scheduleEntryLauncher.launch(intent);
    }

    private void deleteSchedule(Schedule schedule)
    {
        int status = scheduleViewModel.deleteSchedule(schedule);

        if (status != 0)
        {
            Snackbar.make(binding.lytLecturerMenu, "Successfully deleted the schedule",
                    Snackbar.LENGTH_SHORT).setAnimationMode(BaseTransientBottomBar
                    .ANIMATION_MODE_SLIDE).show();
        }
    }


    private void logout()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}