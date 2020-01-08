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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.boommenu.Util;
import sourabhkaushik.com.tech.credtask.databinding.PlayListItemLayoutBinding;
import sourabhkaushik.com.tech.credtask.model.DataModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayListViewModel;
import sourabhkaushik.com.tech.credtask.viewmodel.PlayMusicViewModel;

/**
 * Created by Sourabh kaushik on 11/5/2019.
 */
public class PlayListAdapter extends PagerAdapter {

    private List<DataModel> data;
    private PlayListItemLayoutBinding binding;
    private PlayMusicViewModel playMusicViewModel;
    private Context context;
    private RequestOptions requestOptions;

    public PlayListAdapter(PlayMusicViewModel playMusicViewModel) {
        this.playMusicViewModel = playMusicViewModel;
        data = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.play_list_item_layout, container, false);
        DataModel dataModel = data.get(position);
        binding.setDataModel(data.get(position));
        binding.setPlayMusicViewModel(playMusicViewModel);
        binding.setPosition(position);
        if (container.getId() == R.id.viewpagerTop) {
            requestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), new RoundedCorners(16))
                    .placeholder(R.drawable.music_placeholder);
        } else {
            requestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), new BlurTransformation(Util.blurIndex, Util.sampling)).placeholder(R.color.black);
        }


        binding.coverImage.bringToFront();
        if (dataModel.getImage().contains("http://") || dataModel.getImage().contains("https://")) {
            Glide.with(context).load(dataModel.getImage()).apply(requestOptions).into(binding.coverImage);
        } else {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            byte[] rawArt;
            Bitmap art = null;
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            Uri uri = Uri.fromFile(new File(dataModel.getImage()));
            mmr.setDataSource(context, uri);
            rawArt = mmr.getEmbeddedPicture();
            if (null != rawArt) {
                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

            }
            Glide.with(context).load(art).apply(requestOptions).into(binding.coverImage);
        }
        View view = binding.getRoot();
        container.addView(view);
        return view;
    }

    public void updateData(List<DataModel> model) {
        this.data = model;
        notifyDataSetChanged();

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
