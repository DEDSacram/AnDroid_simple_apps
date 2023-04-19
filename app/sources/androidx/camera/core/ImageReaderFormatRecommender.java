package androidx.camera.core;

final class ImageReaderFormatRecommender {
    private ImageReaderFormatRecommender() {
    }

    static FormatCombo chooseCombo() {
        return FormatCombo.create(256, 35);
    }

    static abstract class FormatCombo {
        /* access modifiers changed from: package-private */
        public abstract int imageAnalysisFormat();

        /* access modifiers changed from: package-private */
        public abstract int imageCaptureFormat();

        FormatCombo() {
        }

        static FormatCombo create(int imageCaptureFormat, int imageAnalysisFormat) {
            return new AutoValue_ImageReaderFormatRecommender_FormatCombo(imageCaptureFormat, imageAnalysisFormat);
        }
    }
}
