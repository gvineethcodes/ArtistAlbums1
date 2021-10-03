package kim.nested.recyclerview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import kim.nested.recyclerview.Adapters.ParentRecyclerViewAdapter;
import kim.nested.recyclerview.Models.ParentModel;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter ParentAdapter;
    StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ParentModel> parentModelArrayList = new ArrayList<>();

        RecyclerView parentRecyclerView = findViewById(R.id.Parent_recyclerView);
        RecyclerView.LayoutManager parentLayoutManager = new LinearLayoutManager(MainActivity.this);
        parentRecyclerView.setLayoutManager(parentLayoutManager);
        ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
        parentRecyclerView.setAdapter(ParentAdapter);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mStorageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {

                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            parentModelArrayList.add(new ParentModel(prefix.getName()));
                        }
//                        ParentAdapter = new ParentRecyclerViewAdapter(parentModelArrayList, MainActivity.this);
//                        parentRecyclerView.setAdapter(ParentAdapter);
//                        parentRecyclerView.setHasFixedSize(true);
                        ParentAdapter.notifyDataSetChanged();
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
    }
}
