package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import androidx.camera.core.ImageAnalysis;
import androidx.concurrent.futures.CallbackToFutureAdapter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ImageAnalysisAbstractAnalyzer f$0;
    public final /* synthetic */ ImageProxy f$1;
    public final /* synthetic */ Matrix f$2;
    public final /* synthetic */ ImageProxy f$3;
    public final /* synthetic */ Rect f$4;
    public final /* synthetic */ ImageAnalysis.Analyzer f$5;
    public final /* synthetic */ CallbackToFutureAdapter.Completer f$6;

    public /* synthetic */ ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda1(ImageAnalysisAbstractAnalyzer imageAnalysisAbstractAnalyzer, ImageProxy imageProxy, Matrix matrix, ImageProxy imageProxy2, Rect rect, ImageAnalysis.Analyzer analyzer, CallbackToFutureAdapter.Completer completer) {
        this.f$0 = imageAnalysisAbstractAnalyzer;
        this.f$1 = imageProxy;
        this.f$2 = matrix;
        this.f$3 = imageProxy2;
        this.f$4 = rect;
        this.f$5 = analyzer;
        this.f$6 = completer;
    }

    public final void run() {
        this.f$0.m138lambda$analyzeImage$0$androidxcameracoreImageAnalysisAbstractAnalyzer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
