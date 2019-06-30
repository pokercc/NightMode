package pokercc.android.nightmodel.attr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import android.view.View;

/**
 * Created by like on 16/7/20.
 */
public abstract class AttrType {
    protected static final String DEF_TYPE_DRAWABLE = "drawable";
    protected static final String DEF_TYPE_COLOR = "color";
    protected static final String DEF_TYPE_STYLE = "style";

    private final String attrType;

    public AttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrType() {
        return attrType;
    }

    public abstract void apply(View view, String resName);

    public void applyColor(View view, @ColorInt int color) {

    }


    @Deprecated
    public String getResourceName(String attrValue, Resources resources) {
        return null;
    }

    public String getIntResourceName(String attrValue, Resources resources) {
        int id = Integer.parseInt(attrValue.substring(1));
        if (id == 0) return null;
        return resources.getResourceEntryName(id);
    }

    protected Drawable getDrawable(Context context, String resName) {
        Drawable drawable = null;
        Resources resources = context.getResources();
        try {
            drawable = resources.getDrawable(resources.getIdentifier(resName, DEF_TYPE_DRAWABLE, context.getPackageName()));
        } catch (Resources.NotFoundException e) {
            try {
                drawable = resources.getDrawable(resources.getIdentifier(resName, DEF_TYPE_COLOR, context.getPackageName()));
            } catch (Resources.NotFoundException e2) {
            }
        }
        return drawable;
    }
}
