package com.saenz.checkmate.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.saenz.checkmate.receiver.NotificationReceiver;
import java.util.Calendar;

public class NotificationScheduler {

    public static void scheduleDailyNotification(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);

        intent.putExtra("IdAlarma1",0);
        intent.putExtra("IdAlarma2",1);
        PendingIntent pendingIntent = createIntent(0,context,intent);
        PendingIntent pendingIntent1 = createIntent(1,context,intent);

        Calendar calendar = setCalendar(11,33,30);
        Calendar calendar1 = setCalendar(11,8,30);

        checkAndAdjustCalendar (calendar);
        checkAndAdjustCalendar (calendar1);

        // Repetir cada 24 horas
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent1
        );
    }
    private static Calendar setCalendar(int h, int m, int s){
        Calendar returnCalendar = Calendar.getInstance();
        returnCalendar.set(Calendar.HOUR_OF_DAY,h);
        returnCalendar.set(Calendar.MINUTE,m);
        returnCalendar.set(Calendar.SECOND,s);
        return returnCalendar;
    }
    private static  void checkAndAdjustCalendar (Calendar c){
        if (c.getTimeInMillis() <= System.currentTimeMillis()  ) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private static PendingIntent createIntent (int m, Context context,Intent intent){
        return PendingIntent.getBroadcast(
                context,
                m,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}