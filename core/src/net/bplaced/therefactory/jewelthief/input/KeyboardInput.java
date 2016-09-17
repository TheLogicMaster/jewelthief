package net.bplaced.therefactory.jewelthief.input;

import com.badlogic.gdx.Input.TextInputListener;

import net.bplaced.therefactory.jewelthief.JewelThief;
import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.constants.I18NKeys;
import net.bplaced.therefactory.jewelthief.net.HTTP;

public class KeyboardInput implements TextInputListener {

    @Override
    public void input(String text) {
        if (text.length() <= Config.PLAYERNAME_MAXLEN) {
            HTTP.changeName(JewelThief.getInstance().getPreferences().getString("id"), text);
        } else {
            JewelThief.getInstance().toast(JewelThief.getInstance().getBundle().format(I18NKeys.NAME_MUST_BE_SHORTER_THAN, Config.PLAYERNAME_MAXLEN + 1), true);
        }
    }

    @Override
    public void canceled() {
    }

}
