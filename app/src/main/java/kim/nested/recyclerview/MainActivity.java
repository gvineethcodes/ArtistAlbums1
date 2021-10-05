package kim.nested.recyclerview;

import static kim.nested.recyclerview.playService.mediaPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import kim.nested.recyclerview.Adapters.ParentRecyclerViewAdapter;
import kim.nested.recyclerview.Models.ParentModel;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton,floatingActionButton2,floatingActionButton3;
    LocalBroadcastManager lbm;
    SeekBar seekBar;
    TextView textView;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    //public static String totalDuration = " ", currentDuration = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, playService.class));
        }else {
            startService(new Intent(this, playService.class));
        }
        RecyclerView parentRecyclerView = findViewById(R.id.Parent_recyclerView);
        RecyclerView.LayoutManager parentLayoutManager = new LinearLayoutManager(MainActivity.this);
        parentRecyclerView.setLayoutManager(parentLayoutManager);
        RecyclerView.Adapter ParentAdapter;
        ArrayList<ParentModel> parentModelArrayList = new ArrayList<>();
        ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
        parentRecyclerView.setAdapter(ParentAdapter);

        StorageReference mStorageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mStorageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            parentModelArrayList.add(new ParentModel(prefix.getName()));
                        }
//                        ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
                        parentRecyclerView.setAdapter(ParentAdapter);
//                        parentRecyclerView.setHasFixedSize(true);
//                        ParentAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //text=e.getMessage();
                    }
                });

        //set the Categories for each array list set in the `ParentViewHolder`
//        parentModelArrayList.add(new ParentModel("Category1"));
//        parentModelArrayList.add(new ParentModel("Category2"));
//        parentModelArrayList.add(new ParentModel("Category3"));
//        parentModelArrayList.add(new ParentModel("Category4"));
//        parentModelArrayList.add(new ParentModel("Category5"));
//        parentModelArrayList.add(new ParentModel("Category6"));
//        parentRecyclerView = findViewById(R.id.Parent_recyclerView);
//        parentRecyclerView.setHasFixedSize(true);
//        parentLayoutManager = new LinearLayoutManager(this);
//        ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
//        parentRecyclerView.setLayoutManager(parentLayoutManager);
//        parentRecyclerView.setAdapter(ParentAdapter);
//        ParentAdapter.notifyDataSetChanged();



        textView = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar2);
        floatingActionButton2 = findViewById(R.id.floatingActionButton);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton3 = findViewById(R.id.floatingActionButton3);

        sharedpreferences = getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

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
                playService.getInstance().playpause(MainActivity.this);
            }
        });

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().prev(MainActivity.this);
            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().next(MainActivity.this);
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
