package androidx.camera.camera2.internal;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Rational;
import android.util.Size;
import androidx.camera.camera2.internal.compat.CameraAccessExceptionCompat;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.camera.camera2.internal.compat.workaround.ExcludedSupportedSizesContainer;
import androidx.camera.camera2.internal.compat.workaround.ExtraSupportedSurfaceCombinationsContainer;
import androidx.camera.camera2.internal.compat.workaround.ResolutionCorrector;
import androidx.camera.camera2.internal.compat.workaround.TargetAspectRatio;
import androidx.camera.core.CameraUnavailableException;
import androidx.camera.core.Logger;
import androidx.camera.core.impl.AttachedSurfaceInfo;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.SurfaceCombination;
import androidx.camera.core.impl.SurfaceConfig;
import androidx.camera.core.impl.SurfaceSizeDefinition;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.utils.CameraOrientationUtil;
import androidx.camera.core.impl.utils.CompareSizesByArea;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class SupportedSurfaceCombination {
    private static final int ALIGN16 = 16;
    private static final Rational ASPECT_RATIO_16_9 = new Rational(16, 9);
    private static final Rational ASPECT_RATIO_3_4 = new Rational(3, 4);
    private static final Rational ASPECT_RATIO_4_3 = new Rational(4, 3);
    private static final Rational ASPECT_RATIO_9_16 = new Rational(9, 16);
    private static final Size DEFAULT_SIZE = new Size(640, 480);
    private static final Size QUALITY_1080P_SIZE = new Size(1920, 1080);
    private static final Size QUALITY_480P_SIZE = new Size(720, 480);
    private static final String TAG = "SupportedSurfaceCombination";
    private static final Size ZERO_SIZE = new Size(0, 0);
    private final CamcorderProfileHelper mCamcorderProfileHelper;
    private final String mCameraId;
    private final CameraCharacteristicsCompat mCharacteristics;
    private final DisplayInfoManager mDisplayInfoManager;
    private final Map<Integer, List<Size>> mExcludedSizeListCache = new HashMap();
    private final ExcludedSupportedSizesContainer mExcludedSupportedSizesContainer;
    private final ExtraSupportedSurfaceCombinationsContainer mExtraSupportedSurfaceCombinationsContainer;
    private final int mHardwareLevel;
    private boolean mIsBurstCaptureSupported = false;
    private boolean mIsRawSupported = false;
    private final boolean mIsSensorLandscapeResolution;
    private final Map<Integer, Size> mMaxSizeCache = new HashMap();
    private Map<Integer, Size[]> mOutputSizesCache = new HashMap();
    private final ResolutionCorrector mResolutionCorrector = new ResolutionCorrector();
    private final List<SurfaceCombination> mSurfaceCombinations = new ArrayList();
    private SurfaceSizeDefinition mSurfaceSizeDefinition;

    SupportedSurfaceCombination(Context context, String cameraId, CameraManagerCompat cameraManagerCompat, CamcorderProfileHelper camcorderProfileHelper) throws CameraUnavailableException {
        int i;
        String str = (String) Preconditions.checkNotNull(cameraId);
        this.mCameraId = str;
        this.mCamcorderProfileHelper = (CamcorderProfileHelper) Preconditions.checkNotNull(camcorderProfileHelper);
        this.mExcludedSupportedSizesContainer = new ExcludedSupportedSizesContainer(cameraId);
        this.mExtraSupportedSurfaceCombinationsContainer = new ExtraSupportedSurfaceCombinationsContainer();
        this.mDisplayInfoManager = DisplayInfoManager.getInstance(context);
        try {
            CameraCharacteristicsCompat cameraCharacteristicsCompat = cameraManagerCompat.getCameraCharacteristicsCompat(str);
            this.mCharacteristics = cameraCharacteristicsCompat;
            Integer keyValue = (Integer) cameraCharacteristicsCompat.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if (keyValue != null) {
                i = keyValue.intValue();
            } else {
                i = 2;
            }
            this.mHardwareLevel = i;
            this.mIsSensorLandscapeResolution = isSensorLandscapeResolution();
            generateSupportedCombinationList();
            generateSurfaceSizeDefinition();
            checkCustomization();
        } catch (CameraAccessExceptionCompat e) {
            throw CameraUnavailableExceptionHelper.createFrom(e);
        }
    }

    /* access modifiers changed from: package-private */
    public String getCameraId() {
        return this.mCameraId;
    }

    /* access modifiers changed from: package-private */
    public boolean isRawSupported() {
        return this.mIsRawSupported;
    }

    /* access modifiers changed from: package-private */
    public boolean isBurstCaptureSupported() {
        return this.mIsBurstCaptureSupported;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0007 A[LOOP:0: B:1:0x0007->B:4:0x0017, LOOP_START, PHI: r0 
      PHI: (r0v1 'isSupported' boolean) = (r0v0 'isSupported' boolean), (r0v3 'isSupported' boolean) binds: [B:0:0x0000, B:4:0x0017] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkSupported(java.util.List<androidx.camera.core.impl.SurfaceConfig> r4) {
        /*
            r3 = this;
            r0 = 0
            java.util.List<androidx.camera.core.impl.SurfaceCombination> r1 = r3.mSurfaceCombinations
            java.util.Iterator r1 = r1.iterator()
        L_0x0007:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x001b
            java.lang.Object r2 = r1.next()
            androidx.camera.core.impl.SurfaceCombination r2 = (androidx.camera.core.impl.SurfaceCombination) r2
            boolean r0 = r2.isSupported(r4)
            if (r0 == 0) goto L_0x001a
            goto L_0x001b
        L_0x001a:
            goto L_0x0007
        L_0x001b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.SupportedSurfaceCombination.checkSupported(java.util.List):boolean");
    }

    /* access modifiers changed from: package-private */
    public SurfaceConfig transformSurfaceConfig(int imageFormat, Size size) {
        SurfaceConfig.ConfigType configType = getConfigType(imageFormat);
        SurfaceConfig.ConfigSize configSize = SurfaceConfig.ConfigSize.NOT_SUPPORT;
        Size maxSize = fetchMaxSize(imageFormat);
        if (size.getWidth() * size.getHeight() <= this.mSurfaceSizeDefinition.getAnalysisSize().getWidth() * this.mSurfaceSizeDefinition.getAnalysisSize().getHeight()) {
            configSize = SurfaceConfig.ConfigSize.ANALYSIS;
        } else if (size.getWidth() * size.getHeight() <= this.mSurfaceSizeDefinition.getPreviewSize().getWidth() * this.mSurfaceSizeDefinition.getPreviewSize().getHeight()) {
            configSize = SurfaceConfig.ConfigSize.PREVIEW;
        } else if (size.getWidth() * size.getHeight() <= this.mSurfaceSizeDefinition.getRecordSize().getWidth() * this.mSurfaceSizeDefinition.getRecordSize().getHeight()) {
            configSize = SurfaceConfig.ConfigSize.RECORD;
        } else if (size.getWidth() * size.getHeight() <= maxSize.getWidth() * maxSize.getHeight()) {
            configSize = SurfaceConfig.ConfigSize.MAXIMUM;
        }
        return SurfaceConfig.create(configType, configSize);
    }

    /* access modifiers changed from: package-private */
    public Map<UseCaseConfig<?>, Size> getSuggestedResolutions(List<AttachedSurfaceInfo> existingSurfaces, List<UseCaseConfig<?>> newUseCaseConfigs) {
        List<AttachedSurfaceInfo> list = existingSurfaces;
        List<UseCaseConfig<?>> list2 = newUseCaseConfigs;
        refreshPreviewSize();
        List<SurfaceConfig> surfaceConfigs = new ArrayList<>();
        for (AttachedSurfaceInfo scc : existingSurfaces) {
            surfaceConfigs.add(scc.getSurfaceConfig());
        }
        for (UseCaseConfig<?> useCaseConfig : newUseCaseConfigs) {
            surfaceConfigs.add(transformSurfaceConfig(useCaseConfig.getInputFormat(), new Size(640, 480)));
        }
        if (checkSupported(surfaceConfigs)) {
            List<Integer> useCasesPriorityOrder = getUseCasesPriorityOrder(list2);
            List<List<Size>> supportedOutputSizesList = new ArrayList<>();
            for (Integer index : useCasesPriorityOrder) {
                supportedOutputSizesList.add(getSupportedOutputSizes(list2.get(index.intValue())));
            }
            Map<UseCaseConfig<?>, Size> suggestedResolutionsMap = null;
            Iterator<List<Size>> it = getAllPossibleSizeArrangements(supportedOutputSizesList).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                List<Size> possibleSizeList = it.next();
                List<SurfaceConfig> surfaceConfigList = new ArrayList<>();
                for (AttachedSurfaceInfo sc : existingSurfaces) {
                    surfaceConfigList.add(sc.getSurfaceConfig());
                }
                int i = 0;
                while (i < possibleSizeList.size()) {
                    List<SurfaceConfig> surfaceConfigs2 = surfaceConfigs;
                    surfaceConfigList.add(transformSurfaceConfig(list2.get(useCasesPriorityOrder.get(i).intValue()).getInputFormat(), possibleSizeList.get(i)));
                    i++;
                    surfaceConfigs = surfaceConfigs2;
                }
                List<SurfaceConfig> surfaceConfigs3 = surfaceConfigs;
                if (checkSupported(surfaceConfigList)) {
                    suggestedResolutionsMap = new HashMap<>();
                    for (UseCaseConfig<?> useCaseConfig2 : newUseCaseConfigs) {
                        suggestedResolutionsMap.put(useCaseConfig2, possibleSizeList.get(useCasesPriorityOrder.indexOf(Integer.valueOf(list2.indexOf(useCaseConfig2)))));
                    }
                } else {
                    surfaceConfigs = surfaceConfigs3;
                }
            }
            if (suggestedResolutionsMap != null) {
                return suggestedResolutionsMap;
            }
            throw new IllegalArgumentException("No supported surface combination is found for camera device - Id : " + this.mCameraId + " and Hardware level: " + this.mHardwareLevel + ". May be the specified resolution is too large and not supported. Existing surfaces: " + list + " New configs: " + list2);
        }
        throw new IllegalArgumentException("No supported surface combination is found for camera device - Id : " + this.mCameraId + ".  May be attempting to bind too many use cases. Existing surfaces: " + list + " New configs: " + list2);
    }

    private Rational getTargetAspectRatio(ImageOutputConfig imageOutputConfig) {
        Rational outputRatio;
        Rational outputRatio2;
        switch (new TargetAspectRatio().get(this.mCameraId, this.mCharacteristics)) {
            case 0:
                return this.mIsSensorLandscapeResolution ? ASPECT_RATIO_4_3 : ASPECT_RATIO_3_4;
            case 1:
                return this.mIsSensorLandscapeResolution ? ASPECT_RATIO_16_9 : ASPECT_RATIO_9_16;
            case 2:
                Size maxJpegSize = fetchMaxSize(256);
                return new Rational(maxJpegSize.getWidth(), maxJpegSize.getHeight());
            case 3:
                Size targetSize = getTargetSize(imageOutputConfig);
                if (imageOutputConfig.hasTargetAspectRatio()) {
                    int aspectRatio = imageOutputConfig.getTargetAspectRatio();
                    switch (aspectRatio) {
                        case 0:
                            if (this.mIsSensorLandscapeResolution) {
                                outputRatio = ASPECT_RATIO_4_3;
                            } else {
                                outputRatio = ASPECT_RATIO_3_4;
                            }
                            return outputRatio;
                        case 1:
                            if (this.mIsSensorLandscapeResolution) {
                                outputRatio2 = ASPECT_RATIO_16_9;
                            } else {
                                outputRatio2 = ASPECT_RATIO_9_16;
                            }
                            return outputRatio2;
                        default:
                            Logger.e(TAG, "Undefined target aspect ratio: " + aspectRatio);
                            return null;
                    }
                } else if (targetSize != null) {
                    return new Rational(targetSize.getWidth(), targetSize.getHeight());
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    /* access modifiers changed from: package-private */
    public SurfaceSizeDefinition getSurfaceSizeDefinition() {
        return this.mSurfaceSizeDefinition;
    }

    private Size fetchMaxSize(int imageFormat) {
        Size size = this.mMaxSizeCache.get(Integer.valueOf(imageFormat));
        if (size != null) {
            return size;
        }
        Size maxSize = getMaxOutputSizeByFormat(imageFormat);
        this.mMaxSizeCache.put(Integer.valueOf(imageFormat), maxSize);
        return maxSize;
    }

    private List<Integer> getUseCasesPriorityOrder(List<UseCaseConfig<?>> newUseCaseConfigs) {
        List<Integer> priorityOrder = new ArrayList<>();
        List<Integer> priorityValueList = new ArrayList<>();
        for (UseCaseConfig<?> config : newUseCaseConfigs) {
            int priority = config.getSurfaceOccupancyPriority(0);
            if (!priorityValueList.contains(Integer.valueOf(priority))) {
                priorityValueList.add(Integer.valueOf(priority));
            }
        }
        Collections.sort(priorityValueList);
        Collections.reverse(priorityValueList);
        for (Integer intValue : priorityValueList) {
            int priorityValue = intValue.intValue();
            for (UseCaseConfig<?> config2 : newUseCaseConfigs) {
                if (priorityValue == config2.getSurfaceOccupancyPriority(0)) {
                    priorityOrder.add(Integer.valueOf(newUseCaseConfigs.indexOf(config2)));
                }
            }
        }
        return priorityOrder;
    }

    /* access modifiers changed from: package-private */
    public List<Size> getSupportedOutputSizes(UseCaseConfig<?> config) {
        int imageFormat = config.getInputFormat();
        ImageOutputConfig imageOutputConfig = (ImageOutputConfig) config;
        Size[] outputSizes = getCustomizedSupportSizesFromConfig(imageFormat, imageOutputConfig);
        if (outputSizes == null) {
            outputSizes = getAllOutputSizesByFormat(imageFormat);
        }
        List<Size> outputSizeCandidates = new ArrayList<>();
        Size maxSize = imageOutputConfig.getMaxResolution((Size) null);
        Size maxOutputSizeByFormat = getMaxOutputSizeByFormat(imageFormat);
        if (maxSize == null || getArea(maxOutputSizeByFormat) < getArea(maxSize)) {
            maxSize = maxOutputSizeByFormat;
        }
        Arrays.sort(outputSizes, new CompareSizesByArea(true));
        Size targetSize = getTargetSize(imageOutputConfig);
        Size minSize = DEFAULT_SIZE;
        int defaultSizeArea = getArea(DEFAULT_SIZE);
        if (getArea(maxSize) < defaultSizeArea) {
            minSize = ZERO_SIZE;
        } else if (targetSize != null && getArea(targetSize) < defaultSizeArea) {
            minSize = targetSize;
        }
        for (Size outputSize : outputSizes) {
            if (getArea(outputSize) <= getArea(maxSize) && getArea(outputSize) >= getArea(minSize) && !outputSizeCandidates.contains(outputSize)) {
                outputSizeCandidates.add(outputSize);
            }
        }
        if (!outputSizeCandidates.isEmpty()) {
            Rational aspectRatio = getTargetAspectRatio(imageOutputConfig);
            Size targetSize2 = targetSize == null ? imageOutputConfig.getDefaultResolution((Size) null) : targetSize;
            List<Size> supportedResolutions = new ArrayList<>();
            new HashMap();
            if (aspectRatio == null) {
                supportedResolutions.addAll(outputSizeCandidates);
                if (targetSize2 != null) {
                    removeSupportedSizesByTargetSize(supportedResolutions, targetSize2);
                }
                ImageOutputConfig imageOutputConfig2 = imageOutputConfig;
            } else {
                Map<Rational, List<Size>> aspectRatioSizeListMap = groupSizesByAspectRatio(outputSizeCandidates);
                if (targetSize2 != null) {
                    for (Rational key : aspectRatioSizeListMap.keySet()) {
                        removeSupportedSizesByTargetSize(aspectRatioSizeListMap.get(key), targetSize2);
                        imageOutputConfig = imageOutputConfig;
                    }
                }
                List<Rational> arrayList = new ArrayList<>(aspectRatioSizeListMap.keySet());
                Collections.sort(arrayList, new CompareAspectRatiosByDistanceToTargetRatio(aspectRatio));
                for (Rational rational : arrayList) {
                    for (Size size : aspectRatioSizeListMap.get(rational)) {
                        List<Rational> aspectRatios = arrayList;
                        if (!supportedResolutions.contains(size)) {
                            supportedResolutions.add(size);
                        }
                        arrayList = aspectRatios;
                    }
                    List<Rational> aspectRatios2 = arrayList;
                }
                List<Rational> aspectRatios3 = arrayList;
            }
            return this.mResolutionCorrector.insertOrPrioritize(getConfigType(config.getInputFormat()), supportedResolutions);
        }
        throw new IllegalArgumentException("Can not get supported output size under supported maximum for the format: " + imageFormat);
    }

    private SurfaceConfig.ConfigType getConfigType(int imageFormat) {
        if (imageFormat == 35) {
            return SurfaceConfig.ConfigType.YUV;
        }
        if (imageFormat == 256) {
            return SurfaceConfig.ConfigType.JPEG;
        }
        if (imageFormat == 32) {
            return SurfaceConfig.ConfigType.RAW;
        }
        return SurfaceConfig.ConfigType.PRIV;
    }

    private Size getTargetSize(ImageOutputConfig imageOutputConfig) {
        return flipSizeByRotation(imageOutputConfig.getTargetResolution((Size) null), imageOutputConfig.getTargetRotation(0));
    }

    private Size flipSizeByRotation(Size size, int targetRotation) {
        Size outputSize = size;
        if (size == null || !isRotationNeeded(targetRotation)) {
            return outputSize;
        }
        return new Size(size.getHeight(), size.getWidth());
    }

    private boolean isRotationNeeded(int targetRotation) {
        Integer sensorOrientation = (Integer) this.mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Preconditions.checkNotNull(sensorOrientation, "Camera HAL in bad state, unable to retrieve the SENSOR_ORIENTATION");
        int relativeRotationDegrees = CameraOrientationUtil.surfaceRotationToDegrees(targetRotation);
        Integer lensFacing = (Integer) this.mCharacteristics.get(CameraCharacteristics.LENS_FACING);
        Preconditions.checkNotNull(lensFacing, "Camera HAL in bad state, unable to retrieve the LENS_FACING");
        int sensorRotationDegrees = CameraOrientationUtil.getRelativeImageRotation(relativeRotationDegrees, sensorOrientation.intValue(), 1 == lensFacing.intValue());
        if (sensorRotationDegrees == 90 || sensorRotationDegrees == 270) {
            return true;
        }
        return false;
    }

    private boolean isSensorLandscapeResolution() {
        Size pixelArraySize = (Size) this.mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        return pixelArraySize == null || pixelArraySize.getWidth() >= pixelArraySize.getHeight();
    }

    static boolean hasMatchingAspectRatio(Size resolution, Rational aspectRatio) {
        if (aspectRatio == null) {
            return false;
        }
        if (aspectRatio.equals(new Rational(resolution.getWidth(), resolution.getHeight()))) {
            return true;
        }
        if (getArea(resolution) >= getArea(DEFAULT_SIZE)) {
            return isPossibleMod16FromAspectRatio(resolution, aspectRatio);
        }
        return false;
    }

    private static boolean isPossibleMod16FromAspectRatio(Size resolution, Rational aspectRatio) {
        int width = resolution.getWidth();
        int height = resolution.getHeight();
        Rational invAspectRatio = new Rational(aspectRatio.getDenominator(), aspectRatio.getNumerator());
        if (width % 16 == 0 && height % 16 == 0) {
            if (ratioIntersectsMod16Segment(Math.max(0, height - 16), width, aspectRatio) || ratioIntersectsMod16Segment(Math.max(0, width - 16), height, invAspectRatio)) {
                return true;
            }
            return false;
        } else if (width % 16 == 0) {
            return ratioIntersectsMod16Segment(height, width, aspectRatio);
        } else {
            if (height % 16 == 0) {
                return ratioIntersectsMod16Segment(width, height, invAspectRatio);
            }
            return false;
        }
    }

    private static int getArea(Size size) {
        return size.getWidth() * size.getHeight();
    }

    private static boolean ratioIntersectsMod16Segment(int height, int mod16Width, Rational aspectRatio) {
        Preconditions.checkArgument(mod16Width % 16 == 0);
        double aspectRatioWidth = ((double) (aspectRatio.getNumerator() * height)) / ((double) aspectRatio.getDenominator());
        if (aspectRatioWidth <= ((double) Math.max(0, mod16Width - 16)) || aspectRatioWidth >= ((double) (mod16Width + 16))) {
            return false;
        }
        return true;
    }

    private Map<Rational, List<Size>> groupSizesByAspectRatio(List<Size> sizes) {
        Map<Rational, List<Size>> aspectRatioSizeListMap = new HashMap<>();
        aspectRatioSizeListMap.put(ASPECT_RATIO_4_3, new ArrayList());
        aspectRatioSizeListMap.put(ASPECT_RATIO_16_9, new ArrayList());
        for (Size outputSize : sizes) {
            Rational matchedKey = null;
            for (Rational key : aspectRatioSizeListMap.keySet()) {
                if (hasMatchingAspectRatio(outputSize, key)) {
                    matchedKey = key;
                    List<Size> sizeList = aspectRatioSizeListMap.get(matchedKey);
                    if (!sizeList.contains(outputSize)) {
                        sizeList.add(outputSize);
                    }
                }
            }
            if (matchedKey == null) {
                aspectRatioSizeListMap.put(new Rational(outputSize.getWidth(), outputSize.getHeight()), new ArrayList(Collections.singleton(outputSize)));
            }
        }
        return aspectRatioSizeListMap;
    }

    private void removeSupportedSizesByTargetSize(List<Size> supportedSizesList, Size targetSize) {
        if (supportedSizesList != null && !supportedSizesList.isEmpty()) {
            int indexBigEnough = -1;
            List<Size> removeSizes = new ArrayList<>();
            for (int i = 0; i < supportedSizesList.size(); i++) {
                Size outputSize = supportedSizesList.get(i);
                if (outputSize.getWidth() < targetSize.getWidth() || outputSize.getHeight() < targetSize.getHeight()) {
                    break;
                }
                if (indexBigEnough >= 0) {
                    removeSizes.add(supportedSizesList.get(indexBigEnough));
                }
                indexBigEnough = i;
            }
            supportedSizesList.removeAll(removeSizes);
        }
    }

    private List<List<Size>> getAllPossibleSizeArrangements(List<List<Size>> supportedOutputSizesList) {
        int totalArrangementsCount = 1;
        for (List<Size> supportedOutputSizes : supportedOutputSizesList) {
            totalArrangementsCount *= supportedOutputSizes.size();
        }
        if (totalArrangementsCount != 0) {
            List<List<Size>> allPossibleSizeArrangements = new ArrayList<>();
            for (int i = 0; i < totalArrangementsCount; i++) {
                allPossibleSizeArrangements.add(new ArrayList<>());
            }
            int currentRunCount = totalArrangementsCount;
            int nextRunCount = currentRunCount / supportedOutputSizesList.get(0).size();
            for (int currentIndex = 0; currentIndex < supportedOutputSizesList.size(); currentIndex++) {
                List<Size> supportedOutputSizes2 = supportedOutputSizesList.get(currentIndex);
                for (int i2 = 0; i2 < totalArrangementsCount; i2++) {
                    allPossibleSizeArrangements.get(i2).add(supportedOutputSizes2.get((i2 % currentRunCount) / nextRunCount));
                }
                if (currentIndex < supportedOutputSizesList.size() - 1) {
                    currentRunCount = nextRunCount;
                    nextRunCount = currentRunCount / supportedOutputSizesList.get(currentIndex + 1).size();
                }
            }
            return allPossibleSizeArrangements;
        }
        throw new IllegalArgumentException("Failed to find supported resolutions.");
    }

    private Size[] excludeProblematicSizes(Size[] outputSizes, int imageFormat) {
        List<Size> excludedSizes = fetchExcludedSizes(imageFormat);
        List<Size> resultSizesList = new ArrayList<>(Arrays.asList(outputSizes));
        resultSizesList.removeAll(excludedSizes);
        return (Size[]) resultSizesList.toArray(new Size[0]);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: android.util.Size[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.util.Size[] getCustomizedSupportSizesFromConfig(int r6, androidx.camera.core.impl.ImageOutputConfig r7) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            java.util.List r1 = r7.getSupportedResolutions(r1)
            if (r1 == 0) goto L_0x002a
            java.util.Iterator r2 = r1.iterator()
        L_0x000d:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x002a
            java.lang.Object r3 = r2.next()
            android.util.Pair r3 = (android.util.Pair) r3
            java.lang.Object r4 = r3.first
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            if (r4 != r6) goto L_0x0029
            java.lang.Object r2 = r3.second
            r0 = r2
            android.util.Size[] r0 = (android.util.Size[]) r0
            goto L_0x002a
        L_0x0029:
            goto L_0x000d
        L_0x002a:
            if (r0 == 0) goto L_0x0039
            android.util.Size[] r0 = r5.excludeProblematicSizes(r0, r6)
            androidx.camera.core.impl.utils.CompareSizesByArea r2 = new androidx.camera.core.impl.utils.CompareSizesByArea
            r3 = 1
            r2.<init>(r3)
            java.util.Arrays.sort(r0, r2)
        L_0x0039:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.camera2.internal.SupportedSurfaceCombination.getCustomizedSupportSizesFromConfig(int, androidx.camera.core.impl.ImageOutputConfig):android.util.Size[]");
    }

    private Size[] getAllOutputSizesByFormat(int imageFormat) {
        Size[] outputs = this.mOutputSizesCache.get(Integer.valueOf(imageFormat));
        if (outputs != null) {
            return outputs;
        }
        Size[] outputs2 = doGetAllOutputSizesByFormat(imageFormat);
        this.mOutputSizesCache.put(Integer.valueOf(imageFormat), outputs2);
        return outputs2;
    }

    private Size[] doGetAllOutputSizesByFormat(int imageFormat) {
        Size[] outputSizes;
        StreamConfigurationMap map = (StreamConfigurationMap) this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map != null) {
            if (Build.VERSION.SDK_INT >= 23 || imageFormat != 34) {
                outputSizes = map.getOutputSizes(imageFormat);
            } else {
                outputSizes = map.getOutputSizes(SurfaceTexture.class);
            }
            if (outputSizes != null) {
                Size[] outputSizes2 = excludeProblematicSizes(outputSizes, imageFormat);
                Arrays.sort(outputSizes2, new CompareSizesByArea(true));
                return outputSizes2;
            }
            throw new IllegalArgumentException("Can not get supported output size for the format: " + imageFormat);
        }
        throw new IllegalArgumentException("Can not retrieve SCALER_STREAM_CONFIGURATION_MAP");
    }

    /* access modifiers changed from: package-private */
    public Size getMaxOutputSizeByFormat(int imageFormat) {
        return (Size) Collections.max(Arrays.asList(getAllOutputSizesByFormat(imageFormat)), new CompareSizesByArea());
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getLegacySupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination2);
        SurfaceCombination surfaceCombination3 = new SurfaceCombination();
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination3);
        SurfaceCombination surfaceCombination4 = new SurfaceCombination();
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination4);
        SurfaceCombination surfaceCombination5 = new SurfaceCombination();
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination5);
        SurfaceCombination surfaceCombination6 = new SurfaceCombination();
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        combinationList.add(surfaceCombination6);
        SurfaceCombination surfaceCombination7 = new SurfaceCombination();
        surfaceCombination7.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination7.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        combinationList.add(surfaceCombination7);
        SurfaceCombination surfaceCombination8 = new SurfaceCombination();
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination8);
        return combinationList;
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getLimitedSupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.RECORD));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.RECORD));
        combinationList.add(surfaceCombination2);
        SurfaceCombination surfaceCombination3 = new SurfaceCombination();
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.RECORD));
        combinationList.add(surfaceCombination3);
        SurfaceCombination surfaceCombination4 = new SurfaceCombination();
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.RECORD));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.RECORD));
        combinationList.add(surfaceCombination4);
        SurfaceCombination surfaceCombination5 = new SurfaceCombination();
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.RECORD));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.RECORD));
        combinationList.add(surfaceCombination5);
        SurfaceCombination surfaceCombination6 = new SurfaceCombination();
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination6);
        return combinationList;
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getFullSupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination2);
        SurfaceCombination surfaceCombination3 = new SurfaceCombination();
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination3);
        SurfaceCombination surfaceCombination4 = new SurfaceCombination();
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination4);
        SurfaceCombination surfaceCombination5 = new SurfaceCombination();
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.ANALYSIS));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination5);
        SurfaceCombination surfaceCombination6 = new SurfaceCombination();
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.ANALYSIS));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination6);
        return combinationList;
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getRAWSupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination2);
        SurfaceCombination surfaceCombination3 = new SurfaceCombination();
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination3);
        SurfaceCombination surfaceCombination4 = new SurfaceCombination();
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination4.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination4);
        SurfaceCombination surfaceCombination5 = new SurfaceCombination();
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination5.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination5);
        SurfaceCombination surfaceCombination6 = new SurfaceCombination();
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination6.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination6);
        SurfaceCombination surfaceCombination7 = new SurfaceCombination();
        surfaceCombination7.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination7.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        surfaceCombination7.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination7);
        SurfaceCombination surfaceCombination8 = new SurfaceCombination();
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        surfaceCombination8.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination8);
        return combinationList;
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getBurstSupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination2);
        SurfaceCombination surfaceCombination3 = new SurfaceCombination();
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination3.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination3);
        return combinationList;
    }

    /* access modifiers changed from: package-private */
    public List<SurfaceCombination> getLevel3SupportedCombinationList() {
        List<SurfaceCombination> combinationList = new ArrayList<>();
        SurfaceCombination surfaceCombination1 = new SurfaceCombination();
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.ANALYSIS));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.YUV, SurfaceConfig.ConfigSize.MAXIMUM));
        surfaceCombination1.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination1);
        SurfaceCombination surfaceCombination2 = new SurfaceCombination();
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.PREVIEW));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.PRIV, SurfaceConfig.ConfigSize.ANALYSIS));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.JPEG, SurfaceConfig.ConfigSize.MAXIMUM));
        surfaceCombination2.addSurfaceConfig(SurfaceConfig.create(SurfaceConfig.ConfigType.RAW, SurfaceConfig.ConfigSize.MAXIMUM));
        combinationList.add(surfaceCombination2);
        return combinationList;
    }

    private void generateSupportedCombinationList() {
        this.mSurfaceCombinations.addAll(getLegacySupportedCombinationList());
        int i = this.mHardwareLevel;
        if (i == 0 || i == 1 || i == 3) {
            this.mSurfaceCombinations.addAll(getLimitedSupportedCombinationList());
        }
        int i2 = this.mHardwareLevel;
        if (i2 == 1 || i2 == 3) {
            this.mSurfaceCombinations.addAll(getFullSupportedCombinationList());
        }
        int[] availableCapabilities = (int[]) this.mCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        if (availableCapabilities != null) {
            for (int capability : availableCapabilities) {
                if (capability == 3) {
                    this.mIsRawSupported = true;
                } else if (capability == 6) {
                    this.mIsBurstCaptureSupported = true;
                }
            }
        }
        if (this.mIsRawSupported) {
            this.mSurfaceCombinations.addAll(getRAWSupportedCombinationList());
        }
        if (this.mIsBurstCaptureSupported && this.mHardwareLevel == 0) {
            this.mSurfaceCombinations.addAll(getBurstSupportedCombinationList());
        }
        if (this.mHardwareLevel == 3) {
            this.mSurfaceCombinations.addAll(getLevel3SupportedCombinationList());
        }
        this.mSurfaceCombinations.addAll(this.mExtraSupportedSurfaceCombinationsContainer.get(this.mCameraId, this.mHardwareLevel));
    }

    private void checkCustomization() {
    }

    private void generateSurfaceSizeDefinition() {
        this.mSurfaceSizeDefinition = SurfaceSizeDefinition.create(new Size(640, 480), this.mDisplayInfoManager.getPreviewSize(), getRecordSize());
    }

    private void refreshPreviewSize() {
        this.mDisplayInfoManager.refresh();
        if (this.mSurfaceSizeDefinition == null) {
            generateSurfaceSizeDefinition();
            return;
        }
        this.mSurfaceSizeDefinition = SurfaceSizeDefinition.create(this.mSurfaceSizeDefinition.getAnalysisSize(), this.mDisplayInfoManager.getPreviewSize(), this.mSurfaceSizeDefinition.getRecordSize());
    }

    private Size getRecordSize() {
        try {
            int cameraId = Integer.parseInt(this.mCameraId);
            CamcorderProfile profile = null;
            if (this.mCamcorderProfileHelper.hasProfile(cameraId, 1)) {
                profile = this.mCamcorderProfileHelper.get(cameraId, 1);
            }
            if (profile != null) {
                return new Size(profile.videoFrameWidth, profile.videoFrameHeight);
            }
            return getRecordSizeByHasProfile(cameraId);
        } catch (NumberFormatException e) {
            return getRecordSizeFromStreamConfigurationMap();
        }
    }

    private Size getRecordSizeFromStreamConfigurationMap() {
        StreamConfigurationMap map = (StreamConfigurationMap) this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (map != null) {
            Size[] videoSizeArr = map.getOutputSizes(MediaRecorder.class);
            if (videoSizeArr == null) {
                return QUALITY_480P_SIZE;
            }
            Arrays.sort(videoSizeArr, new CompareSizesByArea(true));
            for (Size size : videoSizeArr) {
                int width = size.getWidth();
                Size size2 = QUALITY_1080P_SIZE;
                if (width <= size2.getWidth() && size.getHeight() <= size2.getHeight()) {
                    return size;
                }
            }
            return QUALITY_480P_SIZE;
        }
        throw new IllegalArgumentException("Can not retrieve SCALER_STREAM_CONFIGURATION_MAP");
    }

    private Size getRecordSizeByHasProfile(int cameraId) {
        Size recordSize = QUALITY_480P_SIZE;
        CamcorderProfile profile = null;
        if (this.mCamcorderProfileHelper.hasProfile(cameraId, 10)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 10);
        } else if (this.mCamcorderProfileHelper.hasProfile(cameraId, 8)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 8);
        } else if (this.mCamcorderProfileHelper.hasProfile(cameraId, 12)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 12);
        } else if (this.mCamcorderProfileHelper.hasProfile(cameraId, 6)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 6);
        } else if (this.mCamcorderProfileHelper.hasProfile(cameraId, 5)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 5);
        } else if (this.mCamcorderProfileHelper.hasProfile(cameraId, 4)) {
            profile = this.mCamcorderProfileHelper.get(cameraId, 4);
        }
        if (profile != null) {
            return new Size(profile.videoFrameWidth, profile.videoFrameHeight);
        }
        return recordSize;
    }

    private List<Size> fetchExcludedSizes(int imageFormat) {
        List<Size> excludedSizes = this.mExcludedSizeListCache.get(Integer.valueOf(imageFormat));
        if (excludedSizes != null) {
            return excludedSizes;
        }
        List<Size> excludedSizes2 = this.mExcludedSupportedSizesContainer.get(imageFormat);
        this.mExcludedSizeListCache.put(Integer.valueOf(imageFormat), excludedSizes2);
        return excludedSizes2;
    }

    static final class CompareAspectRatiosByDistanceToTargetRatio implements Comparator<Rational> {
        private Rational mTargetRatio;

        CompareAspectRatiosByDistanceToTargetRatio(Rational targetRatio) {
            this.mTargetRatio = targetRatio;
        }

        public int compare(Rational lhs, Rational rhs) {
            if (lhs.equals(rhs)) {
                return 0;
            }
            return (int) Math.signum(Float.valueOf(Math.abs(lhs.floatValue() - this.mTargetRatio.floatValue())).floatValue() - Float.valueOf(Math.abs(rhs.floatValue() - this.mTargetRatio.floatValue())).floatValue());
        }
    }
}
