package com.codepodium.mythings.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepodium.mythings.GameManager;
import com.codepodium.mythings.R;
import com.codepodium.mythings.model.ObjectFrame;

import java.util.HashMap;

// Adapter class for GridView.
public class MyGridAdapter extends ArrayAdapter<String>
{
    Context context;
    int resource;
    Bitmap bmp;
    int xy;

    HashMap<String, ObjectFrame> objects;

    public MyGridAdapter(Context context, int resource, HashMap<String, ObjectFrame> objects, Bitmap bmp, int XY)
    {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.bmp = bmp;
        this.xy = XY;
    }

    // Returns number of objects to be displayed
    @Override
    public int getCount() {
        return this.objects.size();
    }

    // Called times of number according to "getCount" and returns View that will be displayed.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflating view in case of first time run and when its null
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, null);
        }

        // Getting reference from "convertView"
        TextView tvObjectName = (TextView) convertView.findViewById(R.id.tvObjectName);
        ImageView ivObjectPic = (ImageView) convertView.findViewById(R.id.ivObject);

        String objName = GameManager.getInstance().getObjectKeys().get(position);
        tvObjectName.setText(objName.replace(".png", ""));

        ObjectFrame of = this.objects.get(objName);
        if(of != null) {
            if (this.bmp != null) {
                Bitmap oBmp = Bitmap.createBitmap(this.bmp, of.getX(), of.getY(), of.getW(), of.getH());
                if (oBmp != null) {
                    ivObjectPic.getLayoutParams().height = this.xy;
                    ivObjectPic.getLayoutParams().width = this.xy;
                    ivObjectPic.setImageBitmap(oBmp);
                }
            }
        }

        return convertView;
    }

    // Indicates that every view is unique
    @Override
    public boolean hasStableIds() {
        return true;
    }
}