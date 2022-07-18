package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import my.edu.utem.ftmk.bitp3453.bitig1attendance.databinding.ActivityLoginBinding;
import my.edu.utem.ftmk.bitp3453.bitig1attendance.databinding.ActivityScheduleEntryBinding;

public class ScheduleEntryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Bundle[]> {

    private String token;
    private Bundle[] clazzes;
    private Schedule schedule;
    private LoaderManager loaderManager;
    private Spinner cmbClazzes;
//    private EditText txtStart, txtDuration;
    private ActivityScheduleEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");



        loaderManager = LoaderManager.getInstance(this);
        binding = ActivityScheduleEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnStart.setOnClickListener(this::selectDateTime);
        binding.btnSubmit.setOnClickListener(this::saveSchedule);

        if (savedInstanceState != null)
        {

            clazzes = (Bundle[]) savedInstanceState.getParcelableArray("clazzes");
            String[] temp = Arrays.stream(clazzes).map(bundle -> bundle.getString("courseID") +
                    " - " + bundle.getString("name")).toArray(String[]::new);
            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, temp);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.cmbClazzes.setAdapter(adapter);

            long startDateTime = savedInstanceState.getLong("startDateTime");
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(startDateTime);
            schedule.setStartDateTime(startDateTime);
            binding.btnStart.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,
                    DateFormat.SHORT).format(calendar.getTime()));

        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]
                    {"Loading classes, please wait"});

            if(intent.hasExtra("schedule"))
            {
                schedule = (Schedule) intent.getSerializableExtra("schedule");

                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(schedule.getStartDateTime());
                binding.btnStart.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,
                        DateFormat.SHORT).format(calendar.getTime()));

                if(schedule.getType() == 0)
                    binding.rdbLecture.setChecked(true);
                else if(schedule.getType() == 1)
                    binding.rdbLab.setChecked(true);
                else if(schedule.getType() == 2)
                    binding.rdbTutorial.setChecked(true);

                binding.txtDuration.setText(String.valueOf(schedule.getDuration()));

            }
            else
                schedule = new Schedule();

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.cmbClazzes.setAdapter(adapter);

            loaderManager.initLoader(0, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArray("clazzes", clazzes);
        outState.putLong("startDateTime", schedule.getStartDateTime());
        super.onSaveInstanceState(outState);
    }

    private void selectDateTime(View view){
        GregorianCalendar calendar = new GregorianCalendar();

        calendar.set(GregorianCalendar.DAY_OF_MONTH,
                calendar.get(GregorianCalendar.DAY_OF_MONTH) + 1);
        new DatePickerDialog(this, (dialog, year, month, day) -> selectDate(calendar, year, month, day), calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH),
                calendar.get(GregorianCalendar.DAY_OF_MONTH)).show();
    }

    private void selectDate(GregorianCalendar calendar, int year, int month, int day){
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        new TimePickerDialog(this,
                (dialog, hour, minute) -> selectTime(calendar, hour, minute),
                calendar.get(GregorianCalendar.HOUR_OF_DAY),
                calendar.get(GregorianCalendar.MINUTE), true).show();
    }

    private void selectTime(GregorianCalendar calendar, int hour, int minute){
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
        calendar.set(GregorianCalendar.MINUTE, minute);

        schedule.setStartDateTime(calendar.getTimeInMillis());
        binding.btnStart.setText(DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.SHORT).format(calendar.getTime()));
    }


    private void saveSchedule(View view)
    {
        Bundle clazz = clazzes[binding.cmbClazzes.getSelectedItemPosition()];
        String duration = binding.txtDuration.getText().toString();

        Intent intent = new Intent();

        schedule.setClazzID(clazz.getInt("clazzID"));
        schedule.setCourse(clazz.getString("courseID") + " - " + clazz.getString("name"));

        if (binding.rdgType.getCheckedRadioButtonId() == R.id.rdbLecture)
            schedule.setType(0);
        else if (binding.rdgType.getCheckedRadioButtonId() == R.id.rdbLab)
            schedule.setType(1);
        else if (binding.rdgType.getCheckedRadioButtonId() == R.id.rdbTutorial)
            schedule.setType(2);

        try {
            schedule.setDuration(Integer.parseInt(duration));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        intent.putExtra("schedule", schedule);
        setResult(RESULT_OK, intent);
        finish();
    }

    @NonNull
    @Override
    public Loader<Bundle[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new ClazzesLoader(this, token);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bundle[]> loader, Bundle[] clazzes) {
        loaderManager.destroyLoader(loader.getId());

        if (clazzes != null)
        {
            this.clazzes = clazzes;
            String[] temp = Arrays.stream(clazzes).map(bundle -> bundle.getString("courseID") +
                    " - " + bundle.getString("name")).toArray(String[]::new);
            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, temp);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.cmbClazzes.setAdapter(adapter);

            if(schedule.getScheduleID() != 0){
                binding.cmbClazzes.setSelection(Arrays.asList(temp).indexOf(schedule.getCourse()));
            }

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bundle[]> loader) {

    }
}