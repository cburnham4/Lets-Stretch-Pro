package letshangllc.stretchingroutinespro.adapaters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import letshangllc.stretchingroutinespro.JavaObjects.RoutineItem;
import letshangllc.stretchingroutinespro.R;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class RoutineListAdapter extends ArrayAdapter<RoutineItem> {

    private static class ViewHolder {
        CircleImageView routineImg;
        TextView routineName;

    }

    public RoutineListAdapter(Context context, ArrayList<RoutineItem> items) {
        super(context, R.layout.item_routine, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RoutineItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_routine, parent, false);
            viewHolder.routineImg = (CircleImageView) convertView.findViewById(R.id.imgRoutine);
            viewHolder.routineName = (TextView) convertView.findViewById(R.id.tv_routine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object

        if(item.icon == null){
            viewHolder.routineImg.setImageDrawable(ContextCompat.getDrawable(getContext(), item.getRoutineIconIndex()));
        }else{
            viewHolder.routineImg.setImageBitmap(item.icon);
        }

        viewHolder.routineName.setText(item.getName());


        // Return the completed view to render on screen
        return convertView;
    }
}
