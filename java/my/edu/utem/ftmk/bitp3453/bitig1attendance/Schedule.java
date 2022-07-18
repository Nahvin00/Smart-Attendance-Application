package my.edu.utem.ftmk.bitp3453.bitig1attendance;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Schedule implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long scheduleID;
    private int clazzID;
    private int type;
    private long startDateTime;
    private int duration;
    private String lecturerID;
    private String course;

    public long getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(long scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getClazzID() {
        return clazzID;
    }

    public void setClazzID(int clazzID) {
        this.clazzID = clazzID;
    }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLecturerID() { return lecturerID; }

    public void setLecturerID(String lecturerID) { this.lecturerID = lecturerID; }

    public String getCourse() { return course; }

    public void setCourse(String course) { this.course = course; }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;

        if (o instanceof Schedule)
        {
            Schedule that = (Schedule) o;
            equals = scheduleID == that.scheduleID;
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return (int) scheduleID;
    }
}
