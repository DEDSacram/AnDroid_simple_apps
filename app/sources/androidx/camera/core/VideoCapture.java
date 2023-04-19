package androidx.camera.core;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.location.Location;
import android.media.AudioRecord;
import android.media.CamcorderProfile;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.UseCase;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.CaptureConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.ConfigProvider;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.ImmediateSurface;
import androidx.camera.core.impl.MutableConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.OptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.internal.TargetConfig;
import androidx.camera.core.internal.ThreadConfig;
import androidx.camera.core.internal.UseCaseEventConfig;
import androidx.camera.core.internal.utils.VideoUtil;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class VideoCapture extends UseCase {
    private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
    private static final int[] CamcorderQuality = {8, 6, 5, 4};
    public static final Defaults DEFAULT_CONFIG = new Defaults();
    private static final int DEQUE_TIMEOUT_USEC = 10000;
    public static final int ERROR_ENCODER = 1;
    public static final int ERROR_FILE_IO = 4;
    public static final int ERROR_INVALID_CAMERA = 5;
    public static final int ERROR_MUXER = 2;
    public static final int ERROR_RECORDING_IN_PROGRESS = 3;
    public static final int ERROR_RECORDING_TOO_SHORT = 6;
    public static final int ERROR_UNKNOWN = 0;
    private static final String TAG = "VideoCapture";
    private static final String VIDEO_MIME_TYPE = "video/avc";
    private int mAudioBitRate;
    private final MediaCodec.BufferInfo mAudioBufferInfo = new MediaCodec.BufferInfo();
    private volatile int mAudioBufferSize;
    private int mAudioChannelCount;
    private MediaCodec mAudioEncoder;
    private Handler mAudioHandler;
    private HandlerThread mAudioHandlerThread;
    private volatile AudioRecord mAudioRecorder;
    private int mAudioSampleRate;
    private int mAudioTrackIndex;
    Surface mCameraSurface;
    private DeferrableSurface mDeferrableSurface;
    private final AtomicBoolean mEndOfAudioStreamSignal = new AtomicBoolean(true);
    private final AtomicBoolean mEndOfAudioVideoSignal = new AtomicBoolean(true);
    private final AtomicBoolean mEndOfVideoStreamSignal = new AtomicBoolean(true);
    private final AtomicBoolean mIsAudioEnabled = new AtomicBoolean(true);
    public final AtomicBoolean mIsFirstAudioSampleWrite = new AtomicBoolean(false);
    public final AtomicBoolean mIsFirstVideoKeyFrameWrite = new AtomicBoolean(false);
    private volatile boolean mIsRecording = false;
    private MediaMuxer mMuxer;
    private final Object mMuxerLock = new Object();
    private final AtomicBoolean mMuxerStarted = new AtomicBoolean(false);
    private volatile ParcelFileDescriptor mParcelFileDescriptor;
    private ListenableFuture<Void> mRecordingFuture = null;
    volatile Uri mSavedVideoUri;
    private SessionConfig.Builder mSessionConfigBuilder = new SessionConfig.Builder();
    private final MediaCodec.BufferInfo mVideoBufferInfo = new MediaCodec.BufferInfo();
    MediaCodec mVideoEncoder;
    private Throwable mVideoEncoderErrorMessage;
    private VideoEncoderInitStatus mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_UNINITIALIZED;
    private Handler mVideoHandler;
    private HandlerThread mVideoHandlerThread;
    private int mVideoTrackIndex;

    public static final class Metadata {
        public Location location;
    }

    public interface OnVideoSavedCallback {
        void onError(int i, String str, Throwable th);

        void onVideoSaved(OutputFileResults outputFileResults);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoCaptureError {
    }

    enum VideoEncoderInitStatus {
        VIDEO_ENCODER_INIT_STATUS_UNINITIALIZED,
        VIDEO_ENCODER_INIT_STATUS_INITIALIZED_FAILED,
        VIDEO_ENCODER_INIT_STATUS_INSUFFICIENT_RESOURCE,
        VIDEO_ENCODER_INIT_STATUS_RESOURCE_RECLAIMED
    }

    VideoCapture(VideoCaptureConfig config) {
        super(config);
    }

    private static MediaFormat createVideoMediaFormat(VideoCaptureConfig config, Size resolution) {
        MediaFormat format = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, resolution.getWidth(), resolution.getHeight());
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", config.getBitRate());
        format.setInteger("frame-rate", config.getVideoFrameRate());
        format.setInteger("i-frame-interval", config.getIFrameInterval());
        return format;
    }

    public UseCaseConfig<?> getDefaultConfig(boolean applyDefaultConfig, UseCaseConfigFactory factory) {
        Config captureConfig = factory.getConfig(UseCaseConfigFactory.CaptureType.VIDEO_CAPTURE, 1);
        if (applyDefaultConfig) {
            captureConfig = Config.mergeConfigs(captureConfig, DEFAULT_CONFIG.getConfig());
        }
        if (captureConfig == null) {
            return null;
        }
        return getUseCaseConfigBuilder(captureConfig).getUseCaseConfig();
    }

    public void onAttached() {
        this.mVideoHandlerThread = new HandlerThread("CameraX-video encoding thread");
        this.mAudioHandlerThread = new HandlerThread("CameraX-audio encoding thread");
        this.mVideoHandlerThread.start();
        this.mVideoHandler = new Handler(this.mVideoHandlerThread.getLooper());
        this.mAudioHandlerThread.start();
        this.mAudioHandler = new Handler(this.mAudioHandlerThread.getLooper());
    }

    /* access modifiers changed from: protected */
    public Size onSuggestedResolutionUpdated(Size suggestedResolution) {
        if (this.mCameraSurface != null) {
            this.mVideoEncoder.stop();
            this.mVideoEncoder.release();
            this.mAudioEncoder.stop();
            this.mAudioEncoder.release();
            releaseCameraSurface(false);
        }
        try {
            this.mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
            this.mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
            setupEncoder(getCameraId(), suggestedResolution);
            notifyActive();
            return suggestedResolution;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create MediaCodec due to: " + e.getCause());
        }
    }

    /* renamed from: startRecording */
    public void m162lambda$startRecording$0$androidxcameracoreVideoCapture(OutputFileOptions outputFileOptions, Executor executor, OnVideoSavedCallback callback) {
        Executor executor2 = executor;
        OnVideoSavedCallback onVideoSavedCallback = callback;
        if (Looper.getMainLooper() != Looper.myLooper()) {
            CameraXExecutors.mainThreadExecutor().execute(new VideoCapture$$ExternalSyntheticLambda7(this, outputFileOptions, executor2, onVideoSavedCallback));
            return;
        }
        OutputFileOptions outputFileOptions2 = outputFileOptions;
        Logger.i(TAG, "startRecording");
        this.mIsFirstVideoKeyFrameWrite.set(false);
        this.mIsFirstAudioSampleWrite.set(false);
        OnVideoSavedCallback postListener = new VideoSavedListenerWrapper(executor2, onVideoSavedCallback);
        CameraInternal attachedCamera = getCamera();
        if (attachedCamera == null) {
            postListener.onError(5, "Not bound to a Camera [" + this + "]", (Throwable) null);
        } else if (this.mVideoEncoderInitStatus == VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_INSUFFICIENT_RESOURCE || this.mVideoEncoderInitStatus == VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_INITIALIZED_FAILED || this.mVideoEncoderInitStatus == VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_RESOURCE_RECLAIMED) {
            postListener.onError(1, "Video encoder initialization failed before start recording ", this.mVideoEncoderErrorMessage);
        } else if (!this.mEndOfAudioVideoSignal.get()) {
            postListener.onError(3, "It is still in video recording!", (Throwable) null);
        } else {
            if (this.mIsAudioEnabled.get()) {
                try {
                    if (this.mAudioRecorder.getState() == 1) {
                        this.mAudioRecorder.startRecording();
                    }
                } catch (IllegalStateException e) {
                    Logger.i(TAG, "AudioRecorder cannot start recording, disable audio." + e.getMessage());
                    this.mIsAudioEnabled.set(false);
                    releaseAudioInputResource();
                }
                if (this.mAudioRecorder.getRecordingState() != 3) {
                    Logger.i(TAG, "AudioRecorder startRecording failed - incorrect state: " + this.mAudioRecorder.getRecordingState());
                    this.mIsAudioEnabled.set(false);
                    releaseAudioInputResource();
                }
            }
            AtomicReference<CallbackToFutureAdapter.Completer<Void>> recordingCompleterRef = new AtomicReference<>();
            this.mRecordingFuture = CallbackToFutureAdapter.getFuture(new VideoCapture$$ExternalSyntheticLambda0(recordingCompleterRef));
            CallbackToFutureAdapter.Completer<Void> recordingCompleter = (CallbackToFutureAdapter.Completer) Preconditions.checkNotNull(recordingCompleterRef.get());
            this.mRecordingFuture.addListener(new VideoCapture$$ExternalSyntheticLambda3(this), CameraXExecutors.mainThreadExecutor());
            try {
                Logger.i(TAG, "videoEncoder start");
                this.mVideoEncoder.start();
                if (this.mIsAudioEnabled.get()) {
                    Logger.i(TAG, "audioEncoder start");
                    this.mAudioEncoder.start();
                }
                try {
                    synchronized (this.mMuxerLock) {
                        MediaMuxer initMediaMuxer = initMediaMuxer(outputFileOptions);
                        this.mMuxer = initMediaMuxer;
                        Preconditions.checkNotNull(initMediaMuxer);
                        this.mMuxer.setOrientationHint(getRelativeRotation(attachedCamera));
                        Metadata metadata = outputFileOptions.getMetadata();
                        if (!(metadata == null || metadata.location == null)) {
                            this.mMuxer.setLocation((float) metadata.location.getLatitude(), (float) metadata.location.getLongitude());
                        }
                    }
                    this.mEndOfVideoStreamSignal.set(false);
                    this.mEndOfAudioStreamSignal.set(false);
                    this.mEndOfAudioVideoSignal.set(false);
                    this.mIsRecording = true;
                    this.mSessionConfigBuilder.clearSurfaces();
                    this.mSessionConfigBuilder.addSurface(this.mDeferrableSurface);
                    updateSessionConfig(this.mSessionConfigBuilder.build());
                    notifyUpdated();
                    if (this.mIsAudioEnabled.get()) {
                        this.mAudioHandler.post(new VideoCapture$$ExternalSyntheticLambda5(this, postListener));
                    }
                    String cameraId = getCameraId();
                    Size resolution = getAttachedSurfaceResolution();
                    String str = cameraId;
                    VideoCapture$$ExternalSyntheticLambda6 videoCapture$$ExternalSyntheticLambda6 = r1;
                    Handler handler = this.mVideoHandler;
                    VideoCapture$$ExternalSyntheticLambda6 videoCapture$$ExternalSyntheticLambda62 = new VideoCapture$$ExternalSyntheticLambda6(this, postListener, cameraId, resolution, outputFileOptions, recordingCompleter);
                    handler.post(videoCapture$$ExternalSyntheticLambda6);
                } catch (IOException e2) {
                    recordingCompleter.set(null);
                    postListener.onError(2, "MediaMuxer creation failed!", e2);
                }
            } catch (IllegalStateException e3) {
                recordingCompleter.set(null);
                postListener.onError(1, "Audio/Video encoder start fail", e3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startRecording$2$androidx-camera-core-VideoCapture  reason: not valid java name */
    public /* synthetic */ void m163lambda$startRecording$2$androidxcameracoreVideoCapture() {
        this.mRecordingFuture = null;
        if (getCamera() != null) {
            setupEncoder(getCameraId(), getAttachedSurfaceResolution());
            notifyReset();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$startRecording$4$androidx-camera-core-VideoCapture  reason: not valid java name */
    public /* synthetic */ void m165lambda$startRecording$4$androidxcameracoreVideoCapture(OnVideoSavedCallback postListener, String cameraId, Size resolution, OutputFileOptions outputFileOptions, CallbackToFutureAdapter.Completer recordingCompleter) {
        if (!videoEncode(postListener, cameraId, resolution, outputFileOptions)) {
            postListener.onVideoSaved(new OutputFileResults(this.mSavedVideoUri));
            this.mSavedVideoUri = null;
        }
        recordingCompleter.set(null);
    }

    /* renamed from: stopRecording */
    public void m166lambda$stopRecording$5$androidxcameracoreVideoCapture() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            CameraXExecutors.mainThreadExecutor().execute(new VideoCapture$$ExternalSyntheticLambda4(this));
            return;
        }
        Logger.i(TAG, "stopRecording");
        this.mSessionConfigBuilder.clearSurfaces();
        this.mSessionConfigBuilder.addNonRepeatingSurface(this.mDeferrableSurface);
        updateSessionConfig(this.mSessionConfigBuilder.build());
        notifyUpdated();
        if (!this.mIsRecording) {
            return;
        }
        if (this.mIsAudioEnabled.get()) {
            this.mEndOfAudioStreamSignal.set(true);
        } else {
            this.mEndOfVideoStreamSignal.set(true);
        }
    }

    public void onDetached() {
        m166lambda$stopRecording$5$androidxcameracoreVideoCapture();
        ListenableFuture<Void> listenableFuture = this.mRecordingFuture;
        if (listenableFuture != null) {
            listenableFuture.addListener(new VideoCapture$$ExternalSyntheticLambda2(this), CameraXExecutors.mainThreadExecutor());
        } else {
            m161lambda$onDetached$6$androidxcameracoreVideoCapture();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: releaseResources */
    public void m161lambda$onDetached$6$androidxcameracoreVideoCapture() {
        this.mVideoHandlerThread.quitSafely();
        releaseAudioInputResource();
        if (this.mCameraSurface != null) {
            releaseCameraSurface(true);
        }
    }

    private void releaseAudioInputResource() {
        this.mAudioHandlerThread.quitSafely();
        MediaCodec mediaCodec = this.mAudioEncoder;
        if (mediaCodec != null) {
            mediaCodec.release();
            this.mAudioEncoder = null;
        }
        if (this.mAudioRecorder != null) {
            this.mAudioRecorder.release();
            this.mAudioRecorder = null;
        }
    }

    public UseCaseConfig.Builder<?, ?, ?> getUseCaseConfigBuilder(Config config) {
        return Builder.fromConfig(config);
    }

    public void onStateDetached() {
        m166lambda$stopRecording$5$androidxcameracoreVideoCapture();
    }

    private void releaseCameraSurface(boolean releaseVideoEncoder) {
        DeferrableSurface deferrableSurface = this.mDeferrableSurface;
        if (deferrableSurface != null) {
            MediaCodec videoEncoder = this.mVideoEncoder;
            deferrableSurface.close();
            this.mDeferrableSurface.getTerminationFuture().addListener(new VideoCapture$$ExternalSyntheticLambda8(releaseVideoEncoder, videoEncoder), CameraXExecutors.mainThreadExecutor());
            if (releaseVideoEncoder) {
                this.mVideoEncoder = null;
            }
            this.mCameraSurface = null;
            this.mDeferrableSurface = null;
        }
    }

    static /* synthetic */ void lambda$releaseCameraSurface$7(boolean releaseVideoEncoder, MediaCodec videoEncoder) {
        if (releaseVideoEncoder && videoEncoder != null) {
            videoEncoder.release();
        }
    }

    public void setTargetRotation(int rotation) {
        setTargetRotationInternal(rotation);
    }

    /* access modifiers changed from: package-private */
    public void setupEncoder(final String cameraId, final Size resolution) {
        VideoCaptureConfig config = (VideoCaptureConfig) getCurrentConfig();
        this.mVideoEncoder.reset();
        this.mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_UNINITIALIZED;
        try {
            this.mVideoEncoder.configure(createVideoMediaFormat(config, resolution), (Surface) null, (MediaCrypto) null, 1);
            if (this.mCameraSurface != null) {
                releaseCameraSurface(false);
            }
            Surface cameraSurface = this.mVideoEncoder.createInputSurface();
            this.mCameraSurface = cameraSurface;
            this.mSessionConfigBuilder = SessionConfig.Builder.createFrom(config);
            DeferrableSurface deferrableSurface = this.mDeferrableSurface;
            if (deferrableSurface != null) {
                deferrableSurface.close();
            }
            ImmediateSurface immediateSurface = new ImmediateSurface(this.mCameraSurface, resolution, getImageFormat());
            this.mDeferrableSurface = immediateSurface;
            ListenableFuture<Void> terminationFuture = immediateSurface.getTerminationFuture();
            Objects.requireNonNull(cameraSurface);
            terminationFuture.addListener(new VideoCapture$$ExternalSyntheticLambda1(cameraSurface), CameraXExecutors.mainThreadExecutor());
            this.mSessionConfigBuilder.addNonRepeatingSurface(this.mDeferrableSurface);
            this.mSessionConfigBuilder.addErrorListener(new SessionConfig.ErrorListener() {
                public void onError(SessionConfig sessionConfig, SessionConfig.SessionError error) {
                    if (VideoCapture.this.isCurrentCamera(cameraId)) {
                        VideoCapture.this.setupEncoder(cameraId, resolution);
                        VideoCapture.this.notifyReset();
                    }
                }
            });
            updateSessionConfig(this.mSessionConfigBuilder.build());
            this.mIsAudioEnabled.set(true);
            setAudioParametersByCamcorderProfile(resolution, cameraId);
            this.mAudioEncoder.reset();
            this.mAudioEncoder.configure(createAudioMediaFormat(), (Surface) null, (MediaCrypto) null, 1);
            if (this.mAudioRecorder != null) {
                this.mAudioRecorder.release();
            }
            this.mAudioRecorder = autoConfigAudioRecordSource(config);
            if (this.mAudioRecorder == null) {
                Logger.e(TAG, "AudioRecord object cannot initialized correctly!");
                this.mIsAudioEnabled.set(false);
            }
            synchronized (this.mMuxerLock) {
                this.mVideoTrackIndex = -1;
                this.mAudioTrackIndex = -1;
            }
            this.mIsRecording = false;
        } catch (MediaCodec.CodecException e) {
            if (Build.VERSION.SDK_INT >= 23) {
                int errorCode = Api23Impl.getCodecExceptionErrorCode(e);
                String diagnosticInfo = e.getDiagnosticInfo();
                if (errorCode == 1100) {
                    Logger.i(TAG, "CodecException: code: " + errorCode + " diagnostic: " + diagnosticInfo);
                    this.mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_INSUFFICIENT_RESOURCE;
                } else if (errorCode == 1101) {
                    Logger.i(TAG, "CodecException: code: " + errorCode + " diagnostic: " + diagnosticInfo);
                    this.mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_RESOURCE_RECLAIMED;
                }
            } else {
                this.mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_INITIALIZED_FAILED;
            }
            this.mVideoEncoderErrorMessage = e;
        } catch (IllegalArgumentException | IllegalStateException e2) {
            this.mVideoEncoderInitStatus = VideoEncoderInitStatus.VIDEO_ENCODER_INIT_STATUS_INITIALIZED_FAILED;
            this.mVideoEncoderErrorMessage = e2;
        }
    }

    private boolean writeVideoEncodedBuffer(int bufferIndex) {
        if (bufferIndex < 0) {
            Logger.e(TAG, "Output buffer should not have negative index: " + bufferIndex);
            return false;
        }
        ByteBuffer outputBuffer = this.mVideoEncoder.getOutputBuffer(bufferIndex);
        if (outputBuffer == null) {
            Logger.d(TAG, "OutputBuffer was null.");
            return false;
        }
        if (this.mMuxerStarted.get()) {
            if (this.mVideoBufferInfo.size > 0) {
                outputBuffer.position(this.mVideoBufferInfo.offset);
                outputBuffer.limit(this.mVideoBufferInfo.offset + this.mVideoBufferInfo.size);
                this.mVideoBufferInfo.presentationTimeUs = System.nanoTime() / 1000;
                synchronized (this.mMuxerLock) {
                    if (!this.mIsFirstVideoKeyFrameWrite.get()) {
                        if ((this.mVideoBufferInfo.flags & 1) != 0) {
                            Logger.i(TAG, "First video key frame written.");
                            this.mIsFirstVideoKeyFrameWrite.set(true);
                        } else {
                            Bundle syncFrame = new Bundle();
                            syncFrame.putInt("request-sync", 0);
                            this.mVideoEncoder.setParameters(syncFrame);
                        }
                    }
                    this.mMuxer.writeSampleData(this.mVideoTrackIndex, outputBuffer, this.mVideoBufferInfo);
                }
            } else {
                Logger.i(TAG, "mVideoBufferInfo.size <= 0, index " + bufferIndex);
            }
        }
        this.mVideoEncoder.releaseOutputBuffer(bufferIndex, false);
        if ((this.mVideoBufferInfo.flags & 4) != 0) {
            return true;
        }
        return false;
    }

    private boolean writeAudioEncodedBuffer(int bufferIndex) {
        ByteBuffer buffer = getOutputBuffer(this.mAudioEncoder, bufferIndex);
        buffer.position(this.mAudioBufferInfo.offset);
        if (this.mMuxerStarted.get()) {
            try {
                if (this.mAudioBufferInfo.size <= 0 || this.mAudioBufferInfo.presentationTimeUs <= 0) {
                    Logger.i(TAG, "mAudioBufferInfo size: " + this.mAudioBufferInfo.size + " presentationTimeUs: " + this.mAudioBufferInfo.presentationTimeUs);
                } else {
                    synchronized (this.mMuxerLock) {
                        if (!this.mIsFirstAudioSampleWrite.get()) {
                            Logger.i(TAG, "First audio sample written.");
                            this.mIsFirstAudioSampleWrite.set(true);
                        }
                        this.mMuxer.writeSampleData(this.mAudioTrackIndex, buffer, this.mAudioBufferInfo);
                    }
                }
            } catch (Exception e) {
                Logger.e(TAG, "audio error:size=" + this.mAudioBufferInfo.size + "/offset=" + this.mAudioBufferInfo.offset + "/timeUs=" + this.mAudioBufferInfo.presentationTimeUs);
                e.printStackTrace();
            }
        }
        this.mAudioEncoder.releaseOutputBuffer(bufferIndex, false);
        if ((this.mAudioBufferInfo.flags & 4) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00ee, code lost:
        r0 = th;
     */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0145 A[SYNTHETIC, Splitter:B:73:0x0145] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean videoEncode(androidx.camera.core.VideoCapture.OnVideoSavedCallback r16, java.lang.String r17, android.util.Size r18, androidx.camera.core.VideoCapture.OutputFileOptions r19) {
        /*
            r15 = this;
            r1 = r15
            r2 = r16
            r0 = 0
            r3 = 0
            r4 = r3
            r3 = r0
        L_0x0007:
            r5 = 0
            r6 = 1
            r7 = 0
            if (r4 != 0) goto L_0x009e
            if (r3 != 0) goto L_0x009e
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfVideoStreamSignal
            boolean r0 = r0.get()
            if (r0 == 0) goto L_0x0020
            android.media.MediaCodec r0 = r1.mVideoEncoder
            r0.signalEndOfInputStream()
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfVideoStreamSignal
            r0.set(r5)
        L_0x0020:
            android.media.MediaCodec r0 = r1.mVideoEncoder
            android.media.MediaCodec$BufferInfo r5 = r1.mVideoBufferInfo
            r8 = 10000(0x2710, double:4.9407E-320)
            int r5 = r0.dequeueOutputBuffer(r5, r8)
            switch(r5) {
                case -2: goto L_0x0034;
                case -1: goto L_0x0033;
                default: goto L_0x002d;
            }
        L_0x002d:
            boolean r0 = r15.writeVideoEncodedBuffer(r5)
            r4 = r0
            goto L_0x009c
        L_0x0033:
            goto L_0x009c
        L_0x0034:
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mMuxerStarted
            boolean r0 = r0.get()
            if (r0 == 0) goto L_0x0044
            java.lang.String r0 = "Unexpected change in video encoding format."
            r2.onError(r6, r0, r7)
            r3 = 1
            r7 = r3
            goto L_0x0045
        L_0x0044:
            r7 = r3
        L_0x0045:
            java.lang.Object r8 = r1.mMuxerLock
            monitor-enter(r8)
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x0099 }
            android.media.MediaCodec r3 = r1.mVideoEncoder     // Catch:{ all -> 0x0099 }
            android.media.MediaFormat r3 = r3.getOutputFormat()     // Catch:{ all -> 0x0099 }
            int r0 = r0.addTrack(r3)     // Catch:{ all -> 0x0099 }
            r1.mVideoTrackIndex = r0     // Catch:{ all -> 0x0099 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mIsAudioEnabled     // Catch:{ all -> 0x0099 }
            boolean r0 = r0.get()     // Catch:{ all -> 0x0099 }
            if (r0 == 0) goto L_0x0066
            int r0 = r1.mAudioTrackIndex     // Catch:{ all -> 0x0099 }
            if (r0 < 0) goto L_0x0066
            int r0 = r1.mVideoTrackIndex     // Catch:{ all -> 0x0099 }
            if (r0 >= 0) goto L_0x0072
        L_0x0066:
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mIsAudioEnabled     // Catch:{ all -> 0x0099 }
            boolean r0 = r0.get()     // Catch:{ all -> 0x0099 }
            if (r0 != 0) goto L_0x0096
            int r0 = r1.mVideoTrackIndex     // Catch:{ all -> 0x0099 }
            if (r0 < 0) goto L_0x0096
        L_0x0072:
            java.lang.String r0 = "VideoCapture"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0099 }
            r3.<init>()     // Catch:{ all -> 0x0099 }
            java.lang.String r9 = "MediaMuxer started on video encode thread and audio enabled: "
            java.lang.StringBuilder r3 = r3.append(r9)     // Catch:{ all -> 0x0099 }
            java.util.concurrent.atomic.AtomicBoolean r9 = r1.mIsAudioEnabled     // Catch:{ all -> 0x0099 }
            java.lang.StringBuilder r3 = r3.append(r9)     // Catch:{ all -> 0x0099 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0099 }
            androidx.camera.core.Logger.i(r0, r3)     // Catch:{ all -> 0x0099 }
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x0099 }
            r0.start()     // Catch:{ all -> 0x0099 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mMuxerStarted     // Catch:{ all -> 0x0099 }
            r0.set(r6)     // Catch:{ all -> 0x0099 }
        L_0x0096:
            monitor-exit(r8)     // Catch:{ all -> 0x0099 }
            r3 = r7
            goto L_0x009c
        L_0x0099:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0099 }
            throw r0
        L_0x009c:
            goto L_0x0007
        L_0x009e:
            java.lang.String r0 = "VideoCapture"
            java.lang.String r8 = "videoEncoder stop"
            androidx.camera.core.Logger.i(r0, r8)     // Catch:{ IllegalStateException -> 0x00ac }
            android.media.MediaCodec r0 = r1.mVideoEncoder     // Catch:{ IllegalStateException -> 0x00ac }
            r0.stop()     // Catch:{ IllegalStateException -> 0x00ac }
            goto L_0x00b3
        L_0x00ac:
            r0 = move-exception
            java.lang.String r8 = "Video encoder stop failed!"
            r2.onError(r6, r8, r0)
            r3 = 1
        L_0x00b3:
            r8 = 2
            r9 = 6
            java.lang.Object r10 = r1.mMuxerLock     // Catch:{ IllegalStateException -> 0x00f0 }
            monitor-enter(r10)     // Catch:{ IllegalStateException -> 0x00f0 }
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x00e7 }
            if (r0 == 0) goto L_0x00d7
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mMuxerStarted     // Catch:{ all -> 0x00e7 }
            boolean r0 = r0.get()     // Catch:{ all -> 0x00e7 }
            if (r0 == 0) goto L_0x00d0
            java.lang.String r0 = "VideoCapture"
            java.lang.String r11 = "Muxer already started"
            androidx.camera.core.Logger.i(r0, r11)     // Catch:{ all -> 0x00e7 }
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x00e7 }
            r0.stop()     // Catch:{ all -> 0x00e7 }
        L_0x00d0:
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x00e7 }
            r0.release()     // Catch:{ all -> 0x00e7 }
            r1.mMuxer = r7     // Catch:{ all -> 0x00e7 }
        L_0x00d7:
            monitor-exit(r10)     // Catch:{ all -> 0x00e7 }
            r11 = r19
            boolean r0 = r15.removeRecordingResultIfNoVideoKeyFrameArrived(r11)     // Catch:{ IllegalStateException -> 0x00ec }
            if (r0 != 0) goto L_0x00e6
            java.lang.String r10 = "The file has no video key frame."
            r2.onError(r9, r10, r7)     // Catch:{ IllegalStateException -> 0x00ec }
            r3 = 1
        L_0x00e6:
            goto L_0x0141
        L_0x00e7:
            r0 = move-exception
            r11 = r19
        L_0x00ea:
            monitor-exit(r10)     // Catch:{ all -> 0x00ee }
            throw r0     // Catch:{ IllegalStateException -> 0x00ec }
        L_0x00ec:
            r0 = move-exception
            goto L_0x00f3
        L_0x00ee:
            r0 = move-exception
            goto L_0x00ea
        L_0x00f0:
            r0 = move-exception
            r11 = r19
        L_0x00f3:
            java.lang.String r10 = "VideoCapture"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "muxer stop IllegalStateException: "
            java.lang.StringBuilder r12 = r12.append(r13)
            long r13 = java.lang.System.currentTimeMillis()
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            androidx.camera.core.Logger.i(r10, r12)
            java.lang.String r10 = "VideoCapture"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "muxer stop exception, mIsFirstVideoKeyFrameWrite: "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.util.concurrent.atomic.AtomicBoolean r13 = r1.mIsFirstVideoKeyFrameWrite
            boolean r13 = r13.get()
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            androidx.camera.core.Logger.i(r10, r12)
            java.util.concurrent.atomic.AtomicBoolean r10 = r1.mIsFirstVideoKeyFrameWrite
            boolean r10 = r10.get()
            if (r10 == 0) goto L_0x013b
            java.lang.String r9 = "Muxer stop failed!"
            r2.onError(r8, r9, r0)
            goto L_0x0140
        L_0x013b:
            java.lang.String r10 = "The file has no video key frame."
            r2.onError(r9, r10, r7)
        L_0x0140:
            r3 = 1
        L_0x0141:
            android.os.ParcelFileDescriptor r0 = r1.mParcelFileDescriptor
            if (r0 == 0) goto L_0x0154
            android.os.ParcelFileDescriptor r0 = r1.mParcelFileDescriptor     // Catch:{ IOException -> 0x014d }
            r0.close()     // Catch:{ IOException -> 0x014d }
            r1.mParcelFileDescriptor = r7     // Catch:{ IOException -> 0x014d }
            goto L_0x0154
        L_0x014d:
            r0 = move-exception
            java.lang.String r7 = "File descriptor close failed!"
            r2.onError(r8, r7, r0)
            r3 = 1
        L_0x0154:
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mMuxerStarted
            r0.set(r5)
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfAudioVideoSignal
            r0.set(r6)
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mIsFirstVideoKeyFrameWrite
            r0.set(r5)
            java.lang.String r0 = "VideoCapture"
            java.lang.String r5 = "Video encode thread end."
            androidx.camera.core.Logger.i(r0, r5)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.VideoCapture.videoEncode(androidx.camera.core.VideoCapture$OnVideoSavedCallback, java.lang.String, android.util.Size, androidx.camera.core.VideoCapture$OutputFileOptions):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* renamed from: audioEncode */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean m164lambda$startRecording$3$androidxcameracoreVideoCapture(androidx.camera.core.VideoCapture.OnVideoSavedCallback r18) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r0 = 0
            r3 = 0
            r4 = r3
            r3 = r0
        L_0x0009:
            r6 = 1
            r7 = 0
            if (r3 != 0) goto L_0x011d
            boolean r0 = r1.mIsRecording
            if (r0 == 0) goto L_0x011d
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfAudioStreamSignal
            boolean r0 = r0.get()
            if (r0 == 0) goto L_0x0020
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfAudioStreamSignal
            r0.set(r7)
            r1.mIsRecording = r7
        L_0x0020:
            android.media.MediaCodec r0 = r1.mAudioEncoder
            if (r0 == 0) goto L_0x0009
            android.media.AudioRecord r0 = r1.mAudioRecorder
            if (r0 == 0) goto L_0x0009
            android.media.MediaCodec r0 = r1.mAudioEncoder     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            r8 = -1
            int r0 = r0.dequeueInputBuffer(r8)     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            if (r0 < 0) goto L_0x009d
            android.media.MediaCodec r8 = r1.mAudioEncoder     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            java.nio.ByteBuffer r8 = r1.getInputBuffer(r8, r0)     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            r15 = r8
            r15.clear()     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            android.media.AudioRecord r8 = r1.mAudioRecorder     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            int r9 = r1.mAudioBufferSize     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            int r8 = r8.read(r15, r9)     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            r16 = r8
            if (r16 <= 0) goto L_0x009d
            android.media.MediaCodec r8 = r1.mAudioEncoder     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            r10 = 0
            long r11 = java.lang.System.nanoTime()     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            r13 = 1000(0x3e8, double:4.94E-321)
            long r12 = r11 / r13
            boolean r9 = r1.mIsRecording     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            if (r9 == 0) goto L_0x0059
            r14 = r7
            goto L_0x005b
        L_0x0059:
            r9 = 4
            r14 = r9
        L_0x005b:
            r9 = r0
            r11 = r16
            r8.queueInputBuffer(r9, r10, r11, r12, r14)     // Catch:{ CodecException -> 0x0080, IllegalStateException -> 0x0062 }
            goto L_0x009d
        L_0x0062:
            r0 = move-exception
            java.lang.String r8 = "VideoCapture"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "audio dequeueInputBuffer IllegalStateException "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r0.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            androidx.camera.core.Logger.i(r8, r9)
            goto L_0x009e
        L_0x0080:
            r0 = move-exception
            java.lang.String r8 = "VideoCapture"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "audio dequeueInputBuffer CodecException "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r0.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            androidx.camera.core.Logger.i(r8, r9)
        L_0x009d:
        L_0x009e:
            android.media.MediaCodec r0 = r1.mAudioEncoder
            android.media.MediaCodec$BufferInfo r8 = r1.mAudioBufferInfo
            r9 = 0
            int r8 = r0.dequeueOutputBuffer(r8, r9)
            switch(r8) {
                case -2: goto L_0x00bf;
                case -1: goto L_0x00be;
                default: goto L_0x00ab;
            }
        L_0x00ab:
            android.media.MediaCodec$BufferInfo r0 = r1.mAudioBufferInfo
            long r9 = r0.presentationTimeUs
            int r0 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x00ec
            boolean r0 = r1.writeAudioEncodedBuffer(r8)
            android.media.MediaCodec$BufferInfo r3 = r1.mAudioBufferInfo
            long r3 = r3.presentationTimeUs
            r4 = r3
            r3 = r0
            goto L_0x0117
        L_0x00be:
            goto L_0x0117
        L_0x00bf:
            java.lang.Object r9 = r1.mMuxerLock
            monitor-enter(r9)
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x00e9 }
            android.media.MediaCodec r10 = r1.mAudioEncoder     // Catch:{ all -> 0x00e9 }
            android.media.MediaFormat r10 = r10.getOutputFormat()     // Catch:{ all -> 0x00e9 }
            int r0 = r0.addTrack(r10)     // Catch:{ all -> 0x00e9 }
            r1.mAudioTrackIndex = r0     // Catch:{ all -> 0x00e9 }
            if (r0 < 0) goto L_0x00e7
            int r0 = r1.mVideoTrackIndex     // Catch:{ all -> 0x00e9 }
            if (r0 < 0) goto L_0x00e7
            java.lang.String r0 = "VideoCapture"
            java.lang.String r10 = "MediaMuxer start on audio encoder thread."
            androidx.camera.core.Logger.i(r0, r10)     // Catch:{ all -> 0x00e9 }
            android.media.MediaMuxer r0 = r1.mMuxer     // Catch:{ all -> 0x00e9 }
            r0.start()     // Catch:{ all -> 0x00e9 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mMuxerStarted     // Catch:{ all -> 0x00e9 }
            r0.set(r6)     // Catch:{ all -> 0x00e9 }
        L_0x00e7:
            monitor-exit(r9)     // Catch:{ all -> 0x00e9 }
            goto L_0x0117
        L_0x00e9:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00e9 }
            throw r0
        L_0x00ec:
            java.lang.String r0 = "VideoCapture"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Drops frame, current frame's timestamp "
            java.lang.StringBuilder r9 = r9.append(r10)
            android.media.MediaCodec$BufferInfo r10 = r1.mAudioBufferInfo
            long r10 = r10.presentationTimeUs
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = " is earlier that last frame "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.StringBuilder r9 = r9.append(r4)
            java.lang.String r9 = r9.toString()
            androidx.camera.core.Logger.w(r0, r9)
            android.media.MediaCodec r0 = r1.mAudioEncoder
            r0.releaseOutputBuffer(r8, r7)
        L_0x0117:
            if (r8 < 0) goto L_0x0009
            if (r3 == 0) goto L_0x009e
            goto L_0x0009
        L_0x011d:
            java.lang.String r0 = "VideoCapture"
            java.lang.String r8 = "audioRecorder stop"
            androidx.camera.core.Logger.i(r0, r8)     // Catch:{ IllegalStateException -> 0x012a }
            android.media.AudioRecord r0 = r1.mAudioRecorder     // Catch:{ IllegalStateException -> 0x012a }
            r0.stop()     // Catch:{ IllegalStateException -> 0x012a }
            goto L_0x0130
        L_0x012a:
            r0 = move-exception
            java.lang.String r8 = "Audio recorder stop failed!"
            r2.onError(r6, r8, r0)
        L_0x0130:
            android.media.MediaCodec r0 = r1.mAudioEncoder     // Catch:{ IllegalStateException -> 0x0136 }
            r0.stop()     // Catch:{ IllegalStateException -> 0x0136 }
            goto L_0x013c
        L_0x0136:
            r0 = move-exception
            java.lang.String r8 = "Audio encoder stop failed!"
            r2.onError(r6, r8, r0)
        L_0x013c:
            java.lang.String r0 = "VideoCapture"
            java.lang.String r8 = "Audio encode thread end"
            androidx.camera.core.Logger.i(r0, r8)
            java.util.concurrent.atomic.AtomicBoolean r0 = r1.mEndOfVideoStreamSignal
            r0.set(r6)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.VideoCapture.m164lambda$startRecording$3$androidxcameracoreVideoCapture(androidx.camera.core.VideoCapture$OnVideoSavedCallback):boolean");
    }

    private ByteBuffer getInputBuffer(MediaCodec codec, int index) {
        return codec.getInputBuffer(index);
    }

    private ByteBuffer getOutputBuffer(MediaCodec codec, int index) {
        return codec.getOutputBuffer(index);
    }

    private MediaFormat createAudioMediaFormat() {
        MediaFormat format = MediaFormat.createAudioFormat(AUDIO_MIME_TYPE, this.mAudioSampleRate, this.mAudioChannelCount);
        format.setInteger("aac-profile", 2);
        format.setInteger("bitrate", this.mAudioBitRate);
        return format;
    }

    private AudioRecord autoConfigAudioRecordSource(VideoCaptureConfig config) {
        int channelConfig;
        int bufferSize;
        if (this.mAudioChannelCount == 1) {
            channelConfig = 16;
        } else {
            channelConfig = 12;
        }
        try {
            int bufferSize2 = AudioRecord.getMinBufferSize(this.mAudioSampleRate, channelConfig, 2);
            if (bufferSize2 <= 0) {
                bufferSize = config.getAudioMinBufferSize();
            } else {
                bufferSize = bufferSize2;
            }
            AudioRecord recorder = new AudioRecord(5, this.mAudioSampleRate, channelConfig, 2, bufferSize * 2);
            if (recorder.getState() != 1) {
                return null;
            }
            this.mAudioBufferSize = bufferSize;
            Logger.i(TAG, "source: 5 audioSampleRate: " + this.mAudioSampleRate + " channelConfig: " + channelConfig + " audioFormat: " + 2 + " bufferSize: " + bufferSize);
            return recorder;
        } catch (Exception e) {
            Logger.e(TAG, "Exception, keep trying.", e);
            return null;
        }
    }

    private void setAudioParametersByCamcorderProfile(Size currentResolution, String cameraId) {
        boolean isCamcorderProfileFound = false;
        try {
            int[] iArr = CamcorderQuality;
            int length = iArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                int quality = iArr[i];
                if (CamcorderProfile.hasProfile(Integer.parseInt(cameraId), quality)) {
                    CamcorderProfile profile = CamcorderProfile.get(Integer.parseInt(cameraId), quality);
                    if (currentResolution.getWidth() == profile.videoFrameWidth && currentResolution.getHeight() == profile.videoFrameHeight) {
                        this.mAudioChannelCount = profile.audioChannels;
                        this.mAudioSampleRate = profile.audioSampleRate;
                        this.mAudioBitRate = profile.audioBitRate;
                        isCamcorderProfileFound = true;
                        break;
                    }
                }
                i++;
            }
        } catch (NumberFormatException e) {
            Logger.i(TAG, "The camera Id is not an integer because the camera may be a removable device. Use the default values for the audio related settings.");
        }
        if (!isCamcorderProfileFound) {
            VideoCaptureConfig config = (VideoCaptureConfig) getCurrentConfig();
            this.mAudioChannelCount = config.getAudioChannelCount();
            this.mAudioSampleRate = config.getAudioSampleRate();
            this.mAudioBitRate = config.getAudioBitRate();
        }
    }

    private boolean removeRecordingResultIfNoVideoKeyFrameArrived(OutputFileOptions outputFileOptions) {
        boolean checkKeyFrame;
        Logger.i(TAG, "check Recording Result First Video Key Frame Write: " + this.mIsFirstVideoKeyFrameWrite.get());
        if (!this.mIsFirstVideoKeyFrameWrite.get()) {
            Logger.i(TAG, "The recording result has no key frame.");
            checkKeyFrame = false;
        } else {
            checkKeyFrame = true;
        }
        if (outputFileOptions.isSavingToFile()) {
            File outputFile = outputFileOptions.getFile();
            if (!checkKeyFrame) {
                Logger.i(TAG, "Delete file.");
                outputFile.delete();
            }
        } else if (outputFileOptions.isSavingToMediaStore() && !checkKeyFrame) {
            Logger.i(TAG, "Delete file.");
            if (this.mSavedVideoUri != null) {
                outputFileOptions.getContentResolver().delete(this.mSavedVideoUri, (String) null, (String[]) null);
            }
        }
        return checkKeyFrame;
    }

    private MediaMuxer initMediaMuxer(OutputFileOptions outputFileOptions) throws IOException {
        ContentValues values;
        MediaMuxer mediaMuxer;
        if (outputFileOptions.isSavingToFile()) {
            File savedVideoFile = outputFileOptions.getFile();
            this.mSavedVideoUri = Uri.fromFile(outputFileOptions.getFile());
            return new MediaMuxer(savedVideoFile.getAbsolutePath(), 0);
        } else if (outputFileOptions.isSavingToFileDescriptor()) {
            if (Build.VERSION.SDK_INT >= 26) {
                return Api26Impl.createMediaMuxer(outputFileOptions.getFileDescriptor(), 0);
            }
            throw new IllegalArgumentException("Using a FileDescriptor to record a video is only supported for Android 8.0 or above.");
        } else if (outputFileOptions.isSavingToMediaStore()) {
            if (outputFileOptions.getContentValues() != null) {
                values = new ContentValues(outputFileOptions.getContentValues());
            } else {
                values = new ContentValues();
            }
            this.mSavedVideoUri = outputFileOptions.getContentResolver().insert(outputFileOptions.getSaveCollection(), values);
            if (this.mSavedVideoUri != null) {
                try {
                    if (Build.VERSION.SDK_INT < 26) {
                        String savedLocationPath = VideoUtil.getAbsolutePathFromUri(outputFileOptions.getContentResolver(), this.mSavedVideoUri);
                        Logger.i(TAG, "Saved Location Path: " + savedLocationPath);
                        mediaMuxer = new MediaMuxer(savedLocationPath, 0);
                    } else {
                        this.mParcelFileDescriptor = outputFileOptions.getContentResolver().openFileDescriptor(this.mSavedVideoUri, "rw");
                        mediaMuxer = Api26Impl.createMediaMuxer(this.mParcelFileDescriptor.getFileDescriptor(), 0);
                    }
                    return mediaMuxer;
                } catch (IOException e) {
                    this.mSavedVideoUri = null;
                    throw e;
                }
            } else {
                throw new IOException("Invalid Uri!");
            }
        } else {
            throw new IllegalArgumentException("The OutputFileOptions should assign before recording");
        }
    }

    public static final class Defaults implements ConfigProvider<VideoCaptureConfig> {
        private static final int DEFAULT_ASPECT_RATIO = 1;
        private static final int DEFAULT_AUDIO_BIT_RATE = 64000;
        private static final int DEFAULT_AUDIO_CHANNEL_COUNT = 1;
        private static final int DEFAULT_AUDIO_MIN_BUFFER_SIZE = 1024;
        private static final int DEFAULT_AUDIO_SAMPLE_RATE = 8000;
        private static final int DEFAULT_BIT_RATE = 8388608;
        private static final VideoCaptureConfig DEFAULT_CONFIG;
        private static final int DEFAULT_INTRA_FRAME_INTERVAL = 1;
        private static final Size DEFAULT_MAX_RESOLUTION;
        private static final int DEFAULT_SURFACE_OCCUPANCY_PRIORITY = 3;
        private static final int DEFAULT_VIDEO_FRAME_RATE = 30;

        static {
            Size size = new Size(1920, 1080);
            DEFAULT_MAX_RESOLUTION = size;
            DEFAULT_CONFIG = new Builder().setVideoFrameRate(30).setBitRate(8388608).setIFrameInterval(1).setAudioBitRate(DEFAULT_AUDIO_BIT_RATE).setAudioSampleRate(DEFAULT_AUDIO_SAMPLE_RATE).setAudioChannelCount(1).setAudioMinBufferSize(1024).setMaxResolution(size).setSurfaceOccupancyPriority(3).setTargetAspectRatio(1).getUseCaseConfig();
        }

        public VideoCaptureConfig getConfig() {
            return DEFAULT_CONFIG;
        }
    }

    private static final class VideoSavedListenerWrapper implements OnVideoSavedCallback {
        Executor mExecutor;
        OnVideoSavedCallback mOnVideoSavedCallback;

        VideoSavedListenerWrapper(Executor executor, OnVideoSavedCallback onVideoSavedCallback) {
            this.mExecutor = executor;
            this.mOnVideoSavedCallback = onVideoSavedCallback;
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onVideoSaved$0$androidx-camera-core-VideoCapture$VideoSavedListenerWrapper  reason: not valid java name */
        public /* synthetic */ void m168lambda$onVideoSaved$0$androidxcameracoreVideoCapture$VideoSavedListenerWrapper(OutputFileResults outputFileResults) {
            this.mOnVideoSavedCallback.onVideoSaved(outputFileResults);
        }

        public void onVideoSaved(OutputFileResults outputFileResults) {
            try {
                this.mExecutor.execute(new VideoCapture$VideoSavedListenerWrapper$$ExternalSyntheticLambda1(this, outputFileResults));
            } catch (RejectedExecutionException e) {
                Logger.e(VideoCapture.TAG, "Unable to post to the supplied executor.");
            }
        }

        public void onError(int videoCaptureError, String message, Throwable cause) {
            try {
                this.mExecutor.execute(new VideoCapture$VideoSavedListenerWrapper$$ExternalSyntheticLambda0(this, videoCaptureError, message, cause));
            } catch (RejectedExecutionException e) {
                Logger.e(VideoCapture.TAG, "Unable to post to the supplied executor.");
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onError$1$androidx-camera-core-VideoCapture$VideoSavedListenerWrapper  reason: not valid java name */
        public /* synthetic */ void m167lambda$onError$1$androidxcameracoreVideoCapture$VideoSavedListenerWrapper(int videoCaptureError, String message, Throwable cause) {
            this.mOnVideoSavedCallback.onError(videoCaptureError, message, cause);
        }
    }

    public static final class Builder implements UseCaseConfig.Builder<VideoCapture, VideoCaptureConfig, Builder>, ImageOutputConfig.Builder<Builder>, ThreadConfig.Builder<Builder> {
        private final MutableOptionsBundle mMutableConfig;

        public Builder() {
            this(MutableOptionsBundle.create());
        }

        private Builder(MutableOptionsBundle mutableConfig) {
            this.mMutableConfig = mutableConfig;
            Class<?> oldConfigClass = (Class) mutableConfig.retrieveOption(TargetConfig.OPTION_TARGET_CLASS, null);
            if (oldConfigClass == null || oldConfigClass.equals(VideoCapture.class)) {
                setTargetClass((Class<VideoCapture>) VideoCapture.class);
                return;
            }
            throw new IllegalArgumentException("Invalid target class configuration for " + this + ": " + oldConfigClass);
        }

        static Builder fromConfig(Config configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public static Builder fromConfig(VideoCaptureConfig configuration) {
            return new Builder(MutableOptionsBundle.from(configuration));
        }

        public MutableConfig getMutableConfig() {
            return this.mMutableConfig;
        }

        public VideoCaptureConfig getUseCaseConfig() {
            return new VideoCaptureConfig(OptionsBundle.from(this.mMutableConfig));
        }

        public VideoCapture build() {
            if (getMutableConfig().retrieveOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO, null) == null || getMutableConfig().retrieveOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION, null) == null) {
                return new VideoCapture(getUseCaseConfig());
            }
            throw new IllegalArgumentException("Cannot use both setTargetResolution and setTargetAspectRatio on the same config.");
        }

        public Builder setVideoFrameRate(int videoFrameRate) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_VIDEO_FRAME_RATE, Integer.valueOf(videoFrameRate));
            return this;
        }

        public Builder setBitRate(int bitRate) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_BIT_RATE, Integer.valueOf(bitRate));
            return this;
        }

        public Builder setIFrameInterval(int interval) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_INTRA_FRAME_INTERVAL, Integer.valueOf(interval));
            return this;
        }

        public Builder setAudioBitRate(int bitRate) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_AUDIO_BIT_RATE, Integer.valueOf(bitRate));
            return this;
        }

        public Builder setAudioSampleRate(int sampleRate) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_AUDIO_SAMPLE_RATE, Integer.valueOf(sampleRate));
            return this;
        }

        public Builder setAudioChannelCount(int channelCount) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_AUDIO_CHANNEL_COUNT, Integer.valueOf(channelCount));
            return this;
        }

        public Builder setAudioMinBufferSize(int minBufferSize) {
            getMutableConfig().insertOption(VideoCaptureConfig.OPTION_AUDIO_MIN_BUFFER_SIZE, Integer.valueOf(minBufferSize));
            return this;
        }

        public Builder setTargetClass(Class<VideoCapture> targetClass) {
            getMutableConfig().insertOption(TargetConfig.OPTION_TARGET_CLASS, targetClass);
            if (getMutableConfig().retrieveOption(TargetConfig.OPTION_TARGET_NAME, null) == null) {
                setTargetName(targetClass.getCanonicalName() + "-" + UUID.randomUUID());
            }
            return this;
        }

        public Builder setTargetName(String targetName) {
            getMutableConfig().insertOption(TargetConfig.OPTION_TARGET_NAME, targetName);
            return this;
        }

        public Builder setTargetAspectRatio(int aspectRatio) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO, Integer.valueOf(aspectRatio));
            return this;
        }

        public Builder setTargetRotation(int rotation) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_ROTATION, Integer.valueOf(rotation));
            return this;
        }

        public Builder setTargetResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION, resolution);
            return this;
        }

        public Builder setDefaultResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_DEFAULT_RESOLUTION, resolution);
            return this;
        }

        public Builder setMaxResolution(Size resolution) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_MAX_RESOLUTION, resolution);
            return this;
        }

        public Builder setSupportedResolutions(List<Pair<Integer, Size[]>> resolutions) {
            getMutableConfig().insertOption(ImageOutputConfig.OPTION_SUPPORTED_RESOLUTIONS, resolutions);
            return this;
        }

        public Builder setBackgroundExecutor(Executor executor) {
            getMutableConfig().insertOption(ThreadConfig.OPTION_BACKGROUND_EXECUTOR, executor);
            return this;
        }

        public Builder setDefaultSessionConfig(SessionConfig sessionConfig) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_DEFAULT_SESSION_CONFIG, sessionConfig);
            return this;
        }

        public Builder setDefaultCaptureConfig(CaptureConfig captureConfig) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_DEFAULT_CAPTURE_CONFIG, captureConfig);
            return this;
        }

        public Builder setSessionOptionUnpacker(SessionConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_SESSION_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setCaptureOptionUnpacker(CaptureConfig.OptionUnpacker optionUnpacker) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAPTURE_CONFIG_UNPACKER, optionUnpacker);
            return this;
        }

        public Builder setSurfaceOccupancyPriority(int priority) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_SURFACE_OCCUPANCY_PRIORITY, Integer.valueOf(priority));
            return this;
        }

        public Builder setCameraSelector(CameraSelector cameraSelector) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_CAMERA_SELECTOR, cameraSelector);
            return this;
        }

        public Builder setUseCaseEventCallback(UseCase.EventCallback useCaseEventCallback) {
            getMutableConfig().insertOption(UseCaseEventConfig.OPTION_USE_CASE_EVENT_CALLBACK, useCaseEventCallback);
            return this;
        }

        public Builder setZslDisabled(boolean disabled) {
            getMutableConfig().insertOption(UseCaseConfig.OPTION_ZSL_DISABLED, Boolean.valueOf(disabled));
            return this;
        }
    }

    public static class OutputFileResults {
        private Uri mSavedUri;

        OutputFileResults(Uri savedUri) {
            this.mSavedUri = savedUri;
        }

        public Uri getSavedUri() {
            return this.mSavedUri;
        }
    }

    public static final class OutputFileOptions {
        private static final Metadata EMPTY_METADATA = new Metadata();
        private final ContentResolver mContentResolver;
        private final ContentValues mContentValues;
        private final File mFile;
        private final FileDescriptor mFileDescriptor;
        private final Metadata mMetadata;
        private final Uri mSaveCollection;

        OutputFileOptions(File file, FileDescriptor fileDescriptor, ContentResolver contentResolver, Uri saveCollection, ContentValues contentValues, Metadata metadata) {
            this.mFile = file;
            this.mFileDescriptor = fileDescriptor;
            this.mContentResolver = contentResolver;
            this.mSaveCollection = saveCollection;
            this.mContentValues = contentValues;
            this.mMetadata = metadata == null ? EMPTY_METADATA : metadata;
        }

        /* access modifiers changed from: package-private */
        public File getFile() {
            return this.mFile;
        }

        /* access modifiers changed from: package-private */
        public FileDescriptor getFileDescriptor() {
            return this.mFileDescriptor;
        }

        /* access modifiers changed from: package-private */
        public ContentResolver getContentResolver() {
            return this.mContentResolver;
        }

        /* access modifiers changed from: package-private */
        public Uri getSaveCollection() {
            return this.mSaveCollection;
        }

        /* access modifiers changed from: package-private */
        public ContentValues getContentValues() {
            return this.mContentValues;
        }

        /* access modifiers changed from: package-private */
        public Metadata getMetadata() {
            return this.mMetadata;
        }

        /* access modifiers changed from: package-private */
        public boolean isSavingToMediaStore() {
            return (getSaveCollection() == null || getContentResolver() == null || getContentValues() == null) ? false : true;
        }

        /* access modifiers changed from: package-private */
        public boolean isSavingToFile() {
            return getFile() != null;
        }

        /* access modifiers changed from: package-private */
        public boolean isSavingToFileDescriptor() {
            return getFileDescriptor() != null;
        }

        public static final class Builder {
            private ContentResolver mContentResolver;
            private ContentValues mContentValues;
            private File mFile;
            private FileDescriptor mFileDescriptor;
            private Metadata mMetadata;
            private Uri mSaveCollection;

            public Builder(File file) {
                this.mFile = file;
            }

            public Builder(FileDescriptor fileDescriptor) {
                Preconditions.checkArgument(Build.VERSION.SDK_INT >= 26, "Using a FileDescriptor to record a video is only supported for Android 8.0 or above.");
                this.mFileDescriptor = fileDescriptor;
            }

            public Builder(ContentResolver contentResolver, Uri saveCollection, ContentValues contentValues) {
                this.mContentResolver = contentResolver;
                this.mSaveCollection = saveCollection;
                this.mContentValues = contentValues;
            }

            public Builder setMetadata(Metadata metadata) {
                this.mMetadata = metadata;
                return this;
            }

            public OutputFileOptions build() {
                return new OutputFileOptions(this.mFile, this.mFileDescriptor, this.mContentResolver, this.mSaveCollection, this.mContentValues, this.mMetadata);
            }
        }
    }

    private static class Api26Impl {
        private Api26Impl() {
        }

        static MediaMuxer createMediaMuxer(FileDescriptor fileDescriptor, int format) throws IOException {
            return new MediaMuxer(fileDescriptor, format);
        }
    }

    private static class Api23Impl {
        private Api23Impl() {
        }

        static int getCodecExceptionErrorCode(MediaCodec.CodecException e) {
            return e.getErrorCode();
        }
    }
}
