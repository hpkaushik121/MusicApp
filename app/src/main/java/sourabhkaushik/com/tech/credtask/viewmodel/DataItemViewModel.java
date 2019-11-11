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
package sourabhkaushik.com.tech.credtask.viewmodel;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.List;

import sourabhkaushik.com.tech.credtask.model.Album;
import sourabhkaushik.com.tech.credtask.model.AlbumListModel;
import sourabhkaushik.com.tech.credtask.model.DataModel;

/**
 * Created by Gregory Rasmussen on 7/26/17.
 */
public class DataItemViewModel extends BaseObservable {
    private DataModel dataModel;

    private Album album;

    public DataItemViewModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataItemViewModel(Album albumListModel) {
        this.album = albumListModel;
    }

    public void setUp() {
        // perform set up tasks, such as adding listeners
    }

    public void tearDown() {
        // perform tear down tasks, such as removing listeners
    }

    @Bindable
    public String getTitle() {
        return !TextUtils.isEmpty(dataModel.getTitle()) ? dataModel.getTitle() : "";
    }

    @Bindable
    public String getAlbumTitle() {
        return !TextUtils.isEmpty(album.getType()) ? album.getType() : "";
    }

    @Bindable
    public String getDescription() {
        return !TextUtils.isEmpty(dataModel.getDescription()) ? dataModel.getDescription() : "";
    }

    @Bindable
    public String getAlbumDescription() {
        return "All";
    }


}
