package kim.nested.recyclerview;

import static kim.nested.recyclerview.playService.mediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class MainActivity2 extends AppCompatActivity {

    ListView l;
    FloatingActionButton floatingActionButton,floatingActionButton2,floatingActionButton3;
    LocalBroadcastManager lbm;
    SeekBar seekBar;
    TextView textView,textView2;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        l = findViewById(R.id.list);
        ImageView imageView = findViewById(R.id.imageView);
        textView2 = findViewById(R.id.textView2);

        sharedpreferences = getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        Picasso.get()
                .load(Uri.parse(sharedpreferences.getString("img","")))
                .into(imageView);

        String[] splitStr = sharedpreferences.getString("list","/None/None").split("/");
        textView2.setText(splitStr[1]);

        Set<String> set = sharedpreferences.getStringSet(sharedpreferences.getString("list"," "), null);

        ArrayList<String> tutorials = new ArrayList<>(set);

//        ArrayAdapter<String> arr;
//        arr = new ArrayAdapter<String>(
//                this,
//                R.layout.support_simple_spinner_dropdown_item,
//                tutorials);

        Collections.sort(tutorials);


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, tutorials){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                return view;
            }
        };

        l.setAdapter(adapter);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                keepString("play", sharedpreferences.getString("list"," ")+"/"+adapterView.getItemAtPosition(i));
                playService.getInstance().playpause(MainActivity2.this);
                //Log.i("tttS", sharedpreferences.getString("list"," ")+"/"+adapterView.getItemAtPosition(i));
            }
        });

        textView = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar2);
        floatingActionButton2 = findViewById(R.id.floatingActionButton);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton3 = findViewById(R.id.floatingActionButton3);

    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String str = intent.getStringExtra("key");
                switch (str){
                    case "playImg":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        break;

                    case "pauseImg":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        break;

                    case "PauseMax":
                        floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
                        int total = mediaPlayer.getDuration();
                        seekBar.setMax(total);
                        keepString("totalDuration",""+total % 3600000 / 60000+" : "+total % 3600000 % 60000 / 1000);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + str);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(receiver, new IntentFilter("UI"));

        if(mediaPlayer!=null){
            if(mediaPlayer.getCurrentPosition()>0){
                int total = mediaPlayer.getDuration();
                seekBar.setMax(total);
                keepString("totalDuration",""+total % 3600000 / 60000+" : "+total % 3600000 % 60000 / 1000);
            }
            if(mediaPlayer.isPlaying())
                floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
        }else {
            floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().playpause(MainActivity2.this);
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().prev(MainActivity2.this);
            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().next(MainActivity2.this);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mediaPlayer!=null) {
                    int duration = mediaPlayer.getCurrentPosition();
                    keepString("currentDuration",""+duration % 3600000 / 60000+" : "+duration % 3600000 % 60000 / 1000);
                    seekBar.setProgress(duration);
                }else {
                    keepString("currentDuration","0 : 0");
                    keepString("totalDuration","0 : 0");
                    seekBar.setProgress(0);
                }
                textView.setText(sharedpreferences.getString("currentDuration","0 : 0")+" / "+sharedpreferences.getString("totalDuration","0 : 0")+"\n"+sharedpreferences.getString("text"," "));
                handler.postDelayed(this, 500);
            }
        }, 0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) if(mediaPlayer!=null) mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        lbm.unregisterReceiver(receiver);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lbm.unregisterReceiver(receiver);
    }

    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }
}