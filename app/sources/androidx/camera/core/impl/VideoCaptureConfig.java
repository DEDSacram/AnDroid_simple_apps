package androidx.camera.core.impl;

import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.Config;
import androidx.camera.core.internal.ThreadConfig;

public final class VideoCaptureConfig implements UseCaseConfig<VideoCapture>, ImageOutputConfig, ThreadConfig {
    public static final Config.Option<Integer> OPTION_AUDIO_BIT_RATE = Config.Option.create("camerax.core.videoCapture.audioBitRate", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_AUDIO_CHANNEL_COUNT = Config.Option.create("camerax.core.videoCapture.audioChannelCount", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_AUDIO_MIN_BUFFER_SIZE = Config.Option.create("camerax.core.videoCapture.audioMinBufferSize", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_AUDIO_SAMPLE_RATE = Config.Option.create("camerax.core.videoCapture.audioSampleRate", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_BIT_RATE = Config.Option.create("camerax.core.videoCapture.bitRate", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_INTRA_FRAME_INTERVAL = Config.Option.create("camerax.core.videoCapture.intraFrameInterval", Integer.TYPE);
    public static final Config.Option<Integer> OPTION_VIDEO_FRAME_RATE = Config.Option.create("camerax.core.videoCapture.recordingFrameRate", Integer.TYPE);
    private final OptionsBundle mConfig;

    public VideoCaptureConfig(OptionsBundle config) {
        this.mConfig = config;
    }

    public int getVideoFrameRate(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_VIDEO_FRAME_RATE, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getVideoFrameRate() {
        return ((Integer) retrieveOption(OPTION_VIDEO_FRAME_RATE)).intValue();
    }

    public int getBitRate(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_BIT_RATE, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getBitRate() {
        return ((Integer) retrieveOption(OPTION_BIT_RATE)).intValue();
    }

    public int getIFrameInterval(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_INTRA_FRAME_INTERVAL, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getIFrameInterval() {
        return ((Integer) retrieveOption(OPTION_INTRA_FRAME_INTERVAL)).intValue();
    }

    public int getAudioBitRate(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_AUDIO_BIT_RATE, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getAudioBitRate() {
        return ((Integer) retrieveOption(OPTION_AUDIO_BIT_RATE)).intValue();
    }

    public int getAudioSampleRate(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_AUDIO_SAMPLE_RATE, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getAudioSampleRate() {
        return ((Integer) retrieveOption(OPTION_AUDIO_SAMPLE_RATE)).intValue();
    }

    public int getAudioChannelCount(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_AUDIO_CHANNEL_COUNT, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getAudioChannelCount() {
        return ((Integer) retrieveOption(OPTION_AUDIO_CHANNEL_COUNT)).intValue();
    }

    public int getAudioMinBufferSize(int valueIfMissing) {
        return ((Integer) retrieveOption(OPTION_AUDIO_MIN_BUFFER_SIZE, Integer.valueOf(valueIfMissing))).intValue();
    }

    public int getAudioMinBufferSize() {
        return ((Integer) retrieveOption(OPTION_AUDIO_MIN_BUFFER_SIZE)).intValue();
    }

    public int getInputFormat() {
        return 34;
    }

    public Config getConfig() {
        return this.mConfig;
    }
}
