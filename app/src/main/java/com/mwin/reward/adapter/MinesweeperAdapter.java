package com.mwin.reward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.mwin.reward.R;
import com.mwin.reward.activity.MinesweeperActivity;
import com.mwin.reward.async.models.Item_MinestdataData;
import com.mwin.reward.utils.CommonMethodsUtils;

import java.util.List;


public class MinesweeperAdapter extends RecyclerView.Adapter<MinesweeperAdapter.SavedHolder> {

    public List<Item_MinestdataData> listData;
    private Context context;
    private ClickListener clickListener;

    public MinesweeperAdapter(List<Item_MinestdataData> list, Context context, ClickListener clickListener) {
        this.listData = list;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.raw_minesweeper, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, final int position) {
        try {
            if (MinesweeperActivity.isVisible && listData.get(position).getCount().equals("b")) {
                holder.ivimgbox.setVisibility(View.INVISIBLE);
                holder.ivimgboxlottie.setVisibility(View.VISIBLE);
                holder.ivimgboxlottie.setMinAndMaxFrame(30, 70);
                holder.ivimgboxlottie.playAnimation();
                CommonMethodsUtils.setLottieAnimation(holder.ivimgboxlottie, listData.get(position).getIcon());
            } else {
                holder.ivimgbox.setVisibility(View.VISIBLE);
                holder.ivimgboxlottie.setVisibility(View.INVISIBLE);
                if (listData.get(position).isShown()) {
                    Glide.with(context).load(listData.get(position).getIcon()).into(holder.ivimgbox);
                } else {
                    holder.ivimgbox.setImageResource(R.drawable.box_btn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v, ImageView ivimgbox, LottieAnimationView ivimgboxlottie);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivimgbox;
        private LottieAnimationView ivimgboxlottie;
        private RelativeLayout layoutMain;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            ivimgbox = itemView.findViewById(R.id.ivimgbox);
            ivimgboxlottie = itemView.findViewById(R.id.ivimgboxlottie);
            layoutMain = itemView.findViewById(R.id.layoutMain);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v, ivimgbox, ivimgboxlottie);
        }
    }
}

