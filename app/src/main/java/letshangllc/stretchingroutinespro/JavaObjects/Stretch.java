package letshangllc.stretchingroutinespro.JavaObjects;

import android.graphics.Bitmap;

/**
 * Created by cvburnha on 4/14/2016.
 */
public class Stretch {
    private String name;
    private String instructions;
    private int drawableIndex = 0;
    private int duration;
    private Bitmap bitmap;
    private int id;

    public Stretch(String name, String instructions, int duration, Bitmap bitmap, int id) {
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
        this.bitmap = bitmap;
        this.id = id;
    }

    public Stretch(String name, String instructions, Bitmap bitmap, int duration) {
        this.bitmap = bitmap;
        this.duration = duration;
        this.instructions = instructions;
        this.name = name;
    }

    public Stretch(String name, String instructions, int drawableIndex, int duration) {
        this.name = name;
        this.instructions = instructions;
        this.drawableIndex = drawableIndex;
        this.duration = duration;
    }

    public Stretch(String name, String instructions, int duration) {
        this.name = name;
        this.instructions = instructions;
        this.duration = duration;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getDrawableIndex() {
        return drawableIndex;
    }

    public void setDrawableIndex(int drawableIndex) {
        this.drawableIndex = drawableIndex;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
