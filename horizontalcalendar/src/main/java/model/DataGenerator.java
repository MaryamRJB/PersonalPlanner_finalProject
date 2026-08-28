package model;

import com.apachat.primecalendar.core.PrimeCalendar;
import com.apachat.primecalendar.core.persian.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataGenerator {
    public List<DayModel> generateDaysForCurrMonth() {
        PrimeCalendar today_calendar = new PersianCalendar();
        int year = today_calendar.get(Calendar.YEAR);
        int month = today_calendar.get(Calendar.MONTH);

        PrimeCalendar calendar = new PersianCalendar();
        calendar.set(year, month, 1);
        int last_day_curr_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<DayModel> days_list = new ArrayList<>();

        for (int i = 1; i <= last_day_curr_month; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);

            int day_number = i;
            String month_name = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
            String day_of_week = getPersianDayName(calendar.get(Calendar.DAY_OF_WEEK));

            boolean is_today = (i == today_calendar.get(Calendar.DAY_OF_MONTH) &&
                    month == today_calendar.get(Calendar.MONTH) &&
                    year == today_calendar.get(Calendar.YEAR));

            boolean is_selected = (i == today_calendar.get(Calendar.DAY_OF_MONTH));

            DayModel day = new DayModel(day_number, month_name, day_of_week, year, is_today,is_selected);
            days_list.add(day);


        }
        return days_list;
    }
    private String getPersianDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "یکشنبه";

            case Calendar.MONDAY:
                return "دوشنبه";

            case Calendar.TUESDAY:
                return "سه‌شنبه";

            case Calendar.WEDNESDAY:
                return "چهارشنبه";

            case Calendar.THURSDAY:
                return "پنجشنبه";

            case Calendar.FRIDAY:
                return "جمعه";

            case Calendar.SATURDAY:
                return "شنبه";

            default:
                return null;
        }
    }
}
