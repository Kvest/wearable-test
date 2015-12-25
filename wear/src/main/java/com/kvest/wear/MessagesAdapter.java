package com.kvest.wear;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 12/25/15.
 */
public class MessagesAdapter extends WearableListView.Adapter {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private List<Item> dataSet;
    private LayoutInflater layoutInflater;

    public MessagesAdapter(Context context) {
        this.dataSet = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void addMessage(String message, long date) {
        dataSet.add(new Item(message, date));
        notifyItemInserted(dataSet.size() - 1);
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.messages_list_item, null);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        MessageHolder messageHolder = (MessageHolder) holder;

        Item item = dataSet.get(position);

        messageHolder.date.setText(DATE_FORMATTER.format(item.date));
        messageHolder.message.setText(item.message);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private static class Item {
        public String message;
        public long date;

        public Item(String message, long date) {
            this.message = message;
            this.date = date;
        }
    }

    private static class MessageHolder extends WearableListView.ViewHolder {
        private TextView date;
        private TextView message;

        public MessageHolder(View itemView) {
            super(itemView);

            this.date = (TextView) itemView.findViewById(R.id.date);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
