package com.bcit.com7082.group1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bcit.comp7082.group1.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
            Date today = format.parse(todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            Date tomorrow = format.parse(tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(tomorrow));
        } catch (Exception e) {
        }
    }

    public void cancel(final View v) {
        finish();
    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        EditText latitudeFrom = (EditText) findViewById(R.id.etLatitudeFrom);
        EditText latitudeTo = (EditText) findViewById(R.id.etLatitudeTo);
        EditText longitudeFrom = (EditText) findViewById(R.id.etLongitudeFrom);
        EditText longitudeTo = (EditText) findViewById(R.id.etLongitudeTo);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        i.putExtra("LATITUDEFROM", latitudeFrom.getText() != null ? latitudeFrom.getText().toString() : "");
        i.putExtra("LATITUDETO", latitudeTo.getText() != null ? latitudeTo.getText().toString() : "");
        i.putExtra("LONGITUDEFROM", longitudeFrom.getText() != null ? longitudeFrom.getText().toString() : "");
        i.putExtra("LONGITUDETO", longitudeTo.getText() != null ? longitudeTo.getText().toString() : "");
        setResult(RESULT_OK, i);
        finish();
    }
}