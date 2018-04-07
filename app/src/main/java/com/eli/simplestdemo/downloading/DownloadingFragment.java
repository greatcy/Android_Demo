package com.eli.simplestdemo.downloading;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eli.downloadlib.Storage;
import com.eli.simplestdemo.DLApplication;
import com.eli.simplestdemo.IFragmentInterface;
import com.eli.simplestdemo.R;
import com.eli.simplestdemo.UIRefreshAgent;

import java.util.List;

/**
 * Created by chenjunheng on 2018/2/12.
 */

public class DownloadingFragment extends Fragment implements IFragmentInterface,UIRefreshAgent.ICallBack{
    private RecyclerView mRecyclerView;
    private DownloadingAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.downloading_fragment, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String myDataset[] = new String[1000];
        for (int i = 0; i < myDataset.length; i++) {
            myDataset[i] = "info " + i;
        }
        mAdapter = new DownloadingAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        UIRefreshAgent.getInstance(getActivity()).registerMonitor(this, UIRefreshAgent.MONITOR_DOWNLOADING_FLAG);
        return root;
    }

    @Override
    public String getTitle() {
        return DLApplication.getApplication().getString(R.string.tab_downloading);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onListChange(List<Storage.Torrent> paramList) {
        if (this.mAdapter != null)
        {
            this.mAdapter.setData(paramList);
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
