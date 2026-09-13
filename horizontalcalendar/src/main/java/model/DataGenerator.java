package model;

import com.apachat.primecalendar.core.PrimeCalendar;
import com.apachat.primecalendar.core.persian.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataGenerator {

    public List<DayModel> generateDaysForCurrMonth() {

        PrimeCalendar todayCalendar = new PersianCalendar();

        int year = todayCalendar.get(Calendar.YEAR);
        int month = todayCalendar.get(Calendar.MONTH);

        PrimeCalendar calendar = new PersianCalendar();
        calendar.set(year, month, 1);

        int lastDayCurrMonth =
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<DayModel> daysList = new ArrayList<>();

        for (int i = 1; i <= lastDayCurrMonth; i++) {

            calendar.set(Calendar.DAY_OF_MONTH, i);

            int dayNumber = i;

            String monthName =
                    calendar.getDisplayName(
                            Calendar.MONTH,
                            Calendar.LONG,
                            Locale.US
                    );

            String dayOfWeek =
                    getPersianDayName(
                            calendar.get(Calendar.DAY_OF_WEEK)
                    );

            boolean isToday =
                    (i == todayCalendar.get(Calendar.DAY_OF_MONTH)
                            && month == todayCalendar.get(Calendar.MONTH)
                            && year == todayCalendar.get(Calendar.YEAR));

            boolean isSelected =
                    (i == todayCalendar.get(Calendar.DAY_OF_MONTH));

            DayModel day = new DayModel(
                    dayNumber,
                    month,
                    monthName,
                    dayOfWeek,
                    year,
                    isToday,
                    isSelected
            );

            daysList.add(day);
        }

        return daysList;
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
                return "";
        }
    }
}