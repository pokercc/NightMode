package pokercc.android.nightmodel;

import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pokercc.android.nightmodel.attr.Attr;
import pokercc.android.nightmodel.attr.AttrType;
import pokercc.android.nightmodel.attr.impl.AttrDrawableBottom;
import pokercc.android.nightmodel.attr.impl.AttrDrawableLeft;
import pokercc.android.nightmodel.attr.impl.AttrDrawableRight;
import pokercc.android.nightmodel.attr.impl.AttrDrawableTop;
import pokercc.android.nightmodel.attr.impl.AttrTypeBackground;
import pokercc.android.nightmodel.attr.impl.AttrTypeImageSrc;
import pokercc.android.nightmodel.attr.impl.AttrTypeImageSrcCompat;
import pokercc.android.nightmodel.attr.impl.AttrTypeProgressDrawable;
import pokercc.android.nightmodel.attr.impl.AttrTypeTextColor;
import pokercc.android.nightmodel.attr.impl.AttrTypeTextStyle;
import pokercc.android.nightmodel.attr.impl.AttrTypeTint;

/**
 * Created by like on 16/7/21.
 */
  class AttrUtils {


    private static final List<AttrType> ATTR_TYPES = Arrays.asList(
            new AttrTypeBackground(),
            new AttrTypeImageSrc(),
            new AttrTypeImageSrcCompat(),
            new AttrTypeProgressDrawable(),
            new AttrTypeTextColor(),
            new AttrTypeTextStyle(),
            new AttrTypeTint(),
            new AttrDrawableLeft(),
            new AttrDrawableTop(),
            new AttrDrawableRight(),
            new AttrDrawableBottom()
    );
    private static final HashMap<String, AttrType> attrTypeHashMap = new HashMap<>();

    static {
        for (AttrType attrType : ATTR_TYPES) {
            attrTypeHashMap.put(attrType.getAttrType(), attrType);
        }
    }

    public static void addExpandAttrType(AttrType... attrTypes) {
        if (attrTypes == null || attrTypes.length == 0) return;
        for (AttrType attrType : attrTypes) {
            attrTypeHashMap.put(attrType.getAttrType(), attrType);
        }
    }

    private static AttrType getSupportAttrType(String attrName) {
        return attrTypeHashMap.get(attrName);
    }

    public static List<Attr> getNightModelAttr(Object[] args, Resources resources) {
        List<Attr> nightModelAttrs = new ArrayList<>();
        if (args != null && args.length > 0) {
            for (Object obj : args) {
                if (obj instanceof AttributeSet) {
                    AttributeSet attrs = (AttributeSet) obj;
                    for (int i = 0; i < attrs.getAttributeCount(); i++) {
                        String attrName = attrs.getAttributeName(i);
                        String attrValue = attrs.getAttributeValue(i);
                        AttrType attrType = getSupportAttrType(attrName);
                        if (attrType == null) continue;
                        if (attrValue.startsWith("@")) {
                            // 获取资源引用
                            String resourceName;
                            // 判断是style,还是int类型的资源引用
                            if ("style".equals(attrType.getAttrType())) {
                                resourceName = attrValue.substring(1);
                            } else {
                                resourceName = attrType.getIntResourceName(attrValue, resources);
                            }
                            Attr attr = new Attr(resourceName, attrType, false);
                            nightModelAttrs.add(attr);
                        } else if (attrValue.startsWith("?")) {
                            // 获取?attr引用
                            final String resourceName = attrValue.replace("?", "");
                            Attr attr = new Attr(resourceName, attrType, true);
                            nightModelAttrs.add(attr);
                        }
                    }
                }
            }
        }
        return nightModelAttrs;
    }

    private static void log(String message) {
        Log.d("dayNight", message);
    }
}
