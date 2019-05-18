package com.logicoverflow.fitbot.Adapter;

import android.content.Context;
import android.media.Image;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.logicoverflow.fitbot.Model.ChatMessage;
import com.logicoverflow.fitbot.R;

import java.util.List;

/**
 * Created by Fitbot on 11/05/16.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        super(context, R.layout.message_human, data);
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
            Glide.with(convertView).load(R.drawable.instruction1).into(gifView);

            textView.setText(getItem(position).getContent());
            textView.setAutoLinkMask(Linkify.WEB_URLS);
        } else {
           // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }

//        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "onClick", Toast.LENGTH_LONG).show();
//            }
//        });


        return convertView;
    }
}
