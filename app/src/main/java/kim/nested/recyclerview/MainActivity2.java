package kim.nested.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity2 extends AppCompatActivity {

    ListView l;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        l = findViewById(R.id.list);

        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;

        sharedpreferences = getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        Set<String> set = sharedpreferences.getStringSet(sharedpreferences.getString("list"," "), null);

        ArrayList<String> tutorials = new ArrayList<>(set);

        ArrayAdapter<String> arr;
        arr = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                tutorials);
        l.setAdapter(arr);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("play", sharedpreferences.getString("list"," ")+"/"+adapterView.getItemAtPosition(i));
                editor.apply();

                playService.getInstance().playpause(MainActivity2.this);
                Log.i("tttS", sharedpreferences.getString("list"," ")+"/"+adapterView.getItemAtPosition(i));
            }
        });
    }
}