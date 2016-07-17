package letshangllc.stretchingroutinespro.JavaObjects;

import android.graphics.Bitmap;

/**
 * Created by cvburnha on 4/12/2016.
 */
public class RoutineItem {
    int routineIconIndex;
    String name;
    public int id;
    public Bitmap icon;

    public RoutineItem(int id,Bitmap icon,  String name) {
        this.icon = icon;
        this.id = id;
        this.name = name;
    }

    public RoutineItem(int id, int routineIconIndex, String name) {
        this.id = id;
        this.routineIconIndex = routineIconIndex;
        this.name = name;
    }

    public int getRoutineIconIndex() {

        return routineIconIndex;
    }

    public void setRoutineIconIndex(int routineIconIndex) {
        this.routineIconIndex = routineIconIndex;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
