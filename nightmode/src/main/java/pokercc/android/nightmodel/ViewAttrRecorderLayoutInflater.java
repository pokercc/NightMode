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

class ViewAttrRecorderLayoutInflater implements LayoutInflater.Factory2, ModelChangeListener {


    private final LayoutInflater.Factory2 originFactory2;
    private final Resources resources;

    private final List<AttrView> attrViews = new ArrayList<>();

    ViewAttrRecorderLayoutInflater(LayoutInflater.Factory2 originFactory2, Resources resources) {

        this.originFactory2 = originFactory2;
        this.resources = resources;
    }

    @Override
    public View onCreateView(View view, String s, Context context, AttributeSet attributeSet) {
        View result = originFactory2.onCreateView(view, s, context, attributeSet);
        recordViewAttr(result, attributeSet);
        return result;
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        View view = originFactory2.onCreateView(s, context, attributeSet);
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
}
