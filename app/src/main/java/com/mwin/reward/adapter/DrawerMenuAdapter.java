package com.mwin.reward.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mwin.reward.R;
import com.mwin.reward.activity.HomeActivity;
import com.mwin.reward.utils.CommonMethodsUtils;

import java.util.List;

public class DrawerMenuAdapter extends ExpandableRecyclerAdapter<DrawerMenuParentView, DrawerMenuChildView, DrawerMenuAdapter.SideMenuParentViewHolder, DrawerMenuAdapter.SideMenuChildViewHolder> {
    private List<DrawerMenuParentView> mRecipeList;
    private LayoutInflater mInflater;

    private Activity context;

    public DrawerMenuAdapter(Activity context, @NonNull List<DrawerMenuParentView> recipeList) {
        super(recipeList);
        mRecipeList = recipeList;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    // onCreate ...
    @Override
    public SideMenuParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView = mInflater.inflate(R.layout.item_drawer_menu, parentViewGroup, false);
        return new SideMenuParentViewHolder(recipeView);
    }

    @Override
    public SideMenuChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView = mInflater.inflate(R.layout.item_drawer_sub_menu, childViewGroup, false);
        return new SideMenuChildViewHolder(ingredientView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(@NonNull SideMenuParentViewHolder recipeViewHolder, int parentPosition, @NonNull DrawerMenuParentView recipe) {
        recipeViewHolder.bind(recipe);
    }

    @Override
    public void onBindChildViewHolder(@NonNull SideMenuChildViewHolder ingredientViewHolder, int parentPosition, int childPosition, @NonNull DrawerMenuChildView ingredient) {
        ingredientViewHolder.bind(ingredient);
    }

    public class SideMenuChildViewHolder extends ChildViewHolder {
        RelativeLayout relTask;
        ImageView ivIcon;
        TextView txtTitle;
        TextView txtLabel;
        LottieAnimationView ivLottie;
        ProgressBar probr;

        public SideMenuChildViewHolder(View itemView) {
            super(itemView);
            relTask = itemView.findViewById(R.id.relTask);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            probr = itemView.findViewById(R.id.probr);
        }

        public void bind(DrawerMenuChildView objMenu) {
            try {
                txtTitle.setText(objMenu.getMenu().getTitle());
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getTitleColor())) {
                    txtTitle.setTextColor(Color.parseColor(objMenu.getMenu().getTitleColor()));
                }
                if (objMenu.getMenu().getIcon() != null && objMenu.getMenu().getIcon().endsWith(".json")) {
                    ivIcon.setVisibility(android.view.View.INVISIBLE);
                    ivLottie.setVisibility(android.view.View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ivLottie, objMenu.getMenu().getIcon());
                    ivLottie.setRepeatCount(LottieDrawable.INFINITE);
                    probr.setVisibility(View.GONE);
                } else {
                    ivIcon.setVisibility(android.view.View.VISIBLE);
                    ivLottie.setVisibility(android.view.View.INVISIBLE);
                    Glide.with(context).asBitmap()
                            .load(objMenu.getMenu().getIcon())
                            .override((int) ivIcon.getWidth(), (int) ivIcon.getHeight())
                            .addListener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    probr.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(ivIcon);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabel())) {
                    txtLabel.setVisibility(android.view.View.VISIBLE);
                    txtLabel.setText(objMenu.getMenu().getLabel());
                } else {
                    txtLabel.setVisibility(android.view.View.GONE);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabelColor())) {
                    txtLabel.setTextColor(Color.parseColor(objMenu.getMenu().getLabelColor()));
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabelBGColor())) {
                    txtLabel.getBackground().setTint(Color.parseColor(objMenu.getMenu().getLabelBGColor()));
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getIsBlink())) {
                    if (objMenu.getMenu().getIsBlink().matches("1")) {
                        relTask.setBackground(context.getDrawable(R.drawable.border_accent_rectangle));
                    } else {
                        relTask.setBackground(null);
                    }
                } else {
                    relTask.setBackground(null);
                }

                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getIsBlink())) {
                    if (objMenu.getMenu().getIsBlink().matches("1")) {
                        relTask.setVisibility(android.view.View.VISIBLE);
                        Animation anim = new AlphaAnimation(0.2f, 1.0f);
                        anim.setDuration(400); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        relTask.startAnimation(anim);
                    }
                }

                relTask.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        ((HomeActivity) context).closeDrawer();
                        CommonMethodsUtils.Redirect(context, objMenu.getMenu().getScreenNo(), objMenu.getMenu().getTitle(), objMenu.getMenu().getUrl(), objMenu.getMenu().getGameId(), objMenu.getMenu().getOfferId(), objMenu.getMenu().getIcon());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SideMenuParentViewHolder extends ParentViewHolder {
        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;
        ImageView ivArrow;
        RelativeLayout relTask;
        ImageView ivIcon;
        TextView txtTitle;
        TextView txtLabel;
        LottieAnimationView ivLottie;
        ProgressBar probr;

        public SideMenuParentViewHolder(View itemView) {
            super(itemView);
            relTask = itemView.findViewById(R.id.relTask);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLabel = itemView.findViewById(R.id.txtLabel);
            ivLottie = itemView.findViewById(R.id.ivLottie);
            probr = itemView.findViewById(R.id.probr);
        }

        public void bind(DrawerMenuParentView objMenu) {
            try {
                ivArrow.setVisibility(objMenu.getMenu().getSubMenuList() != null && objMenu.getMenu().getSubMenuList().size() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
                txtTitle.setText(objMenu.getMenu().getTitle());
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getTitleColor())) {
                    txtTitle.setTextColor(Color.parseColor(objMenu.getMenu().getTitleColor()));
                }
                if (objMenu.getMenu().getIcon() != null && objMenu.getMenu().getIcon().endsWith(".json")) {
                    ivIcon.setVisibility(android.view.View.INVISIBLE);
                    ivLottie.setVisibility(android.view.View.VISIBLE);
                    CommonMethodsUtils.setLottieAnimation(ivLottie, objMenu.getMenu().getIcon());
                    ivLottie.setRepeatCount(LottieDrawable.INFINITE);
                    probr.setVisibility(View.GONE);
                } else {
                    ivIcon.setVisibility(android.view.View.VISIBLE);
                    ivLottie.setVisibility(android.view.View.INVISIBLE);
                    Glide.with(context).asBitmap()
                            .load(objMenu.getMenu().getIcon())
                            .override((int) ivIcon.getWidth(), (int) ivIcon.getHeight())
                            .addListener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    probr.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(ivIcon);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabel())) {
                    txtLabel.setVisibility(android.view.View.VISIBLE);
                    txtLabel.setText(objMenu.getMenu().getLabel());
                } else {
                    txtLabel.setVisibility(android.view.View.GONE);
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabelColor())) {
                    txtLabel.setTextColor(Color.parseColor(objMenu.getMenu().getLabelColor()));
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getLabelBGColor())) {
                    txtLabel.getBackground().setTint(Color.parseColor(objMenu.getMenu().getLabelBGColor()));
                }
                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getIsBlink())) {
                    if (objMenu.getMenu().getIsBlink().matches("1")) {
                        relTask.setBackground(context.getDrawable(R.drawable.border_accent_rectangle));
                    } else {
                        relTask.setBackground(null);
                    }
                } else {
                    relTask.setBackground(null);
                }

                if (!CommonMethodsUtils.isStringNullOrEmpty(objMenu.getMenu().getIsBlink())) {
                    if (objMenu.getMenu().getIsBlink().matches("1")) {
                        relTask.setVisibility(android.view.View.VISIBLE);
                        Animation anim = new AlphaAnimation(0.2f, 1.0f);
                        anim.setDuration(400); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        relTask.startAnimation(anim);
                    }
                }

                if (objMenu.getMenu().getSubMenuList() == null || objMenu.getMenu().getSubMenuList().size() == 0) {
                    relTask.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(android.view.View v) {
                            ((HomeActivity) context).closeDrawer();
                            CommonMethodsUtils.Redirect(context, objMenu.getMenu().getScreenNo(), objMenu.getMenu().getTitle(), objMenu.getMenu().getUrl(), objMenu.getMenu().getGameId(), objMenu.getMenu().getOfferId(), objMenu.getMenu().getIcon());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (expanded) {
                    ivArrow.setRotation(ROTATED_POSITION);
                } else {
                    ivArrow.setRotation(INITIAL_POSITION);
                }
            }
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                RotateAnimation rotateAnimation;
                if (expanded) { // rotate clockwise
                    rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                            INITIAL_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                } else { // rotate counterclockwise
                    rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                            INITIAL_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                }
                rotateAnimation.setDuration(200);
                rotateAnimation.setFillAfter(true);
                ivArrow.startAnimation(rotateAnimation);
            }
        }
    }
}