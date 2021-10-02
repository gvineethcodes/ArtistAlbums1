package kim.nested.recyclerview.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kim.nested.recyclerview.Models.ChildModel;
import kim.nested.recyclerview.R;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {
    public ArrayList<ChildModel> childModelArrayList;
    Context cxt;

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
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recyclerview_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChildModel currentItem = childModelArrayList.get(position);
        try {
            URL url = new URL(currentItem.getHeroImage());
            //URL url = new URL("https://lh3.googleusercontent.com/7Nz4Fic8IguijBDMBVsuRF7ZIbhNJmlqCksR5MV0djhHCvoo699St3EAgdJ8U2ZZqxk8jX8NjBFwFaCYwOUIbaoB02IBm0RRnAJ-2GSRyXLyoDCUe2Xf4DmFn86jzyfW=w1280");
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            holder.heroImage.setImageBitmap(mIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //holder.heroImage.setImageResource(currentItem.getHeroImage());
        holder.movieName.setText(currentItem.getMovieName());

    }


    @Override
    public int getItemCount() {
        return childModelArrayList.size();
    }
}