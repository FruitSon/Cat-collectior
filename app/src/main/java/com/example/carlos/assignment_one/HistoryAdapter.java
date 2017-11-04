package com.example.carlos.assignment_one;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by RZ on 11/3/17.
 */

public class HistoryAdapter extends ArrayAdapter<HistoryInfo> {
    private int resourceId;
    /**
     *context:当前活动上下文
     *textViewResourceId:ListView子项布局的ID
     *objects：要适配的数据
     */
    public HistoryAdapter(Context context, int textViewResourceId,
                               List<HistoryInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryInfo hi = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        NetworkImageView catImg = (NetworkImageView) view.findViewById(R.id.cat_image);
        TextView catName = (TextView) view.findViewById(R.id.cat_name);
        TextView posLng = (TextView) view.findViewById(R.id.cat_pos_lng);
        TextView posLat = (TextView) view.findViewById(R.id.cat_pos_lat);
        ImageView catPetted = (ImageView) view.findViewById(R.id.cat_petted);

        Log.d("url is:",hi.getImageId());
        catImg.setImageUrl(hi.getImageId(), MyVolleySingleton.getInstance(getContext()).getImageLoader());

        catName.setText(hi.getName());
        posLng.setText(hi.getLng()+"");
        posLat.setText(hi.getLat()+"");
        catPetted.setImageResource(hi.getPetted()?R.drawable.pet_yellow:R.drawable.pet_grey);
        return view;
    }

}
