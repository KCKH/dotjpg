/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.model;

/**
 * Created by alashov on 05/08/15.
 */
public class ListItem {
    public final String text;
    public final int icon;

    public ListItem(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return text;
    }
}
