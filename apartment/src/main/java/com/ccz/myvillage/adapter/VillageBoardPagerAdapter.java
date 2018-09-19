package com.ccz.myvillage.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;

import com.ccz.myvillage.activity.pager.NoticeBoardPagerFragment;
import com.ccz.myvillage.dto.Category;

import java.util.List;

public class VillageBoardPagerAdapter extends FragmentPagerAdapter {
    private RecyclerView parentRecycler;
    private List<Category> categoryList;

    private NoticeBoardPagerFragment[] noticeBoardPagers;

    public VillageBoardPagerAdapter(FragmentManager fm, List<Category> categoryList) {
        super(fm);
        this.categoryList = categoryList;
        noticeBoardPagers = new NoticeBoardPagerFragment[categoryList.size()];
    }

    @Override
    public Fragment getItem(int position) {
        position = position % noticeBoardPagers.length;
        if(noticeBoardPagers[position] != null)
            return noticeBoardPagers[position];
        return initBoardPager(position);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    private NoticeBoardPagerFragment initBoardPager(int position) {
        Bundle args = new Bundle();
        args.putSerializable("category", categoryList.get(position));

        noticeBoardPagers[position] = new NoticeBoardPagerFragment();
        noticeBoardPagers[position].setArguments(args);
        return noticeBoardPagers[position];
    }
}
