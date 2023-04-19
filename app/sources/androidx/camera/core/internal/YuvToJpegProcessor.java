package androidx.camera.core.internal;

import android.graphics.Rect;
import android.media.ImageWriter;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.utils.ExifData;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.core.internal.compat.ImageWriterCompat;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class YuvToJpegProcessor implements CaptureProcessor {
    private static final String TAG = "YuvToJpegProcessor";
    private static final Rect UNINITIALIZED_RECT = new Rect(0, 0, 0, 0);
    CallbackToFutureAdapter.Completer<Void> mCloseCompleter;
    private ListenableFuture<Void> mCloseFuture;
    private boolean mClosed = false;
    private Rect mImageRect = UNINITIALIZED_RECT;
    private ImageWriter mImageWriter;
    private final Object mLock = new Object();
    private final int mMaxImages;
    private int mProcessingImages = 0;
    private int mQuality;
    private int mRotationDegrees = 0;

    public YuvToJpegProcessor(int quality, int maxImages) {
        this.mQuality = quality;
        this.mMaxImages = maxImages;
    }

    public void setJpegQuality(int quality) {
        synchronized (this.mLock) {
            this.mQuality = quality;
        }
    }

    public void setRotationDegrees(int rotationDegrees) {
        synchronized (this.mLock) {
            this.mRotationDegrees = rotationDegrees;
        }
    }

    public void onOutputSurface(Surface surface, int imageFormat) {
        Preconditions.checkState(imageFormat == 256, "YuvToJpegProcessor only supports JPEG output format.");
        synchronized (this.mLock) {
            if (this.mClosed) {
                Logger.w(TAG, "Cannot set output surface. Processor is closed.");
            } else if (this.mImageWriter == null) {
                this.mImageWriter = ImageWriterCompat.newInstance(surface, this.mMaxImages, imageFormat);
            } else {
                throw new IllegalStateException("Output surface already set.");
            }
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    public void process(androidx.camera.core.impl.ImageProxyBundle r24) {
        /*
            r23 = this;
            r1 = r23
            java.util.List r2 = r24.getCaptureIds()
            int r0 = r2.size()
            r3 = 0
            r4 = 1
            if (r0 != r4) goto L_0x0010
            r0 = r4
            goto L_0x0011
        L_0x0010:
            r0 = r3
        L_0x0011:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Processing image bundle have single capture id, but found "
            java.lang.StringBuilder r5 = r5.append(r6)
            int r6 = r2.size()
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            androidx.core.util.Preconditions.checkArgument(r0, r5)
            java.lang.Object r0 = r2.get(r3)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r5 = r24
            com.google.common.util.concurrent.ListenableFuture r6 = r5.getImageProxy(r0)
            boolean r0 = r6.isDone()
            androidx.core.util.Preconditions.checkArgument(r0)
            java.lang.Object r7 = r1.mLock
            monitor-enter(r7)
            android.media.ImageWriter r0 = r1.mImageWriter     // Catch:{ all -> 0x0223 }
            r8 = r0
            boolean r0 = r1.mClosed     // Catch:{ all -> 0x0223 }
            if (r0 != 0) goto L_0x004e
            r0 = r4
            goto L_0x004f
        L_0x004e:
            r0 = r3
        L_0x004f:
            r9 = r0
            android.graphics.Rect r0 = r1.mImageRect     // Catch:{ all -> 0x0223 }
            r10 = r0
            if (r9 == 0) goto L_0x0060
            int r0 = r1.mProcessingImages     // Catch:{ all -> 0x005b }
            int r0 = r0 + r4
            r1.mProcessingImages = r0     // Catch:{ all -> 0x005b }
            goto L_0x0060
        L_0x005b:
            r0 = move-exception
            r20 = r2
            goto L_0x0226
        L_0x0060:
            int r0 = r1.mQuality     // Catch:{ all -> 0x0223 }
            r11 = r0
            int r0 = r1.mRotationDegrees     // Catch:{ all -> 0x0223 }
            r12 = r0
            monitor-exit(r7)     // Catch:{ all -> 0x0223 }
            r7 = 0
            r13 = 0
            r14 = 0
            java.lang.Object r0 = r6.get()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            androidx.camera.core.ImageProxy r0 = (androidx.camera.core.ImageProxy) r0     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r7 = r0
            if (r9 != 0) goto L_0x00c2
            java.lang.String r0 = "YuvToJpegProcessor"
            java.lang.String r15 = "Image enqueued for processing on closed processor."
            androidx.camera.core.Logger.w(r0, r15)     // Catch:{ Exception -> 0x00bc, all -> 0x00b5 }
            r7.close()     // Catch:{ Exception -> 0x00bc, all -> 0x00b5 }
            r15 = 0
            java.lang.Object r7 = r1.mLock
            monitor-enter(r7)
            if (r9 == 0) goto L_0x0093
            int r0 = r1.mProcessingImages     // Catch:{ all -> 0x0091 }
            int r4 = r0 + -1
            r1.mProcessingImages = r4     // Catch:{ all -> 0x0091 }
            if (r0 != 0) goto L_0x0093
            boolean r0 = r1.mClosed     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x0093
            r3 = 1
            goto L_0x0093
        L_0x0091:
            r0 = move-exception
            goto L_0x00b3
        L_0x0093:
            r0 = r3
            androidx.concurrent.futures.CallbackToFutureAdapter$Completer<java.lang.Void> r3 = r1.mCloseCompleter     // Catch:{ all -> 0x0091 }
            monitor-exit(r7)     // Catch:{ all -> 0x0091 }
            if (r13 == 0) goto L_0x009c
            r13.close()
        L_0x009c:
            if (r15 == 0) goto L_0x00a1
            r15.close()
        L_0x00a1:
            if (r0 == 0) goto L_0x00b2
            r8.close()
            java.lang.String r4 = "YuvToJpegProcessor"
            java.lang.String r7 = "Closed after completion of last image processed."
            androidx.camera.core.Logger.d(r4, r7)
            if (r3 == 0) goto L_0x00b2
            r3.set(r14)
        L_0x00b2:
            return
        L_0x00b3:
            monitor-exit(r7)     // Catch:{ all -> 0x0091 }
            throw r0
        L_0x00b5:
            r0 = move-exception
            r20 = r2
            r4 = r3
            r3 = r13
            goto L_0x01a8
        L_0x00bc:
            r0 = move-exception
            r20 = r2
            r2 = r7
            goto L_0x017d
        L_0x00c2:
            android.media.Image r0 = r8.dequeueInputImage()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r13 = r0
            java.lang.Object r0 = r6.get()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            androidx.camera.core.ImageProxy r0 = (androidx.camera.core.ImageProxy) r0     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r7 = r0
            int r0 = r7.getFormat()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r4 = 35
            if (r0 != r4) goto L_0x00d8
            r0 = 1
            goto L_0x00d9
        L_0x00d8:
            r0 = r3
        L_0x00d9:
            java.lang.String r4 = "Input image is not expected YUV_420_888 image format"
            androidx.core.util.Preconditions.checkState(r0, r4)     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            byte[] r18 = androidx.camera.core.internal.utils.ImageUtil.yuv_420_888toNv21(r7)     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            android.graphics.YuvImage r0 = new android.graphics.YuvImage     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r19 = 17
            int r20 = r7.getWidth()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            int r21 = r7.getHeight()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r22 = 0
            r17 = r0
            r17.<init>(r18, r19, r20, r21, r22)     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            android.media.Image$Plane[] r4 = r13.getPlanes()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            r4 = r4[r3]     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            java.nio.ByteBuffer r4 = r4.getBuffer()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            int r15 = r4.position()     // Catch:{ Exception -> 0x0179, all -> 0x0173 }
            androidx.camera.core.impl.utils.ExifOutputStream r3 = new androidx.camera.core.impl.utils.ExifOutputStream     // Catch:{ Exception -> 0x0179, all -> 0x016d }
            androidx.camera.core.internal.YuvToJpegProcessor$ByteBufferOutputStream r14 = new androidx.camera.core.internal.YuvToJpegProcessor$ByteBufferOutputStream     // Catch:{ Exception -> 0x0179, all -> 0x016d }
            r14.<init>(r4)     // Catch:{ Exception -> 0x0179, all -> 0x016d }
            r20 = r2
            androidx.camera.core.impl.utils.ExifData r2 = getExifData(r7, r12)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r3.<init>(r14, r2)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r2 = r3
            r0.compressToJpeg(r10, r11, r2)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r7.close()     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r7 = 0
            int r3 = r4.position()     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r4.limit(r3)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r4.position(r15)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r8.queueInputImage(r13)     // Catch:{ Exception -> 0x016a, all -> 0x0166 }
            r2 = 0
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            if (r9 == 0) goto L_0x013f
            int r0 = r1.mProcessingImages     // Catch:{ all -> 0x013d }
            int r4 = r0 + -1
            r1.mProcessingImages = r4     // Catch:{ all -> 0x013d }
            if (r0 != 0) goto L_0x013f
            boolean r0 = r1.mClosed     // Catch:{ all -> 0x013d }
            if (r0 == 0) goto L_0x013f
            r16 = 1
            goto L_0x0141
        L_0x013d:
            r0 = move-exception
            goto L_0x0164
        L_0x013f:
            r16 = 0
        L_0x0141:
            r0 = r16
            androidx.concurrent.futures.CallbackToFutureAdapter$Completer<java.lang.Void> r4 = r1.mCloseCompleter     // Catch:{ all -> 0x013d }
            monitor-exit(r3)     // Catch:{ all -> 0x013d }
            if (r2 == 0) goto L_0x014b
            r2.close()
        L_0x014b:
            if (r7 == 0) goto L_0x0150
            r7.close()
        L_0x0150:
            if (r0 == 0) goto L_0x0162
            r8.close()
            java.lang.String r3 = "YuvToJpegProcessor"
            java.lang.String r13 = "Closed after completion of last image processed."
            androidx.camera.core.Logger.d(r3, r13)
            if (r4 == 0) goto L_0x0162
            r3 = 0
            r4.set(r3)
        L_0x0162:
            goto L_0x0220
        L_0x0164:
            monitor-exit(r3)     // Catch:{ all -> 0x013d }
            throw r0
        L_0x0166:
            r0 = move-exception
            r3 = r13
            r4 = 0
            goto L_0x01a8
        L_0x016a:
            r0 = move-exception
            r2 = r7
            goto L_0x017d
        L_0x016d:
            r0 = move-exception
            r20 = r2
            r3 = r13
            r4 = 0
            goto L_0x0178
        L_0x0173:
            r0 = move-exception
            r20 = r2
            r4 = r3
            r3 = r13
        L_0x0178:
            goto L_0x01a8
        L_0x0179:
            r0 = move-exception
            r20 = r2
            r2 = r7
        L_0x017d:
            if (r9 == 0) goto L_0x01e4
            java.lang.String r3 = "YuvToJpegProcessor"
            java.lang.String r4 = "Failed to process YUV -> JPEG"
            androidx.camera.core.Logger.e(r3, r4, r0)     // Catch:{ all -> 0x01a4 }
            android.media.Image r3 = r8.dequeueInputImage()     // Catch:{ all -> 0x01a4 }
            r13 = r3
            android.media.Image$Plane[] r3 = r13.getPlanes()     // Catch:{ all -> 0x01a4 }
            r4 = 0
            r3 = r3[r4]     // Catch:{ all -> 0x01a0 }
            java.nio.ByteBuffer r3 = r3.getBuffer()     // Catch:{ all -> 0x01a0 }
            r3.rewind()     // Catch:{ all -> 0x01a0 }
            r3.limit(r4)     // Catch:{ all -> 0x01a0 }
            r8.queueInputImage(r13)     // Catch:{ all -> 0x01a0 }
            goto L_0x01e5
        L_0x01a0:
            r0 = move-exception
            r7 = r2
            r3 = r13
            goto L_0x01a8
        L_0x01a4:
            r0 = move-exception
            r4 = 0
            r7 = r2
            r3 = r13
        L_0x01a8:
            java.lang.Object r14 = r1.mLock
            monitor-enter(r14)
            if (r9 == 0) goto L_0x01be
            int r2 = r1.mProcessingImages     // Catch:{ all -> 0x01bc }
            int r13 = r2 + -1
            r1.mProcessingImages = r13     // Catch:{ all -> 0x01bc }
            if (r2 != 0) goto L_0x01be
            boolean r2 = r1.mClosed     // Catch:{ all -> 0x01bc }
            if (r2 == 0) goto L_0x01be
            r16 = 1
            goto L_0x01c0
        L_0x01bc:
            r0 = move-exception
            goto L_0x01e2
        L_0x01be:
            r16 = r4
        L_0x01c0:
            r2 = r16
            androidx.concurrent.futures.CallbackToFutureAdapter$Completer<java.lang.Void> r4 = r1.mCloseCompleter     // Catch:{ all -> 0x01bc }
            monitor-exit(r14)     // Catch:{ all -> 0x01bc }
            if (r3 == 0) goto L_0x01ca
            r3.close()
        L_0x01ca:
            if (r7 == 0) goto L_0x01cf
            r7.close()
        L_0x01cf:
            if (r2 == 0) goto L_0x01e1
            r8.close()
            java.lang.String r13 = "YuvToJpegProcessor"
            java.lang.String r14 = "Closed after completion of last image processed."
            androidx.camera.core.Logger.d(r13, r14)
            if (r4 == 0) goto L_0x01e1
            r13 = 0
            r4.set(r13)
        L_0x01e1:
            throw r0
        L_0x01e2:
            monitor-exit(r14)     // Catch:{ all -> 0x01bc }
            throw r0
        L_0x01e4:
            r4 = 0
        L_0x01e5:
            java.lang.Object r3 = r1.mLock
            monitor-enter(r3)
            if (r9 == 0) goto L_0x01fb
            int r0 = r1.mProcessingImages     // Catch:{ all -> 0x01f9 }
            int r7 = r0 + -1
            r1.mProcessingImages = r7     // Catch:{ all -> 0x01f9 }
            if (r0 != 0) goto L_0x01fb
            boolean r0 = r1.mClosed     // Catch:{ all -> 0x01f9 }
            if (r0 == 0) goto L_0x01fb
            r16 = 1
            goto L_0x01fd
        L_0x01f9:
            r0 = move-exception
            goto L_0x0221
        L_0x01fb:
            r16 = r4
        L_0x01fd:
            r0 = r16
            androidx.concurrent.futures.CallbackToFutureAdapter$Completer<java.lang.Void> r4 = r1.mCloseCompleter     // Catch:{ all -> 0x01f9 }
            monitor-exit(r3)     // Catch:{ all -> 0x01f9 }
            if (r13 == 0) goto L_0x0207
            r13.close()
        L_0x0207:
            if (r2 == 0) goto L_0x020c
            r2.close()
        L_0x020c:
            if (r0 == 0) goto L_0x021e
            r8.close()
            java.lang.String r3 = "YuvToJpegProcessor"
            java.lang.String r7 = "Closed after completion of last image processed."
            androidx.camera.core.Logger.d(r3, r7)
            if (r4 == 0) goto L_0x021e
            r3 = 0
            r4.set(r3)
        L_0x021e:
            r7 = r2
            r2 = r13
        L_0x0220:
            return
        L_0x0221:
            monitor-exit(r3)     // Catch:{ all -> 0x01f9 }
            throw r0
        L_0x0223:
            r0 = move-exception
            r20 = r2
        L_0x0226:
            monitor-exit(r7)     // Catch:{ all -> 0x0228 }
            throw r0
        L_0x0228:
            r0 = move-exception
            goto L_0x0226
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.internal.YuvToJpegProcessor.process(androidx.camera.core.impl.ImageProxyBundle):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002d, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002f, code lost:
        r0.set(null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() {
        /*
            r4 = this;
            r0 = 0
            java.lang.Object r1 = r4.mLock
            monitor-enter(r1)
            boolean r2 = r4.mClosed     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x000a
            monitor-exit(r1)     // Catch:{ all -> 0x0034 }
            return
        L_0x000a:
            r2 = 1
            r4.mClosed = r2     // Catch:{ all -> 0x0034 }
            int r2 = r4.mProcessingImages     // Catch:{ all -> 0x0034 }
            if (r2 != 0) goto L_0x0025
            android.media.ImageWriter r2 = r4.mImageWriter     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0025
            java.lang.String r2 = "YuvToJpegProcessor"
            java.lang.String r3 = "No processing in progress. Closing immediately."
            androidx.camera.core.Logger.d(r2, r3)     // Catch:{ all -> 0x0034 }
            android.media.ImageWriter r2 = r4.mImageWriter     // Catch:{ all -> 0x0034 }
            r2.close()     // Catch:{ all -> 0x0034 }
            androidx.concurrent.futures.CallbackToFutureAdapter$Completer<java.lang.Void> r2 = r4.mCloseCompleter     // Catch:{ all -> 0x0034 }
            r0 = r2
            goto L_0x002c
        L_0x0025:
            java.lang.String r2 = "YuvToJpegProcessor"
            java.lang.String r3 = "close() called while processing. Will close after completion."
            androidx.camera.core.Logger.d(r2, r3)     // Catch:{ all -> 0x0034 }
        L_0x002c:
            monitor-exit(r1)     // Catch:{ all -> 0x0034 }
            if (r0 == 0) goto L_0x0033
            r1 = 0
            r0.set(r1)
        L_0x0033:
            return
        L_0x0034:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0034 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.internal.YuvToJpegProcessor.close():void");
    }

    public ListenableFuture<Void> getCloseFuture() {
        ListenableFuture<Void> closeFuture;
        synchronized (this.mLock) {
            if (!this.mClosed || this.mProcessingImages != 0) {
                if (this.mCloseFuture == null) {
                    this.mCloseFuture = CallbackToFutureAdapter.getFuture(new YuvToJpegProcessor$$ExternalSyntheticLambda0(this));
                }
                closeFuture = Futures.nonCancellationPropagating(this.mCloseFuture);
            } else {
                closeFuture = Futures.immediateFuture(null);
            }
        }
        return closeFuture;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getCloseFuture$0$androidx-camera-core-internal-YuvToJpegProcessor  reason: not valid java name */
    public /* synthetic */ Object m179lambda$getCloseFuture$0$androidxcameracoreinternalYuvToJpegProcessor(CallbackToFutureAdapter.Completer completer) throws Exception {
        synchronized (this.mLock) {
            this.mCloseCompleter = completer;
        }
        return "YuvToJpegProcessor-close";
    }

    public void onResolutionUpdate(Size size) {
        synchronized (this.mLock) {
            this.mImageRect = new Rect(0, 0, size.getWidth(), size.getHeight());
        }
    }

    private static ExifData getExifData(ImageProxy imageProxy, int rotationDegrees) {
        ExifData.Builder builder = ExifData.builderForDevice();
        imageProxy.getImageInfo().populateExifData(builder);
        builder.setOrientationDegrees(rotationDegrees);
        return builder.setImageWidth(imageProxy.getWidth()).setImageHeight(imageProxy.getHeight()).build();
    }

    private static final class ByteBufferOutputStream extends OutputStream {
        private final ByteBuffer mByteBuffer;

        ByteBufferOutputStream(ByteBuffer buf) {
            this.mByteBuffer = buf;
        }

        public void write(int b) throws IOException {
            if (this.mByteBuffer.hasRemaining()) {
                this.mByteBuffer.put((byte) b);
                return;
            }
            throw new EOFException("Output ByteBuffer has no bytes remaining.");
        }

        public void write(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            } else if (len != 0) {
                if (this.mByteBuffer.remaining() >= len) {
                    this.mByteBuffer.put(b, off, len);
                    return;
                }
                throw new EOFException("Output ByteBuffer has insufficient bytes remaining.");
            }
        }
    }
}
