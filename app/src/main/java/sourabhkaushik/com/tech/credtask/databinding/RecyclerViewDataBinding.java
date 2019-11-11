/*
 * Copyright (c) 2018 Phunware Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package sourabhkaushik.com.tech.credtask.databinding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sourabhkaushik.com.tech.credtask.adapter.DataAdapter;
import sourabhkaushik.com.tech.credtask.adapter.DummyAdapter;
import sourabhkaushik.com.tech.credtask.adapter.DummyAlbumAdapter;
import sourabhkaushik.com.tech.credtask.adapter.PlayListAdapter;
import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.AlbumListModel;
import sourabhkaushik.com.tech.credtask.model.DataModel;

/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class RecyclerViewDataBinding {

    @BindingAdapter({"app:adapter", "app:data"})
    public static void bind(RecyclerView recyclerView, DataAdapter adapter, List<DataModel> data) {
        recyclerView.setAdapter(adapter);
        adapter.updateData(data);
    }

    @BindingAdapter({"app:dummyAdapter", "app:dummyData"})
    public static void bind(RecyclerView recyclerView, DummyAdapter dummyAdapter, List<Album> dummyData) {
        recyclerView.setAdapter(dummyAdapter);
        dummyAdapter.updateData(dummyData);
    }

    @BindingAdapter({"app:dummyAlbumAdapter", "app:dummyAlbumData"})
    public static void bind(RecyclerView recyclerView, DummyAlbumAdapter dummyAdapter, List<Album> dummyData) {
        recyclerView.setAdapter(dummyAdapter);
        dummyAdapter.updateData(dummyData);
    }

    @BindingAdapter({"app:playListAdapter", "app:playListData"})
    public static void bind(RecyclerView recyclerView, PlayListAdapter dummyAdapter, List<DataModel> dummyData) {
        recyclerView.setAdapter(dummyAdapter);
        dummyAdapter.updateData(dummyData);
    }
}
