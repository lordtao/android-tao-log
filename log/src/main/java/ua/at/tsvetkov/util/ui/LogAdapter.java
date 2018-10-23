package ua.at.tsvetkov.util.ui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ua.at.tsvetkov.util.R;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final LogColorsSet colorsSet;
    private final ArrayList<LogItem> logItems = new ArrayList<>();

     public static class LogViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTextView;
        public TextView tagTextView;
        public TextView messageTextView;
        public View containerView;

        public LogViewHolder(View view) {
            super(view);
            containerView = view.findViewById(R.id.itemLogLinearLayout);
            dateTextView = view.findViewById(R.id.itemLogDateTextView);
            tagTextView = view.findViewById(R.id.itemLogTagTextView);
            messageTextView = view.findViewById(R.id.itemLogMessageTextView);
        }
    }

    public LogAdapter(LogColorsSet myColorsSet) {
        colorsSet = myColorsSet;
    }

    public void setData(ArrayList<LogItem> logs) {
        logItems.clear();
        logItems.addAll(logs);
//        notifyItemRangeChanged(0,logs.size());
        notifyDataSetChanged();
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);

        LogViewHolder logViewHolder = new LogViewHolder(view);
        return logViewHolder;
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        LogItem logItem = logItems.get(position);
        holder.dateTextView.setText(logItem.getDate());
        holder.tagTextView.setText(logItem.getTag());
        holder.messageTextView.setText(logItem.getMessage());

        LogColor color = colorsSet.getColor(logItem.getLevel());
        holder.containerView.setBackgroundColor(color.getBackground());
        holder.dateTextView.setTextColor(Color.parseColor("#000000"));
        holder.tagTextView.setTextColor(color.getForeground());
        holder.messageTextView.setTextColor(color.getForeground());
    }

    @Override
    public int getItemCount() {
        return logItems.size();
    }

}