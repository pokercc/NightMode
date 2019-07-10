package pokercc.android.nightmodel.attr;

import android.view.View;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

/**
 * Created by like on 16/7/20.
 */
public class AttrView {
    private final WeakReference<View> view;
    private final List<Attr> attrs;

    public AttrView(View view, List<Attr> attrs) {
        this.view = new WeakReference<>(view);
        this.attrs = attrs;
    }

    public void apply() {
        View view = this.view.get();
        if (view != null) {
            for (Attr attr : attrs) {
                attr.apply(view);
            }
        }
    }
}
