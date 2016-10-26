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

package at.therefactory.jewelthief.misc;

public interface AndroidInterface {

    /**
     * Shows a toast message on Android devices.
     *
     * @param message The message to display.
     * @param longDuration If true message is shown for a long time, else a shorter time.
     */
    void toast(String message, boolean longDuration);

    /**
     * Tries to return the version name set in the build.gradle file.
     * If that fails the Config.VERSION_NAME is returned.
     *
     * @return The version name set in the build.gradle file, or if that fails the Config.VERSION_NAME.
     */
    String getVersionName();

}
