package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Vector;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {

    private Vector<Schedule> schedules;
    private final LayoutInflater layoutInflater;

    public ScheduleAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(layoutInflater.inflate(R.layout.schedule_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {

        holder.setSchedule(schedules.get(position));

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules.clear();
        this.schedules.addAll(schedules);
        notifyDataSetChanged();
    }
}
