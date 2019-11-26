package sourabhkaushik.com.tech.credtask.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListAdapterViewHolder>{

    private List<DataModel> data;
    private PlayMusicViewModel playMusicViewModel;
    private Context context;

    public PlayListAdapter(PlayMusicViewModel playMusicViewModel) {
        this.playMusicViewModel = playMusicViewModel;
        data=new ArrayList<>();
    }

    @NonNull
    @Override
    public PlayListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new PlayListAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapterViewHolder holder, int position) {
        DataModel dataModel=data.get(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        holder.getBinding().coverImage.bringToFront();
        if(dataModel.getImage().contains("http://")||dataModel.getImage().contains("https://")){
            Glide.with(context).load(dataModel.getImage()).apply(requestOptions).into(holder.binding.coverImage);
        }else {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art;
            BitmapFactory.Options bfo=new BitmapFactory.Options();
            Uri uri= Uri.fromFile(new File(dataModel.getImage()));
            mmr.setDataSource(context, uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt){
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                Glide.with(context).load(art).apply(requestOptions).into(holder.binding.coverImage);
            }

        }
        holder.bind(dataModel,position);
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public void updateData(List<DataModel> model){
        this.data=model;
        notifyDataSetChanged();

    }
    public class PlayListAdapterViewHolder extends RecyclerView.ViewHolder{
        PlayListItemLayoutBinding binding;

        public PlayListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
        }

        public PlayListItemLayoutBinding getBinding(){
            if(binding==null){
                binding= DataBindingUtil.bind(itemView);
            }
            return binding;
        }

        public void bind(DataModel model,Integer position){
            binding.setDataModel(model);
            binding.setPlayMusicViewModel(playMusicViewModel);
            binding.setPosition(position);
        }
    }
}
