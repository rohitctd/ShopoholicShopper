package com.shopoholic.coachmark;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.shopoholic.R;
import com.shopoholic.utils.AppSharedPreference;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class CoachMarksImpl {
    private static CoachMarksImpl coachMarks = null;

    /**
     * This method is used to provide the instance of Network Connection Class
     * @return instance of Network Connection Class
     */
    public static CoachMarksImpl getInstance() {
        if (coachMarks == null) {
            return new CoachMarksImpl();
        } else {
            return coachMarks;
        }
    }

    /**
     * function to set home coach marks
     * @param filter
     * @param map
     * @param search
     */
    public void setHomeScreenCoachMarks(Activity mActivity, View menu, View search, View map, View filter, View product, View offer, View service, View location, View category) {
        GuideView.Builder categoryBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.category_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(category).setGuideListener(view -> {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_HOME_SEEN, true);
                });

        GuideView.Builder locationBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.location_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(location).setGuideListener(view -> categoryBuilder.build().show());

        GuideView.Builder serviceBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.service_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(service).setGuideListener(view -> locationBuilder.build().show());

        GuideView.Builder offerBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.offer_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(offer).setGuideListener(view -> serviceBuilder.build().show());

        GuideView.Builder productBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.product_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(product).setGuideListener(view -> offerBuilder.build().show());

        GuideView.Builder filterBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.filter_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(filter).setGuideListener(view -> productBuilder.build().show());

        GuideView.Builder mapBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.map_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(map).setGuideListener(view -> filterBuilder.build().show());

        GuideView.Builder searchBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.search_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(search).setGuideListener(view -> mapBuilder.build().show());

        GuideView.Builder menuBuilder = new GuideView.Builder(mActivity).setContentText(mActivity.getString(R.string.menu_hint)).setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(menu).setGuideListener(view -> searchBuilder.build().show());

        menuBuilder.build().show();

    }

    /**
     * function to set side menu coach marks
     * @param buddy
     * @param chat
     * @param hunt
     * @param huntList
     * @param mActivity
     * @param profile
     * @param setting
     */
    public void setSideMenuCoachMarks(Activity mActivity, View setting, View profile, View buddy, View chat, View hunt, View huntList) {
        GuideView.Builder huntListBuilder = new GuideView.Builder(mActivity).setContentText("List of your all the hunt requests").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(huntList).setGuideListener(view -> {
                    AppSharedPreference.getInstance().putBoolean(mActivity, AppSharedPreference.PREF_KEY.IS_SIDE_MENU_SEEN, true);
                });

        GuideView.Builder huntBuilder = new GuideView.Builder(mActivity).setContentText("Demand your choice of product or service whatever you want us to find for you.").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(hunt).setGuideListener(view -> huntListBuilder.build().show());

        GuideView.Builder chatBuilder = new GuideView.Builder(mActivity).setContentText("See all the chat records with buddies").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(chat).setGuideListener(view -> huntBuilder.build().show());

        GuideView.Builder buddyBuilder = new GuideView.Builder(mActivity).setContentText("List of your favourite buddies").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(buddy).setGuideListener(view -> chatBuilder.build().show());

        GuideView.Builder profileBuilder = new GuideView.Builder(mActivity).setContentText("Check the details of your profile").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(profile).setGuideListener(view -> buddyBuilder.build().show());

        GuideView.Builder settingBuilder = new GuideView.Builder(mActivity).setContentText("Check all the settings and Policies").setGravity(Gravity.center)
                .setDismissType(DismissType.outside).setTargetView(setting).setGuideListener(view -> profileBuilder.build().show());

        settingBuilder.build().show();
    }
}
