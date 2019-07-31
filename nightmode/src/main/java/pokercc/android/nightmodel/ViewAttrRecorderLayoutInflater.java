package pokercc.android.nightmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.collection.ArrayMap;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pokercc.android.nightmodel.attr.Attr;
import pokercc.android.nightmodel.attr.AttrView;

class ViewAttrRecorderLayoutInflater implements LayoutInflater.Factory2, ModelChangeListener {


    private final LayoutInflater.Factory2 originFactory2;
    private final Resources resources;

    private final List<AttrView> attrViews = new ArrayList<>();

    private static final Map<String, Constructor<? extends View>> sConstructorMap
            = new ArrayMap<>();
    private final Object[] mConstructorArgs = new Object[2];
    private static final Class<?>[] sConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};


    ViewAttrRecorderLayoutInflater(Context context, LayoutInflater.Factory2 originFactory2) {

        this.originFactory2 = originFactory2;
        this.resources = context.getResources();
    }


    @Override
    public View onCreateView(View view, String s, Context context, AttributeSet attributeSet) {
        return onCreateView(s, context, attributeSet);
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        View view = originFactory2.onCreateView(s, context, attributeSet);
        if (view == null) {
            // AppcompatLayoutInflater 只创建它需要的
            // 系统的LayoutInflater 在android 23以下有bug，缺少context
            view = createViewFromTag(context, s, attributeSet);
        }
        if (view != null) {
            recordViewAttr(view, attributeSet);
        }
        return view;
    }

    /**
     * 记录View的属性
     *
     * @return
     */

    private void recordViewAttr(View view, AttributeSet attributeSet) {
        if (view != null) {
            final List<Attr> attrs = AttrUtils.getNightModelAttr(attributeSet, resources);
            if (!attrs.isEmpty()) {
                attrViews.add(new AttrView(view, attrs));
            }
        }
    }


    @Override
    public void onChanged(boolean isNight) {
        for (AttrView attrView : attrViews) {
            attrView.apply();
        }
    }




    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                // try the android.widget prefix first...
                return createView(context, name, "android.widget.");
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            if (constructor == null) {
                Class<? extends View> clazz = context.getClassLoader().loadClass(
                        prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            return null;
        }
    }

}
