package kim.nested.recyclerview;

import static kim.nested.recyclerview.playService.mediaPlayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, playService.class));
        }else {
            startService(new Intent(this, playService.class));
        }

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.getInstance().playpause(MainActivity.this);
                if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                    floatingActionButton.setImageResource(R.drawable.ic_baseline_pause_24);
                }else {
                    floatingActionButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }
        });
    }
}
