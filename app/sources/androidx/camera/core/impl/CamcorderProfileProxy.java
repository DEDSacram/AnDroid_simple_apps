package androidx.camera.core.impl;

import android.media.CamcorderProfile;

public abstract class CamcorderProfileProxy {
    public static int CODEC_PROFILE_NONE = -1;

    public abstract int getAudioBitRate();

    public abstract int getAudioChannels();

    public abstract int getAudioCodec();

    public abstract int getAudioSampleRate();

    public abstract int getDuration();

    public abstract int getFileFormat();

    public abstract int getQuality();

    public abstract int getVideoBitRate();

    public abstract int getVideoCodec();

    public abstract int getVideoFrameHeight();

    public abstract int getVideoFrameRate();

    public abstract int getVideoFrameWidth();

    public static CamcorderProfileProxy create(int duration, int quality, int fileFormat, int videoCodec, int videoBitRate, int videoFrameRate, int videoFrameWidth, int videoFrameHeight, int audioCodec, int audioBitRate, int audioSampleRate, int audioChannels) {
        return new AutoValue_CamcorderProfileProxy(duration, quality, fileFormat, videoCodec, videoBitRate, videoFrameRate, videoFrameWidth, videoFrameHeight, audioCodec, audioBitRate, audioSampleRate, audioChannels);
    }

    public static CamcorderProfileProxy fromCamcorderProfile(CamcorderProfile camcorderProfile) {
        return new AutoValue_CamcorderProfileProxy(camcorderProfile.duration, camcorderProfile.quality, camcorderProfile.fileFormat, camcorderProfile.videoCodec, camcorderProfile.videoBitRate, camcorderProfile.videoFrameRate, camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight, camcorderProfile.audioCodec, camcorderProfile.audioBitRate, camcorderProfile.audioSampleRate, camcorderProfile.audioChannels);
    }

    public String getVideoCodecMimeType() {
        switch (getVideoCodec()) {
            case 1:
                return "video/3gpp";
            case 2:
                return "video/avc";
            case 3:
                return "video/mp4v-es";
            case 4:
                return "video/x-vnd.on2.vp8";
            case 5:
                return "video/hevc";
            default:
                return null;
        }
    }

    public String getAudioCodecMimeType() {
        switch (getAudioCodec()) {
            case 1:
                return "audio/3gpp";
            case 2:
                return "audio/amr-wb";
            case 3:
            case 4:
            case 5:
                return "audio/mp4a-latm";
            case 6:
                return "audio/vorbis";
            case 7:
                return "audio/opus";
            default:
                return null;
        }
    }

    public int getRequiredAudioProfile() {
        switch (getAudioCodec()) {
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 39;
            default:
                return CODEC_PROFILE_NONE;
        }
    }
}
