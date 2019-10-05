package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private EditText yearView;
    private EditText monthView;
    private EditText dayView;
    private int year = 0;
    private int month = 0;
    private int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendar);
        yearView = findViewById(R.id.year);
        monthView = findViewById(R.id.month);
        dayView = findViewById(R.id.day);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                Toast.makeText(getApplicationContext(), "Today is " + year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickSelectButton(View view){
        year = getIntFromEditText(yearView);
        month = getIntFromEditText(monthView);
        day = getIntFromEditText(dayView);
        if(month < 1 || month > 12 || day < 1 || day > 31){
            Toast.makeText(getApplicationContext(), "It is impossible to select " + year + "-" + (month + 1) + "-" + day, Toast.LENGTH_SHORT).show();
            yearView.setText(0);
            monthView.setText(0);
            dayView.setText(0);
        }else{
            calendarView.setDate(getTimeInMillisFromInts(year, month, day));
        }
    }

    public int getIntFromEditText(EditText text){
        return Integer.parseInt(text.getText().toString());
    }

    public long getTimeInMillisFromInts(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }
}
