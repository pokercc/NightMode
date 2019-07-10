package pokercc.android.nightmodel;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import pokercc.android.nightmodel.attr.Attr;
import pokercc.android.nightmodel.attr.AttrView;

class ViewAttrRecorderLayoutInflater extends LayoutInflater implements LayoutInflater.Factory2, ModelChangeListener {


    private final LayoutInflater.Factory2 originFactory2;
    private final Resources resources;

    private final List<AttrView> attrViews = new ArrayList<>();


    ViewAttrRecorderLayoutInflater(Context context, LayoutInflater.Factory2 originFactory2) {
        super(context);
        this.originFactory2 = originFactory2;
        this.resources = context.getResources();
    }


    @Override
    public View onCreateView(View view, String s, Context context, AttributeSet attributeSet) {
        View result = originFactory2.onCreateView(view, s, context, attributeSet);
        if (result == null) {
            try {
                // 需要用系统的兜底，AppcompatLayoutInflater 只创建它需要的
                result = onCreateView(view, s, attributeSet);
            } catch (ClassNotFoundException e) {
            }
        }
        recordViewAttr(result, attributeSet);
        return result;
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        View view = originFactory2.onCreateView(s, context, attributeSet);
        if (view == null) {
            try {
                // 需要用系统的兜底，AppcompatLayoutInflater 只创建它需要的
                view = onCreateView(s, attributeSet);
            } catch (ClassNotFoundException e) {
            }
        }
        recordViewAttr(view, attributeSet);

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

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return this;
    }


    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };
    /**
     *
     * copy from @{@link com.android.internal.policy.PhoneLayoutInflater}
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }


}
