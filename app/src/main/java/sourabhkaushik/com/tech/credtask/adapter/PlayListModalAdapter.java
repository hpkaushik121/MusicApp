package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.Utils.AppUtils;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.databinding.PlayListModalAdapterBinding;
import sourabhkaushik.com.tech.credtask.interfaces.MediaPlayerInterfaceInstance;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.model.PlayListModel;
import sourabhkaushik.com.tech.credtask.services.MediaPlayerService;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/16/2019.
 */
public class PlayListModalAdapter extends  RecyclerView.Adapter<PlayListModalAdapter.GenericAdapter> implements PlayListTouchHelperAdapter.ItemTouchHelperContract{

    private PlayListViewModel playListViewModel;
    private Context context;
    private RequestOptions requestOptions;

    public PlayListModalAdapter( PlayListViewModel playListViewModel) {
        this.playListViewModel = playListViewModel;

    }

    @NonNull
    @Override
    public GenericAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        requestOptions = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(16));
        return new GenericAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_modal_adapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GenericAdapter holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return MediaPlayerService.albumList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        DataModel dataModel=MediaPlayerService.albumList.get(MediaPlayerService.positionToplay);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(MediaPlayerService.albumList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(MediaPlayerService.albumList, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        int pos=MediaPlayerService.albumList.indexOf(dataModel);
        playListViewModel.prevPosition=MediaPlayerService.positionToplay=pos;
        Type listType = new TypeToken<List<DataModel>>() {}.getType();
        MediaPlayerInterfaceInstance.getInstance().getMpinterface()
                .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(MediaPlayerService.albumList),listType));

    }


    @Override
    public void onRowSelected(GenericAdapter myViewHolder) {
//        myViewHolder.binding.getRoot().setBackgroundColor(Color.GRAY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myViewHolder.binding.getRoot().setElevation(50);
        }
    }

    @Override
    public void onRowClear(GenericAdapter myViewHolder) {
//        myViewHolder.binding.getRoot().setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myViewHolder.binding.getRoot().setElevation(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        if(position==MediaPlayerService.positionToplay){
            AppUtils.showToast("this song is currently playing");
            notifyDataSetChanged();
            return;

        }
        DataModel dataModel=MediaPlayerService.albumList.get(MediaPlayerService.positionToplay);
        MediaPlayerService.albumList.remove(position);
        MediaPlayerService.positionToplay=MediaPlayerService.albumList.indexOf(dataModel);
        Type listType = new TypeToken<List<DataModel>>() {}.getType();
        MediaPlayerInterfaceInstance
                .getInstance()
                .getMpinterface()
                .onPositionChange((List<DataModel>) new Gson().fromJson(new Gson().toJson(MediaPlayerService.albumList),listType));
        notifyItemRemoved(position);
    }
    private class setImage extends AsyncTask<Object,String,Bitmap> {
        private ImageView viewHolder;

        @Override
        protected Bitmap doInBackground(Object... objects) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art=null;
            BitmapFactory.Options bfo=new BitmapFactory.Options();
            Uri uri= Uri.fromFile(new File((String) objects[0]));
            viewHolder= (ImageView) objects[1];
            mmr.setDataSource(context, uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt){
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

            }
            return art;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
                Glide.with(context).load(bitmap).error(R.drawable.music_placeholder).placeholder(R.drawable.music_placeholder).apply(requestOptions).into(viewHolder);


        }
    }


    public class GenericAdapter extends RecyclerView.ViewHolder{

        private PlayListModalAdapterBinding binding;
        GenericAdapter(@NonNull View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }
        public PlayListModalAdapterBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }

        public void bind(Integer position){
            binding.setPosition(position);
            binding.setPlayListViewModel(playListViewModel);
            binding.title.setText(MediaPlayerService.albumList.get(position).getTitle());

            if(MediaPlayerService.albumList.get(position)
                    .getImage().contains("http://")||MediaPlayerService.albumList.get(position).getImage().contains("https://")){
                Glide.with(binding.getRoot().getContext()).load(MediaPlayerService.albumList.get(position)
                        .getImage()).apply(requestOptions).into(binding.songConver);
            }else {
                new setImage().execute(MediaPlayerService.albumList.get(position).getImage(),binding.songConver);
            }
            binding.titleDescrption.setText(MediaPlayerService.albumList.get(position).getDescription());
            binding.isPlaying.setVisibility(position== MediaPlayerService.positionToplay?View.VISIBLE:View.GONE);
            binding.setPosition(position);
        }
    }
}
