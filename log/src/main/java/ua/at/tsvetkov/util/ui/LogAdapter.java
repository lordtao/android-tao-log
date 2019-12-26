package ua.at.tsvetkov.util.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.at.tsvetkov.util.R;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
   private String[] mDataset;

   public static class LogViewHolder extends RecyclerView.ViewHolder {

      public TextView dateTextView;
      public TextView tagTextView;
      public TextView messageTextView;

      public LogViewHolder(View v) {
         super(v);
         dateTextView = v.findViewById(R.id.itemLogDateTextView);
         tagTextView = v.findViewById(R.id.itemLogTagTextView);
         messageTextView = v.findViewById(R.id.itemLogMessageTextView);
      }
   }

   public LogAdapter(String[] myDataset) {
      mDataset = myDataset;
   }

   @NonNull
   @Override
   public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
      return new LogViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
      holder.dateTextView.setText(mDataset[position]);
   }

   @Override
   public int getItemCount() {
      return mDataset.length;
   }

}