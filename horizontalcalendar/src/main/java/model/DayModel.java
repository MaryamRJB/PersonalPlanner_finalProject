package model;

public class DayModel {

    private int dayOfMonth;
    private int month;
    private String monthName;
    private String dayOfWeekName;
    private int year;

    private boolean isToday;
    private boolean isSelected;

    public DayModel(
            int dayOfMonth,
            int month,
            String monthName,
            String dayOfWeekName,
            int year,
            boolean isToday,
            boolean isSelected) {

        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.monthName = monthName;
        this.dayOfWeekName = dayOfWeekName;
        this.year = year;
        this.isToday = isToday;
        this.isSelected = isSelected;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public String getMonthName() {
        return monthName;
    }

    public String getDayOfWeekName() {
        return dayOfWeekName;
    }

    public int getYear() {
        return year;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}