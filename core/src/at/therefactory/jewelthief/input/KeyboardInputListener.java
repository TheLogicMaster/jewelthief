/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Jewel Thief.
 *
 * Jewel Thief is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jewel Thief is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jewel Thief.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.therefactory.jewelthief.input;

import com.badlogic.gdx.Input.TextInputListener;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.I18NKeys;
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.net.HttpServer;

import static at.therefactory.jewelthief.constants.Config.PLAYERNAME_MAXLEN;

class KeyboardInputListener implements TextInputListener {

    @Override
    public void input(String text) {
        if (text.length() <= PLAYERNAME_MAXLEN) {
            text = text.trim().replaceAll(" ", "_");
            HttpServer.changeName(JewelThief.getInstance().getPreferences().getString("id"), text);
            if (text.length() == 0) {
                JewelThief.getInstance().getPreferences().putInteger(PrefsKeys.MY_RANK, -1);
            }
        } else {
            JewelThief.getInstance().toast(JewelThief.getInstance().getBundle().format(I18NKeys.NAME_MUST_BE_SHORTER_THAN, PLAYERNAME_MAXLEN + 1), true);
        }
    }

    @Override
    public void canceled() {
        // do nothing
    }

}
