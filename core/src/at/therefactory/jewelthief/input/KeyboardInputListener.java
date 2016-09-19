package at.therefactory.jewelthief.input;

import com.badlogic.gdx.Input.TextInputListener;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.I18NKeys;
import at.therefactory.jewelthief.net.HTTP;

import static at.therefactory.jewelthief.constants.Config.PLAYERNAME_MAXLEN;

public class KeyboardInputListener implements TextInputListener {

    @Override
    public void input(String text) {
        if (text.length() <= PLAYERNAME_MAXLEN) {
            HTTP.changeName(JewelThief.getInstance().getPreferences().getString("id"), text);
        } else {
            JewelThief.getInstance().toast(JewelThief.getInstance().getBundle().format(I18NKeys.NAME_MUST_BE_SHORTER_THAN, PLAYERNAME_MAXLEN + 1), true);
        }
    }

    @Override
    public void canceled() {
    }

}
