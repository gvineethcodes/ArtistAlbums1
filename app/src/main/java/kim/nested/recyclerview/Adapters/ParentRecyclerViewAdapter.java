package kim.nested.recyclerview.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kim.nested.recyclerview.MainActivity;
import kim.nested.recyclerview.Models.ChildModel;
import kim.nested.recyclerview.Models.ParentModel;
import kim.nested.recyclerview.R;

public class ParentRecyclerViewAdapter extends RecyclerView.Adapter<ParentRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ParentModel> parentModelArrayList;
    public Context cxt;
    StorageReference mStorageRef;
    ChildRecyclerViewAdapter childRecyclerViewAdapter;
    private RecyclerView.LayoutManager childLM;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public RecyclerView childRecyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.Movie_category);
            childRecyclerView = itemView.findViewById(R.id.Child_RV);

        }
    }

    public ParentRecyclerViewAdapter(ArrayList<ParentModel> exampleList, Context context) {
        this.parentModelArrayList = exampleList;
        this.cxt = context;

        sharedpreferences = cxt.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return parentModelArrayList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(linearLayoutManager);
        ArrayList<ChildModel> arrayList = new ArrayList<>();
        childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList,holder.childRecyclerView.getContext());
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);
        //holder.childRecyclerView.setHasFixedSize(true);
        String name=" ";

        ParentModel currentItem = parentModelArrayList.get(position);
        name=currentItem.movieCategory();
        holder.category.setText(name);

        mStorageRef = FirebaseStorage.getInstance().getReference(name);
        String finalName = name;
        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Set<String> set = new HashSet<String>();

//                ArrayList<ChildModel> arrayList = new ArrayList<>();
                //Log.i("tttAr", prefix.getName());

                for (StorageReference prefix : listResult.getPrefixes()) {
//                    Log.i("tttA", prefix.getName());
                    prefix.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference item : listResult.getItems()){
//                                Log.i("tttT", item.getName());

//                                Log.i("tttI", prefix.getName());
//                                Log.i("tttP", ""+prefix.getPath());
//                                Log.i("tttS", ""+prefix.getPath().subSequence((prefix.getPath()).indexOf("Album "),(prefix.getPath()).indexOf("/https")));
//                                arrayList.add(new ChildModel((prefix.getName()).replaceAll("[*]","/"),
//                                        ""+prefix.getPath().subSequence((prefix.getPath()).indexOf("Album "),(prefix.getPath()).indexOf("/https"))));

                                if (item.getName().endsWith(".png") || item.getName().endsWith(".jpg") ){
                                    int s = item.getPath().indexOf("/",item.getPath().indexOf("/")+1);
                                    int e = item.getPath().indexOf("/",(item.getPath().indexOf("/",item.getPath().indexOf("/")+1))+1);

//                                    Log.i("tttS", ""+item.getPath().substring(1,s));
//                                    Log.i("tttI", ""+item.getPath().substring(s+1,e));
//                                    Log.i("tttS", ""+item.getPath().indexOf("/",item.getPath().indexOf("/")+1));

                                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            arrayList.add(new ChildModel(""+uri,""+item.getPath().substring(s+1,e),item.getPath().substring(1,s)));
                                            childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList,holder.childRecyclerView.getContext());
                                            holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);
                                            if(sharedpreferences.getString("11","11").equals("11")){
                                                keepString("img",""+uri);
                                                keepString("11","0");
                                            }
                                        }
                                    });
                                }else {
                                    set.add(item.getName());
                                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            keepString(finalName +"/"+prefix.getName()+"/"+item.getName(), ""+uri);
                                        }
                                    });
                                }
                            }
//                            Log.i("tttS", finalName +" "+prefix.getPath().substring(1,prefix.getPath().indexOf("/",prefix.getPath().indexOf("/")+1)));
                            keepStringSet(finalName +"/"+prefix.getName(), set);
                            if(sharedpreferences.getString("1","1").equals("1")){
                                Set<String> set1 = sharedpreferences.getStringSet(finalName +"/"+prefix.getName(), null);
                                ArrayList<String> tutorials = new ArrayList<>(set1);
                                Collections.sort(tutorials);
                                keepString("play", finalName +"/"+prefix.getName()+"/"+tutorials.get(0));
                                keepString("list", finalName +"/"+prefix.getName());
                                keepString("1","0");
                            }
                            set.clear();
                        }
                    });
                }
            }
        });

