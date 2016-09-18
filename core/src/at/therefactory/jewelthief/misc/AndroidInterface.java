package at.therefactory.jewelthief.misc;

/**
 * Created by Christian on 09.06.2016.
 */
public interface AndroidInterface {

    /**
     * Shows a toast message on Android devices.
     * @param message The message to display.
     * @param longDuration If true message is shown for a long time, else a shorter time.
     */
    void toast(String message, boolean longDuration);

}
