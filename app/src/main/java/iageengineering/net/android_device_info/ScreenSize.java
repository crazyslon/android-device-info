package iageengineering.net.android_device_info;

public class ScreenSize {
    private final int width;
    private final int height;

    public ScreenSize(int width,int height) {
        this.width = width;
        this.height = height;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}