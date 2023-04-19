package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.Surface;
import androidx.camera.camera2.internal.CaptureSession;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraCaptureFailure;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.RequestProcessor;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.SessionProcessorSurface;
import androidx.camera.core.impl.TagBundle;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Camera2RequestProcessor implements RequestProcessor {
    private static final String TAG = "Camera2RequestProcessor";
    private final CaptureSession mCaptureSession;
    private volatile boolean mIsClosed = false;
    private final List<SessionProcessorSurface> mProcessorSurfaces;
    private volatile SessionConfig mSessionConfig;

    public Camera2RequestProcessor(CaptureSession captureSession, List<SessionProcessorSurface> processorSurfaces) {
        boolean z = false;
        Preconditions.checkArgument(captureSession.mState == CaptureSession.State.OPENED ? true : z, "CaptureSession state must be OPENED. Current state:" + captureSession.mState);
        this.mCaptureSession = captureSession;
        this.mProcessorSurfaces = Collections.unmodifiableList(new ArrayList(processorSurfaces));
    }

    public void close() {
        this.mIsClosed = true;
    }

    public void updateSessionConfig(SessionConfig sessionConfig) {
        this.mSessionConfig = sessionConfig;
    }

    private boolean areRequestsValid(List<RequestProcessor.Request> requests) {
        for (RequestProcessor.Request request : requests) {
            if (!isRequestValid(request)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRequestValid(RequestProcessor.Request request) {
        if (request.getTargetOutputConfigIds().isEmpty()) {
            Logger.e(TAG, "Unable to submit the RequestProcessor.Request: empty targetOutputConfigIds");
            return false;
        }
        for (Integer outputConfigId : request.getTargetOutputConfigIds()) {
            if (findSurface(outputConfigId.intValue()) == null) {
                Logger.e(TAG, "Unable to submit the RequestProcessor.Request: targetOutputConfigId(" + outputConfigId + ") is not a valid id");
                return false;
            }
        }
        return true;
    }

    public int submit(RequestProcessor.Request request, RequestProcessor.Callback callback) {
        return submit((List<RequestProcessor.Request>) Arrays.asList(new RequestProcessor.Request[]{request}), callback);
    }

    public int submit(List<RequestProcessor.Request> requests, RequestProcessor.Callback callback) {
        if (this.mIsClosed || !areRequestsValid(requests)) {
            return -1;
        }
        ArrayList<CaptureConfig> captureConfigs = new ArrayList<>();
        boolean shouldInvokeSequenceCallback = true;
        for (RequestProcessor.Request request : requests) {
            CaptureConfig.Builder builder = new CaptureConfig.Builder();
            builder.setTemplateType(request.getTemplateId());
            builder.setImplementationOptions(request.getParameters());
            builder.addCameraCaptureCallback(CaptureCallbackContainer.create(new Camera2CallbackWrapper(request, callback, shouldInvokeSequenceCallback)));
            shouldInvokeSequenceCallback = false;
            for (Integer outputConfigId : request.getTargetOutputConfigIds()) {
                builder.addSurface(findSurface(outputConfigId.intValue()));
            }
            captureConfigs.add(builder.build());
        }
        return this.mCaptureSession.issueBurstCaptureRequest(captureConfigs);
    }

    public int setRepeating(RequestProcessor.Request request, RequestProcessor.Callback callback) {
        if (this.mIsClosed || !isRequestValid(request)) {
            return -1;
        }
        SessionConfig.Builder sessionConfigBuilder = new SessionConfig.Builder();
        sessionConfigBuilder.setTemplateType(request.getTemplateId());
        sessionConfigBuilder.setImplementationOptions(request.getParameters());
        sessionConfigBuilder.addCameraCaptureCallback(CaptureCallbackContainer.create(new Camera2CallbackWrapper(request, callback, true)));
        if (this.mSessionConfig != null) {
            for (CameraCaptureCallback cameraCaptureCallback : this.mSessionConfig.getRepeatingCameraCaptureCallbacks()) {
                sessionConfigBuilder.addCameraCaptureCallback(cameraCaptureCallback);
            }
            TagBundle tagBundle = this.mSessionConfig.getRepeatingCaptureConfig().getTagBundle();
            for (String key : tagBundle.listKeys()) {
                sessionConfigBuilder.addTag(key, tagBundle.getTag(key));
            }
        }
        for (Integer outputConfigId : request.getTargetOutputConfigIds()) {
            sessionConfigBuilder.addSurface(findSurface(outputConfigId.intValue()));
        }
        return this.mCaptureSession.issueRepeatingCaptureRequests(sessionConfigBuilder.build());
    }

    public void abortCaptures() {
        if (!this.mIsClosed) {
            this.mCaptureSession.abortCaptures();
        }
    }

    public void stopRepeating() {
        if (!this.mIsClosed) {
            this.mCaptureSession.stopRepeating();
        }
    }

    private class Camera2CallbackWrapper extends CameraCaptureSession.CaptureCallback {
        private final RequestProcessor.Callback mCallback;
        private final boolean mInvokeSequenceCallback;
        private final RequestProcessor.Request mRequest;

        Camera2CallbackWrapper(RequestProcessor.Request captureRequest, RequestProcessor.Callback callback, boolean invokeSequenceCallback) {
            this.mCallback = callback;
            this.mRequest = captureRequest;
            this.mInvokeSequenceCallback = invokeSequenceCallback;
        }

        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            this.mCallback.onCaptureStarted(this.mRequest, frameNumber, timestamp);
        }

        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            this.mCallback.onCaptureProgressed(this.mRequest, new Camera2CameraCaptureResult(partialResult));
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            this.mCallback.onCaptureCompleted(this.mRequest, new Camera2CameraCaptureResult(result));
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            this.mCallback.onCaptureFailed(this.mRequest, new Camera2CameraCaptureFailure(CameraCaptureFailure.Reason.ERROR, failure));
        }

        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            if (this.mInvokeSequenceCallback) {
                this.mCallback.onCaptureSequenceCompleted(sequenceId, frameNumber);
            }
        }

        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            if (this.mInvokeSequenceCallback) {
                this.mCallback.onCaptureSequenceAborted(sequenceId);
            }
        }

        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            this.mCallback.onCaptureBufferLost(this.mRequest, frameNumber, Camera2RequestProcessor.this.findOutputConfigId(target));
        }
    }

    /* access modifiers changed from: package-private */
    public int findOutputConfigId(Surface surface) {
        for (SessionProcessorSurface sessionProcessorSurface : this.mProcessorSurfaces) {
            try {
                if (sessionProcessorSurface.getSurface().get() == surface) {
                    return sessionProcessorSurface.getOutputConfigId();
                }
            } catch (InterruptedException | ExecutionException e) {
            }
        }
        return -1;
    }

    private DeferrableSurface findSurface(int outputConfigId) {
        for (SessionProcessorSurface sessionProcessorSurface : this.mProcessorSurfaces) {
            if (sessionProcessorSurface.getOutputConfigId() == outputConfigId) {
                return sessionProcessorSurface;
            }
        }
        return null;
    }
}
