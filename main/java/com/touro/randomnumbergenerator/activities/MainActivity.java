package com.touro.randomnumbergenerator.activities;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.touro.randomnumbergenerator.R;
import com.touro.randomnumbergenerator.databinding.ActivityMainBinding;
import com.touro.randomnumbergenerator.lib.Utils;
import com.touro.randomnumbergenerator.models.RandomNumber;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RandomNumber mRandomNumber;
    private ArrayList<Integer> mNumberHistory;

    TextInputEditText etFrom;
    TextInputEditText etTo;
    TextView textView;

    private final String mKEY = "keyString";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        mRandomNumber = new RandomNumber();
        initializeHistoryList(savedInstanceState,"keyString");

        etFrom = findViewById(R.id.etFrom);;
        etTo = findViewById(R.id.etTo);
        textView = findViewById(R.id.random_number);

        // Issue?
//        if (savedInstanceState != null) {
//            String savedText = savedInstanceState.getString(mKEY);
//            textView.setText(savedText);
//        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFABClick();
            }
        });
    }

    private void handleFABClick() {
        // Get the numbers they inputted
        try {
            int from = Integer.parseInt(etFrom.getText().toString());
            int to = Integer.parseInt(etTo.getText().toString());
            // Send both numbers to model
            mRandomNumber.setFromTo(from, to);
            // Add random number to history and TextView
            int rand = mRandomNumber.getCurrentRandomNumber();
            mNumberHistory.add(rand);
            // Put that number into the destination TextView
            textView.setText(Integer.toString(rand));
        }
        catch (Exception ex)
        {
            // Toast error message
            String error_message = "Please fill in both fields first.";
            Toast.makeText(this, error_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeHistoryList (Bundle savedInstanceState, String key)
    {
        if (savedInstanceState != null) {
            mNumberHistory = savedInstanceState.getIntegerArrayList (key);
        }
        else {
            String history = getDefaultSharedPreferences (this).getString (key, null);
            mNumberHistory = history == null ?
                    new ArrayList<> () : Utils.getNumberListFromJSONString (history);
        }
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(mKEY, mNumberHistory);
        // outState.putString(mKEY, textView.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        String history = Utils.getJSONStringFromNumberList(mNumberHistory);
        if (history != null)
        { editor.putString(mKEY, history); }

        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        View view = findViewById(R.id.main_view);

        // Display history in Dialog Box
        if (id == R.id.show_history) {
                String message = "Numbers Generated: " + mNumberHistory.toString();
                Utils.showInfoDialog(MainActivity.this, "History", message);
                return true;
        }
        else if (id == R.id.clear_history) {
            mNumberHistory.clear();
            Snackbar.make(view, "History Cleared", Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab).setAction("Action", null).show();
            return true;
        }
        else if (id == R.id.about) {
            String aboutText = getString(R.string.about_text);
            Snackbar.make(view, aboutText, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab).setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}