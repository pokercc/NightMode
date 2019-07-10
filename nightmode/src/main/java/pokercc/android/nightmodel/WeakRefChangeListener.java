package pokercc.android.nightmodel;

import java.lang.ref.WeakReference;

public class WeakRefChangeListener implements ModelChangeListener {
    private final WeakReference<ModelChangeListener> listenerWeakReference;

    public WeakRefChangeListener(ModelChangeListener listener) {
        this.listenerWeakReference = new WeakReference<>(listener);
    }

    @Override
    public void onChanged(boolean isNight) {
        ModelChangeListener modelChangeListener = listenerWeakReference.get();
        if (modelChangeListener != null) {
            modelChangeListener.onChanged(isNight);
        }

    }
}
