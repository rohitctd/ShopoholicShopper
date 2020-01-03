package com.shopoholic.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.Constants;

import java.util.Locale;

/**
 * Base activity class
 */

public class BaseActivity extends AppCompatActivity implements TextWatcher{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
    }

    /**
     * hides keyboard onClick anywhere besides edit text
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];

            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                if (this.getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
                    ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
                }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /**
     * function to set language
     */
    private void setLanguage() {
        String currentLang = AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_LANGUAGE);
        String language;
        Locale locale = Locale.ENGLISH;
        switch (currentLang) {
            case Constants.AppConstant.CHINES_TRAD:
                language = Constants.AppConstant.CODE_CHINES_TRADITIONAL;
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case Constants.AppConstant.CHINES_SIMPLE:
                locale = Locale.SIMPLIFIED_CHINESE;
                language = Constants.AppConstant.CODE_CHINES_SIMPLIFIED;
                break;
            case Constants.AppConstant.MALAYALAM:
                language = Constants.AppConstant.CODE_MALAYALAM;
                locale = new Locale(language, "");
                break;
            case Constants.AppConstant.HINDI:
                language = Constants.AppConstant.CODE_HINDI;
                locale = new Locale(language, "");
                break;
            case Constants.AppConstant.ARABIC:
                language = Constants.AppConstant.CODE_ARABIC;
                locale = new Locale(language, "");
                break;
        }
        if (!Locale.getDefault().getLanguage().equalsIgnoreCase(locale.getLanguage())) {
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }


}
