package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.DummyItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.databinding.MainViewRecyclerBinding;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.viewmodel.DataItemViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.DataViewModel;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class DummyAlbumAdapter extends RecyclerView.Adapter<DummyAlbumAdapter.DummAlbumViewHolder>{

    public List<Album> albums;
    public DataViewModel dataViewModel;
    private Context mContext;
    private int listPosition;
    private RequestOptions requestOptions;
    public DummyAlbumAdapter(DataViewModel model,int lstPosition) {
        albums=new ArrayList<>();
        this.listPosition=lstPosition;
        this.dataViewModel=model;
    }

    @NonNull
    @Override
    public DummAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext=parent.getContext();
        return new DummAlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_view_recycler,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DummAlbumViewHolder holder, int position) {
        Album album=albums.get(position);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth( 5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(16));


        if(album.getCoverImage().contains("http://")||album.getCoverImage().contains("https://")){
            Glide.with(mContext).load(album.getCoverImage()).apply(requestOptions).into(holder.binding.coverImage);
        }else {

            new setImage().execute(album.getCoverImage(),holder);

        }
        holder.bind(dataViewModel,album,position);
    }

    @Override
    public int getItemCount() {
        return albums==null?0:albums.size();
    }
    public void updateData(@Nullable List<Album> data) {
        this.albums=data;
        notifyDataSetChanged();
    }

    private class setImage extends AsyncTask<Object,String,Bitmap> {
        private DummAlbumViewHolder viewHolder;

        @Override
        protected Bitmap doInBackground(Object... objects) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art=null;
            BitmapFactory.Options bfo=new BitmapFactory.Options();
            Uri uri= Uri.fromFile(new File((String) objects[0]));
            viewHolder= (DummAlbumViewHolder) objects[1];
            mmr.setDataSource(mContext, uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt){
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

            }
            return art;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

                Glide.with(mContext).load(bitmap).error(R.drawable.music_placeholder).placeholder(R.drawable.music_placeholder).apply(requestOptions).into(viewHolder.binding.coverImage);


        }
    }

    public class DummAlbumViewHolder extends RecyclerView.ViewHolder{
        MainViewRecyclerBinding binding;


        public DummAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
        }

        MainViewRecyclerBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }
        void bind(DataViewModel dataViewModel, Album album, Integer position) {
            if (binding == null) {
                binding = DataBindingUtil.bind(itemView);
            }
            binding.setDataViewModel(dataViewModel);
            binding.setAlbumModel(album);
            binding.setListPosition(listPosition);
            binding.setPosition(position);
        }

    }
}
