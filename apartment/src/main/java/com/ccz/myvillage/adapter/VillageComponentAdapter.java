package com.ccz.myvillage.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccz.myvillage.R;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.dto.TabMenuItem;

import java.util.List;

public class VillageComponentAdapter extends RecyclerView.Adapter<VillageComponentAdapter.ViewHolder>{

    private RecyclerView parentRecycler;
    private List<Category> categoryList;

    public VillageComponentAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_tabmenu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category item = categoryList.get(position);
        holder.selectView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView selectView;

        public ViewHolder(View itemView) {
            super(itemView);
            selectView = (TextView) itemView.findViewById(R.id.tvSelect);

            itemView.findViewById(R.id.container).setOnClickListener(this);
        }

        public void showSelect() {
            selectView.setTextColor(0xffffffff);
        }

        public void hideSelect() {
            selectView.setTextColor(0xafffffff);
        }

        @Override
        public void onClick(View view) {
            parentRecycler.smoothScrollToPosition(getAdapterPosition());
        }
    }

}
