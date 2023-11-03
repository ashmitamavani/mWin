package com.mwin.reward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mwin.reward.R;
import com.mwin.reward.async.models.Alphabet_Item;

import java.util.List;


public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.SavedHolder> {
    public List<Alphabet_Item> data;
    private Context context;

    private ClickListener clickListener;

    public AlphabetAdapter(List<Alphabet_Item> list, Context context, ClickListener clickListener) {
        this.data = list;
        this.context = context;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public SavedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_alphabet, parent, false);
        return new SavedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedHolder holder, int position) {
//        try {

        holder.tvAlphabet.setText(data.get(position).getValue());

//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v, TextView textView);
    }

    public class SavedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvAlphabet;

        public SavedHolder(@NonNull View itemView) {
            super(itemView);
            tvAlphabet = itemView.findViewById(R.id.tvAlphabet);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getLayoutPosition(), v, tvAlphabet);
        }
    }
}