//        mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                //for (StorageReference prefix : listResult.getPrefixes())
//                {
//                    Log.i("tttA", listResult.getPrefixes().toString());
//
////                    mStorageRef = FirebaseStorage.getInstance().getReference().child(prefix.getName());
////                    mStorageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
////                        @Override
////                        public void onSuccess(ListResult listResult) {
////                            for (StorageReference prefix : listResult.getPrefixes()) {
////                                Log.i("tttI", prefix.getName());
////                            }
////                        }
////                    });
//                }
//            }
//        });



//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        mStorageRef.listAll()
//                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                    @Override
//                    public void onSuccess(ListResult listResult) {
//
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//
//                            Log.i("tttN",prefix.getName());
////                            album = prefix.getName();
////                            //image = prefix.listAll()
////                            Log.i("tttI",image);
//                            mStorageRef = FirebaseStorage.getInstance().getReference().child(name).child(prefix.getName());
//
//                            mStorageRef.listAll()
//                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                                        @Override
//                                        public void onSuccess(ListResult listResult) {
//
//                                            Log.i("tttA",prefix.getName());
//                                            for (StorageReference prefix : listResult.getPrefixes()) {
//                                                image = (prefix.getName()).replaceAll("[*]","/");
//                                                Log.i("tttI",image);
//                                                arrayList.add(new ChildModel(image,album));
//
//                                            }
//
//                                        }
//
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            //text=e.getMessage();
//                                        }
//                                    });
//                        }
//                        //holder.childRecyclerView.setHasFixedSize(true);
//                        //childLM = new LinearLayoutManager(holder.childRecyclerView.getContext());
//                        childRecyclerViewAdapter = new ChildRecyclerViewAdapter(arrayList,holder.childRecyclerView.getContext());
//                        holder.childRecyclerView.setLayoutManager(childLM);
//                        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);
//                        childRecyclerViewAdapter.notifyDataSetChanged();
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //text=e.getMessage();
//                    }
//                });



        // added the first child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category1")) {
//            arrayList.add(new ChildModel(R.drawable.themartian,"Movie Name"));
//            arrayList.add(new ChildModel(R.drawable.moana,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.mov2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.blackp,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood1,"Movie Name"));
//        }
//
//        // added in second child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category2")) {
//            arrayList.add(new ChildModel(R.drawable.moviedubbedinhindi2,"Movie Name"));
//            arrayList.add(new ChildModel(R.drawable.moviedubbedinhindi3,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi1,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi5,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi6,"Movie Name"));
//        }
//
//        // added in third child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category3")) {
//            arrayList.add(new ChildModel(R.drawable.hollywood6,"Movie Name"));
//            arrayList.add(new ChildModel(R.drawable.hollywood5,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood3,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood1,"Movie Name"));
//        }
//
//        // added in fourth child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category4")) {
//            arrayList.add(new ChildModel(R.drawable.bestofoscar6,"Movie Name"));
//            arrayList.add(new ChildModel(R.drawable.bestofoscar5,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar3,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar1,"Movie Name"));
//        }
//
//        // added in fifth child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category5")) {
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.mov2,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood1,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar1,"Movie Name"));
//        }
//
//        // added in sixth child row
//        if (parentModelArrayList.get(position).movieCategory().equals("Category6")) {
//            arrayList.add(new ChildModel(R.drawable.hollywood5,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.blackp,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.bestofoscar4,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.moviedubbedinhindi6,"Movie Name"));
//            arrayList.add(new ChildModel( R.drawable.hollywood1,"Movie Name"));
//            arrayList.add(new ChildModel(R.drawable.bestofoscar6,"Movie Name"));
//        }

        }
    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }
    private void keepStringSet(String keyStr1, Set<String> valueStr1) {
        editor.putStringSet(keyStr1, valueStr1);
        editor.apply();
    }

}