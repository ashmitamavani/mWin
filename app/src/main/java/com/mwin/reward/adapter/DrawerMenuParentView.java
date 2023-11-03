package com.mwin.reward.adapter;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.mwin.reward.async.models.MenuListItem;

import java.util.List;

public class DrawerMenuParentView implements Parent<DrawerMenuChildView> {

    // a recipe contains several ingredients
    private MenuListItem menuListItem;
    private List<DrawerMenuChildView> answer;

    public DrawerMenuParentView(MenuListItem question, List<DrawerMenuChildView> answer) {
        this.menuListItem = question;
        this.answer = answer;
    }

    @Override
    public List<DrawerMenuChildView> getChildList() {
        return answer;
    }

    public MenuListItem getMenu() {
        return menuListItem;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
