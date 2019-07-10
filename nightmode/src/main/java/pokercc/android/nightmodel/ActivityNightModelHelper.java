package pokercc.android.nightmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pokercc.android.nightmodel.attr.AttrType;

/**
 * 单activity 夜间模式切换免重启的帮助类
 *
 * @author pokercc
 * 2019/07/10
 */
public class ActivityNightModelHelper {


    private final AppCompatActivity appCompatActivity;
    private final List<ModelChangeListener> modelChangeListeners = new ArrayList<>();
    private DayNightModePreference dayNightModePreference = DayNightModePreference.DEFAULT;
    @StyleRes
    private final int nightTheme, dayTheme;

    public ActivityNightModelHelper(AppCompatActivity appCompatActivity, int dayTheme, int nightTheme) {
        this.appCompatActivity = appCompatActivity;
        this.nightTheme = nightTheme;
        this.dayTheme = dayTheme;
    }

    public void setDayNightModePreference(@NonNull DayNightModePreference dayNightModePreference) {
        this.dayNightModePreference = dayNightModePreference;
    }


    public void onCreate() {
        // 1. 配置appCompat(必须要先配置这个，再设置Theme,不然dialog的样式会实效)
        boolean nightMode = isNightMode();
        appCompatActivity.getDelegate().setLocalNightMode(nightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        appCompatActivity.getDelegate().applyDayNight();
        // 2. 设置Theme
        appCompatActivity.setTheme(nightMode ? nightTheme : dayTheme);

        // 3. 设置layoutFactory
        final LayoutInflater defaultLayoutInflater = LayoutInflater.from(appCompatActivity);
        AppCompatDelegate delegate = appCompatActivity.getDelegate();
        if (delegate instanceof LayoutInflater.Factory2) {
            LayoutInflater.Factory2 originInflaterFactory = (LayoutInflater.Factory2) delegate;
            ViewAttrRecorderLayoutInflater factory = new ViewAttrRecorderLayoutInflater(originInflaterFactory, appCompatActivity.getResources());
            LayoutInflaterCompat.setFactory2(defaultLayoutInflater, factory);
            addModelChangeListener(new WeakRefChangeListener(factory));
        }

    }


    public boolean isNightMode() {
        return dayNightModePreference.isNightMode(appCompatActivity, appCompatActivity.getLocalClassName());
    }

    public void applyDayNight(boolean night) {
        // 1. 重置Resource
        unsetResources(appCompatActivity);
        // 2. 配置appCompat(必须要先配置这个，再设置Theme,不然dialog的样式会实效)
        appCompatActivity.getDelegate().setLocalNightMode(night ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        appCompatActivity.getDelegate().applyDayNight();
        // 3. 重置theme,比如Dialog就会用到activity的theme
        appCompatActivity.setTheme(night ? nightTheme : dayTheme);
        // 4. 更新UI
        Looper.myQueue().addIdleHandler(new NotifyHandler(modelChangeListeners, night));
        // 5. 保存配置
        dayNightModePreference.saveNightModeChange(appCompatActivity, appCompatActivity.getLocalClassName(), night);

    }

    public void addExpandAttrType(AttrType... attrTypes) {
        AttrUtils.addExpandAttrType(attrTypes);
    }

    /**
     * it's used for update StateListDrawable, otherwise StateListDrawable
     * will not be updated.
     *
     * @param activity
     */
    private static void unsetResources(AppCompatActivity activity) {
        try {
            Field resources = AppCompatActivity.class.getDeclaredField("mResources");
            resources.setAccessible(true);
            resources.set(activity, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加夜间模式切换监听
     *
     * @param listener
     */
    public void addModelChangeListener(ModelChangeListener listener) {
        modelChangeListeners.add(listener);

    }

    /**
     * 移除夜间模式切换监听
     *
     * @param listener
     */
    public void removeModelChangeListener(ModelChangeListener listener) {
        modelChangeListeners.remove(listener);

    }

    private static class NotifyHandler implements MessageQueue.IdleHandler {

        List<ModelChangeListener> listeners;
        boolean isNight;

        NotifyHandler(List<ModelChangeListener> listeners, boolean isNight) {
            this.listeners = listeners;
            this.isNight = isNight;
        }

        @Override
        public boolean queueIdle() {
            if (listeners != null) {
                for (ModelChangeListener listener : listeners) {
                    listener.onChanged(isNight);
                }
            }
            return false;
        }
    }

    public interface DayNightModePreference {
        String KEY_CURRENT_MODEL = "night_mode";

        DayNightModePreference DEFAULT = new DayNightModePreference() {
            @Override
            public boolean isNightMode(Context context, @Nullable String pageName) {
                return getSharedPreferences(context, pageName).getBoolean(KEY_CURRENT_MODEL, false);
            }

            private SharedPreferences getSharedPreferences(Context context, @Nullable String pageName) {
                return context.getSharedPreferences("day_night_config" + (TextUtils.isEmpty(pageName) ? "" : pageName), Context.MODE_PRIVATE);
            }

            @Override
            public void saveNightModeChange(Context context, @Nullable String pageName, boolean nightMode) {
                getSharedPreferences(context, pageName).edit().putBoolean(KEY_CURRENT_MODEL, nightMode).apply();

            }
        };

        boolean isNightMode(Context context, @Nullable String pageName);

        void saveNightModeChange(Context context, @Nullable String pageName, boolean nightMode);
    }

}
