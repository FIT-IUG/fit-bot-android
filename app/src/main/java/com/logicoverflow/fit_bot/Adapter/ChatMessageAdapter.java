package com.logicoverflow.fit_bot.Adapter;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.logicoverflow.fit_bot.Model.ChatMessage;
import com.logicoverflow.fit_bot.R;

import java.util.List;

/**
 * Created by Fitbot on 11/05/16.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    Context context ;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {

        super(context, R.layout.message_human, data);
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);

        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (!item.isMine() && item.isImage()) return OTHER_IMAGE;
        else return MY_IMAGE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_human, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_chatbot, parent, false);

            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
            textView.setAutoLinkMask(Linkify.WEB_URLS);

        } else if (viewType == OTHER_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_gif_chatbot, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            ImageView gifView = convertView.findViewById(R.id.message_gif);
            Glide.with(convertView).load("https://firebasestorage.googleapis.com/v0/b/fit-bot-936cb.appspot.com/o/instruction1.gif?alt=media&token=72cc3b31-25ed-4e10-a328-593f1f2f75e1").placeholder(R.drawable.progress_bar).error(R.drawable.ic_cloud_error).into(gifView);

            textView.setText(getItem(position).getContent());
            //textView.setAutoLinkMask(Linkify.WEB_URLS);
        } else {
           // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }

        return convertView;
    }






}
