package com.example.alarmclock;

import static android.os.Build.*;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmclock.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannel();
        binding.SelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Alarm Time")
                        .build();

                timePicker.show(getSupportFragmentManager(),"androidKnowledge");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(timePicker.getHour() >12){
                            binding.SelectTime.setText(
                                    String.format("%02d",(timePicker.getHour()-12))+":"+String.format("%02d",timePicker.getMinute())+"PM"
                            );
                        }else{
                            binding.SelectTime.setText(timePicker.getHour()+":"+timePicker.getMinute()+"AM");
                        }

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                        calendar.set(Calendar.MINUTE,timePicker.getMinute());
                        calendar.set(Calendar.SECOND,0);
                        calendar.set(Calendar.MILLISECOND,0);
                    }
                });

            }
        });

        binding.SetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent= new Intent(MainActivity.this,AlarmReceiver.class);
                pendingIntent= PendingIntent.getBroadcast(MainActivity.this,0,intent, PendingIntent.FLAG_IMMUTABLE);

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                Toast.makeText(MainActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
            }
        });
        binding.CancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, AlarmReceiver.class);
                pendingIntent=PendingIntent.getBroadcast(MainActivity.this,0, intent, PendingIntent.FLAG_IMMUTABLE);

                if(alarmManager==null){
                    alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                }
                alarmManager.cancel(pendingIntent);
                Toast.makeText(MainActivity.this, "ALARM CANCELLED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1){
            CharSequence name = "alarmClock";
            String desc = "App for Alarm Clock";
            int imp= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidKnowledge",name,imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
