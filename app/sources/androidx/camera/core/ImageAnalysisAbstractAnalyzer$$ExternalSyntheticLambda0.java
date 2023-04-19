package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import androidx.camera.core.ImageAnalysis;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import java.util.concurrent.Executor;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda0 implements CallbackToFutureAdapter.Resolver {
    public final /* synthetic */ ImageAnalysisAbstractAnalyzer f$0;
    public final /* synthetic */ Executor f$1;
    public final /* synthetic */ ImageProxy f$2;
    public final /* synthetic */ Matrix f$3;
    public final /* synthetic */ ImageProxy f$4;
    public final /* synthetic */ Rect f$5;
    public final /* synthetic */ ImageAnalysis.Analyzer f$6;

    public /* synthetic */ ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda0(ImageAnalysisAbstractAnalyzer imageAnalysisAbstractAnalyzer, Executor executor, ImageProxy imageProxy, Matrix matrix, ImageProxy imageProxy2, Rect rect, ImageAnalysis.Analyzer analyzer) {
        this.f$0 = imageAnalysisAbstractAnalyzer;
        this.f$1 = executor;
        this.f$2 = imageProxy;
        this.f$3 = matrix;
        this.f$4 = imageProxy2;
        this.f$5 = rect;
        this.f$6 = analyzer;
    }

    public final Object attachCompleter(CallbackToFutureAdapter.Completer completer) {
        return this.f$0.m139lambda$analyzeImage$1$androidxcameracoreImageAnalysisAbstractAnalyzer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, completer);
    }
}
