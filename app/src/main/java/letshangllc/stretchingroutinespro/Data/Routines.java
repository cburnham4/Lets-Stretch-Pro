package letshangllc.stretchingroutinespro.data;

import android.content.Context;

import java.util.ArrayList;

import letshangllc.stretchingroutinespro.R;
import letshangllc.stretchingroutinespro.JavaObjects.RoutineItem;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class Routines {
    Context context;

    static RoutineItem backStretch = new RoutineItem(99999, R.drawable.silhouette, "Back Stretch");
    static RoutineItem morningStretch =  new RoutineItem(99998, R.drawable.wake_up, "Morning Stretch");
    static RoutineItem fullBodyStretch = new RoutineItem(99997, R.drawable.full_body_stretch, "Full Body Stretch");
    static RoutineItem legStretch =new RoutineItem(99996, R.drawable.standing_yoga_stretch,"Leg Stretch");
    static RoutineItem beforeBedStretch = new RoutineItem(99995, R.drawable.lizard_pose_left, "Before Bed Stretch");




    public Routines(){
        routineItems = new ArrayList<>();
        routineItems.add(backStretch);
        routineItems.add(morningStretch);
        routineItems.add(fullBodyStretch);
        routineItems.add(legStretch);
        routineItems.add(beforeBedStretch);
    }
    private static ArrayList<RoutineItem> routineItems;

    public static ArrayList<RoutineItem> getRoutines(){
        return routineItems;
    }
}
