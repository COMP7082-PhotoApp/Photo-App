package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    public EditText yearView;
    public EditText monthView;
    public EditText dayView;
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
                CalendarActivity.this.year = year;
                CalendarActivity.this.month = month + 1;
                CalendarActivity.this.day = dayOfMonth;
                Toast.makeText(getApplicationContext(), "Select Date is " + year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickSelectButton(View view){
        if(!yearView.getText().toString().equalsIgnoreCase("") && !yearView.getText().toString().equals("") && !yearView.getText().toString().equals("")){
            year = getIntFromEditText(yearView);
            month = getIntFromEditText(monthView);
            day = getIntFromEditText(dayView);
        }
        if(month < 1 || month > 12 || day < 1 || day > 31){
            Toast.makeText(getApplicationContext(), "It is impossible to select " + year + "-" + (month + 1) + "-" + day, Toast.LENGTH_SHORT).show();
            yearView.setText(0);
            monthView.setText(0);
            dayView.setText(0);
        }else{
            calendarView.setDate(getTimeInMillisFromInts(year, month, day));
            Intent intent = new Intent();
            if(month < 10){
                intent.putExtra("selectDate", year + "-0" + month + "-" + day);
            }else{
                intent.putExtra("selectDate", year + "-" + month + "-" + day);
            }

            setResult(1, intent);
            finish();
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
