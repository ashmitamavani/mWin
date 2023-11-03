package com.mwin.reward.adapter;

import com.mwin.reward.async.models.SubMenuListItem;

public class DrawerMenuChildView {

    // a recipe contains several ingredients
    private SubMenuListItem subMenu;

    public DrawerMenuChildView(SubMenuListItem name) {
        subMenu = name;
    }

    public SubMenuListItem getMenu() {
        return subMenu;
    }
}
