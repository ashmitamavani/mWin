package com.mwin.reward.adapter;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

public class HelpQAParentView implements Parent<HelpQAChildView> {

    // a recipe contains several ingredients
    private String question;
    private List<HelpQAChildView> answer;

    public HelpQAParentView(String question, List<HelpQAChildView> answer) {
        this.question = question;
        this.answer = answer;
    }

    @Override
    public List<HelpQAChildView> getChildList() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
