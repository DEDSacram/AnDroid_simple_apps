package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;
import androidx.camera.camera2.internal.compat.ApiCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CameraBurstCaptureCallback extends CameraCaptureSession.CaptureCallback {
    final Map<CaptureRequest, List<CameraCaptureSession.CaptureCallback>> mCallbackMap = new HashMap();
    CaptureSequenceCallback mCaptureSequenceCallback = null;

    interface CaptureSequenceCallback {
        void onCaptureSequenceCompletedOrAborted(CameraCaptureSession cameraCaptureSession, int i, boolean z);
    }

    CameraBurstCaptureCallback() {
    }

    public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface surface, long frame) {
        for (CameraCaptureSession.CaptureCallback callback : getCallbacks(request)) {
            ApiCompat.Api24Impl.onCaptureBufferLost(callback, session, request, surface, frame);
        }
    }

    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        for (CameraCaptureSession.CaptureCallback callback : getCallbacks(request)) {
            callback.onCaptureCompleted(session, request, result);
        }
    }

    public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
        for (CameraCaptureSession.CaptureCallback callback : getCallbacks(request)) {
            callback.onCaptureFailed(session, request, failure);
        }
    }

    public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
        for (CameraCaptureSession.CaptureCallback callback : getCallbacks(request)) {
            callback.onCaptureProgressed(session, request, partialResult);
        }
    }

    public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
        for (CameraCaptureSession.CaptureCallback callback : getCallbacks(request)) {
            callback.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    }

    public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
        for (List<CameraCaptureSession.CaptureCallback> callbackList : this.mCallbackMap.values()) {
            for (CameraCaptureSession.CaptureCallback callback : callbackList) {
                callback.onCaptureSequenceAborted(session, sequenceId);
            }
        }
        CaptureSequenceCallback captureSequenceCallback = this.mCaptureSequenceCallback;
        if (captureSequenceCallback != null) {
            captureSequenceCallback.onCaptureSequenceCompletedOrAborted(session, sequenceId, true);
        }
    }

    public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
        for (List<CameraCaptureSession.CaptureCallback> callbackList : this.mCallbackMap.values()) {
            for (CameraCaptureSession.CaptureCallback callback : callbackList) {
                callback.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            }
        }
        CaptureSequenceCallback captureSequenceCallback = this.mCaptureSequenceCallback;
        if (captureSequenceCallback != null) {
            captureSequenceCallback.onCaptureSequenceCompletedOrAborted(session, sequenceId, false);
        }
    }

    private List<CameraCaptureSession.CaptureCallback> getCallbacks(CaptureRequest request) {
        List<CameraCaptureSession.CaptureCallback> callbacks = this.mCallbackMap.get(request);
        return callbacks != null ? callbacks : Collections.emptyList();
    }

    /* access modifiers changed from: package-private */
    public void addCamera2Callbacks(CaptureRequest captureRequest, List<CameraCaptureSession.CaptureCallback> captureCallbacks) {
        List<CameraCaptureSession.CaptureCallback> existingCallbacks = this.mCallbackMap.get(captureRequest);
        if (existingCallbacks != null) {
            List<CameraCaptureSession.CaptureCallback> totalCallbacks = new ArrayList<>(captureCallbacks.size() + existingCallbacks.size());
            totalCallbacks.addAll(captureCallbacks);
            totalCallbacks.addAll(existingCallbacks);
            this.mCallbackMap.put(captureRequest, totalCallbacks);
            return;
        }
        this.mCallbackMap.put(captureRequest, captureCallbacks);
    }

    public void setCaptureSequenceCallback(CaptureSequenceCallback callback) {
        this.mCaptureSequenceCallback = callback;
    }
}
