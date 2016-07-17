package letshangllc.stretchingroutinespro.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import letshangllc.stretchingroutinespro.JavaObjects.Stretch;
import letshangllc.stretchingroutinespro.R;

/**
 * Created by cvburnha on 6/21/2016.
 */
public class StretchesAdapter extends ArrayAdapter<Stretch> {

    private static class ViewHolder {
        CircleImageView stretchImg;
        TextView tvName;
        TextView tvDuration;
        TextView tvInstruction;
    }

    public StretchesAdapter(Context context, ArrayList<Stretch> stretches){
        super(context, R.layout.item_stretch, stretches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Stretch item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_stretch, parent, false);
            viewHolder.stretchImg = (CircleImageView) convertView.findViewById(R.id.img_stretch);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvStretchName);
            viewHolder.tvDuration = (TextView) convertView.findViewById(R.id.tvStretchDuration);
            viewHolder.tvInstruction = (TextView) convertView.findViewById(R.id.tvStretchInstruction);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object

        if(item.bitmap != null){
            viewHolder.stretchImg.setImageBitmap(item.bitmap);
        }

        viewHolder.tvName.setText(item.getName());
        viewHolder.tvDuration.setText(String.format(Locale.US, "%d s", item.getDuration()));
        viewHolder.tvInstruction.setText(item.getInstructions());


        // Return the completed view to render on screen
        return convertView;
    }



}
