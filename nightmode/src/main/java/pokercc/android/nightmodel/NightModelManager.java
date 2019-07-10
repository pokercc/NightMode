package pokercc.android.nightmodel;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;

import pokercc.android.nightmodel.attr.AttrType;

/**
 * Created by like on 16/7/20.
 */
public class NightModelManager {


    /**
     * ths method should be called in Application onCreate method
     *
     * @param context
     */
    public void init(Context context) {
        boolean isNightModel = PersistenceUtils.isNightModel(context);
        AppCompatDelegate.setDefaultNightMode(isNightModel ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * this method should be called in Activity onCreate method,
     * and before method super.onCreate(savedInstanceState);
     *
     * @param activity
     */
    public void attach(AppCompatActivity activity) {

        final LayoutInflater defaultLayoutInflater = LayoutInflater.from(activity);
        if (activity.getDelegate() instanceof LayoutInflater.Factory2) {
            LayoutInflater.Factory2 originInflaterFactory = (LayoutInflater.Factory2) activity.getDelegate();
            ViewAttrRecorderLayoutInflater factory = new ViewAttrRecorderLayoutInflater(originInflaterFactory, activity.getResources());
            LayoutInflaterCompat.setFactory2(defaultLayoutInflater, factory);
            ModelChangeManager.getInstance().addListener(new WeakRefChangeListener(factory));
        }


    }

    /**
     * this method should be called in Activity onDestroy method
     *
     * @param activity
     */
    @Deprecated
    public void detach(AppCompatActivity activity) {
    }

    public boolean isCurrentNightModel(Context context) {
        return PersistenceUtils.isNightModel(context);
    }


    public void applyDayMight(AppCompatActivity activity, boolean night) {
        invokeResources(activity);
        AppCompatDelegate.setDefaultNightMode(night ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        activity.getDelegate().applyDayNight();
        PersistenceUtils.setNightModel(activity.getApplicationContext(), night);
        ModelChangeManager.getInstance().notifyChange(night);
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
    private void invokeResources(AppCompatActivity activity) {
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


    private static class NightModelManagerHolder {
        static NightModelManager instance = new NightModelManager();
    }

    public static NightModelManager getInstance() {
        return NightModelManagerHolder.instance;
    }


    /**
     * 添加夜间模式切换监听
     *
     * @param listener
     */
    public void addModelChangeListener(ModelChangeListener listener) {
        ModelChangeManager.getInstance().addListener(listener);
    }

    /**
     * 移除夜间模式切换监听
     *
     * @param listener
     */
    public void removeModelChangeListener(ModelChangeListener listener) {
        ModelChangeManager.getInstance().removeListener(listener);
    }
}
