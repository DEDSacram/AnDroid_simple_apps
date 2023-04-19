package com.example.hello;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class SecondActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //je orientácia na šírku? vtedy túto aktivitu netreba
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        if (savedInstanceState == null) {
            SecondFragment details = new SecondFragment();
            // odovzdanie indexu vybranej polozky
            details.setArguments(getIntent().getExtras());

            //TODO TIP: API 21 deprecated use this:
            this.getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }

}

