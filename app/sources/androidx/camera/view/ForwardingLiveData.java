package androidx.camera.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

final class ForwardingLiveData<T> extends MediatorLiveData<T> {
    private LiveData<T> mLiveDataSource;

    ForwardingLiveData() {
    }

    /* access modifiers changed from: package-private */
    public void setSource(LiveData<T> liveDataSource) {
        LiveData<T> liveData = this.mLiveDataSource;
        if (liveData != null) {
            super.removeSource(liveData);
        }
        this.mLiveDataSource = liveDataSource;
        super.addSource(liveDataSource, new ForwardingLiveData$$ExternalSyntheticLambda0(this));
    }

    public T getValue() {
        LiveData<T> liveData = this.mLiveDataSource;
        if (liveData == null) {
            return null;
        }
        return liveData.getValue();
    }
}
