package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.GregorianCalendar;

public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    private Schedule schedule;
    private final TextView txtCourse, txtDateTime, txtType;

    public ScheduleViewHolder(@NonNull View itemView) {
        super(itemView);

        txtCourse = itemView.findViewById(R.id.txtCourse);
        txtDateTime = itemView.findViewById(R.id.txtDateTime);
        txtType = itemView.findViewById(R.id.txtType);

        itemView.setOnClickListener(this::select);
    }

    public Schedule getSchedule()
    {
        return schedule;
    }

    public void setSchedule(Schedule schedule)
    {
        this.schedule = schedule;
        GregorianCalendar start = new GregorianCalendar(), end = new GregorianCalendar();

        start.setTimeInMillis(schedule.getStartDateTime());
        end.setTimeInMillis(schedule.getStartDateTime());
        end.add(GregorianCalendar.HOUR, schedule.getDuration());

        txtCourse.setText(schedule.getCourse());
        txtDateTime.setText(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
                .format(start.getTime()) + "until" + DateFormat.getTimeInstance(DateFormat.SHORT)
                .format(end.getTime()));

        if (schedule.getType() == 0)
            txtType.setText("Lecture");
        else if (schedule.getType() == 1)
            txtType.setText("Lab");
        else if (schedule.getType() == 2)
            txtType.setText("Tutorial");
    }

    private void select(View view) {
        LecturerMenuActivity activity = (LecturerMenuActivity) itemView.getContext();
        long now = System.currentTimeMillis();

        if(now >= schedule.getStartDateTime() && now < schedule.getStartDateTime()
                + schedule.getDuration() * 3_600_000L)
        {

            activity.scanBarcode(schedule);
        }
        else if(now < getSchedule().getStartDateTime())
            activity.updateSchedule(schedule);
    }
}
