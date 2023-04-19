package androidx.camera.camera2.internal;

import androidx.camera.core.CameraState;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CameraStateRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.Objects;

class CameraStateMachine {
    private static final String TAG = "CameraStateMachine";
    private final CameraStateRegistry mCameraStateRegistry;
    private final MutableLiveData<CameraState> mCameraStates;

    CameraStateMachine(CameraStateRegistry cameraStateRegistry) {
        this.mCameraStateRegistry = cameraStateRegistry;
        MutableLiveData<CameraState> mutableLiveData = new MutableLiveData<>();
        this.mCameraStates = mutableLiveData;
        mutableLiveData.postValue(CameraState.create(CameraState.Type.CLOSED));
    }

    /* renamed from: androidx.camera.camera2.internal.CameraStateMachine$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$core$impl$CameraInternal$State;

        static {
            int[] iArr = new int[CameraInternal.State.values().length];
            $SwitchMap$androidx$camera$core$impl$CameraInternal$State = iArr;
            try {
                iArr[CameraInternal.State.PENDING_OPEN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.OPENING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.OPEN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.CLOSING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.RELEASING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.CLOSED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$androidx$camera$core$impl$CameraInternal$State[CameraInternal.State.RELEASED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public void updateState(CameraInternal.State newInternalState, CameraState.StateError stateError) {
        CameraState newPublicState;
        switch (AnonymousClass1.$SwitchMap$androidx$camera$core$impl$CameraInternal$State[newInternalState.ordinal()]) {
            case 1:
                newPublicState = onCameraPendingOpen();
                break;
            case 2:
                newPublicState = CameraState.create(CameraState.Type.OPENING, stateError);
                break;
            case 3:
                newPublicState = CameraState.create(CameraState.Type.OPEN, stateError);
                break;
            case 4:
            case 5:
                newPublicState = CameraState.create(CameraState.Type.CLOSING, stateError);
                break;
            case 6:
            case 7:
                newPublicState = CameraState.create(CameraState.Type.CLOSED, stateError);
                break;
            default:
                throw new IllegalStateException("Unknown internal camera state: " + newInternalState);
        }
        Logger.d(TAG, "New public camera state " + newPublicState + " from " + newInternalState + " and " + stateError);
        if (!Objects.equals(this.mCameraStates.getValue(), newPublicState)) {
            Logger.d(TAG, "Publishing new public camera state " + newPublicState);
            this.mCameraStates.postValue(newPublicState);
        }
    }

    private CameraState onCameraPendingOpen() {
        if (this.mCameraStateRegistry.isCameraClosing()) {
            return CameraState.create(CameraState.Type.OPENING);
        }
        return CameraState.create(CameraState.Type.PENDING_OPEN);
    }

    public LiveData<CameraState> getStateLiveData() {
        return this.mCameraStates;
    }
}
