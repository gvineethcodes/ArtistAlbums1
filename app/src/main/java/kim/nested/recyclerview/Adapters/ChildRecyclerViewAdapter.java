package kim.nested.recyclerview.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kim.nested.recyclerview.MainActivity;
import kim.nested.recyclerview.MainActivity2;
import kim.nested.recyclerview.Models.ChildModel;
import kim.nested.recyclerview.R;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {
    public ArrayList<ChildModel> childModelArrayList;
    Context cxt;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView heroImage;
        public TextView movieName;

        public MyViewHolder(View itemView) {
            super(itemView);
            heroImage = itemView.findViewById(R.id.hero_image);
            movieName = itemView.findViewById(R.id.movie_name);

        }
    }

    public ChildRecyclerViewAdapter(ArrayList<ChildModel> arrayList, Context mContext) {
        this.cxt = mContext;
        this.childModelArrayList = arrayList;

        sharedpreferences = cxt.getSharedPreferences("" + R.string.app_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChildModel currentItem = childModelArrayList.get(position);
//        try {
//            URL url = new URL(currentItem.getHeroImage());
//            //URL url = new URL("https://lh3.googleusercontent.com/7Nz4Fic8IguijBDMBVsuRF7ZIbhNJmlqCksR5MV0djhHCvoo699St3EAgdJ8U2ZZqxk8jX8NjBFwFaCYwOUIbaoB02IBm0RRnAJ-2GSRyXLyoDCUe2Xf4DmFn86jzyfW=w1280");
//            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
//            urlcon.setDoInput(true);
//            urlcon.connect();
//            InputStream in = urlcon.getInputStream();
//            Bitmap mIcon = BitmapFactory.decodeStream(in);
//            holder.heroImage.setImageBitmap(mIcon);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//            URL url = new URL(currentItem.getHeroImage());
//            //URL url = new URL("https://drive.google.com/file/d/1Aai5BOpCDW941fHOAA-clQY1fL70DpSn/view?usp=sharing");
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            holder.heroImage.setImageBitmap(bmp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Picasso.get()
                .load(Uri.parse(currentItem.getHeroImage()))
                .into(holder.heroImage);
//        Glide.with(cxt)
//                .load(Uri.parse(currentItem.getHeroImage()))
//                .into(holder.heroImage);
//        holder.heroImage.setImageResource(currentItem.getHeroImage());
//        holder.heroImage.setImageURI(Uri.parse(currentItem.getHeroImage()));

        holder.movieName.setText(currentItem.getMovieName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChildModel currentItem = childModelArrayList.get(holder.getAdapterPosition());
                keepString("list", currentItem.getArtist()+"/"+currentItem.getMovieName());
                keepString("img", currentItem.getHeroImage());

                cxt.startActivity(new Intent(cxt, MainActivity2.class));

                //Toast.makeText(cxt, currentItem.getArtist()+currentItem.getMovieName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }

    private void keepString(String keyStr1, String valueStr1) {
        editor.putString(keyStr1, valueStr1);
        editor.apply();
    }
}