package com.kvest.wearabletest.ui.acticity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kvest.wearabletest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 12/25/15.
 */
public class MessagesAdapter extends BaseAdapter {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private List<Item> dataSet;
    private LayoutInflater layoutInflater;

    public MessagesAdapter(Context context) {
        this.dataSet = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.messages_list_item, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder)convertView.getTag();
        Item item = dataSet.get(position);

        holder.date.setText(DATE_FORMATTER.format(item.date));
        holder.message.setText(item.message);
        convertView.setBackgroundColor(item.confirmed ? Color.WHITE : Color.GRAY);

        return convertView;
    }

    public void addMessage(String message, long date) {
        dataSet.add(new Item(message, date));
        notifyDataSetChanged();
    }

    public void markConfirmed(long date) {
        boolean wasChanged = false;
        for (int i = 0; i < dataSet.size(); ++i) {
            if (dataSet.get(i).date == date) {
                dataSet.get(i).confirmed = true;

                wasChanged = true;
            }
        }

        if (wasChanged) {
            notifyDataSetChanged();
        }
    }

    private static class Item {
        public String message;
        public long date;
        public boolean confirmed;

        public Item(String message, long date) {
            this.message = message;
            this.date = date;
            this.confirmed = false;
        }
    }

    private static class ViewHolder {
        private TextView date;
        private TextView message;

        public ViewHolder(View itemView) {
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
