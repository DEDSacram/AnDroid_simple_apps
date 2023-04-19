package androidx.camera.core.impl;

import androidx.camera.core.Logger;
import androidx.camera.core.impl.SessionConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class UseCaseAttachState {
    private static final String TAG = "UseCaseAttachState";
    private final Map<String, UseCaseAttachInfo> mAttachedUseCasesToInfoMap = new LinkedHashMap();
    private final String mCameraId;

    private interface AttachStateFilter {
        boolean filter(UseCaseAttachInfo useCaseAttachInfo);
    }

    public UseCaseAttachState(String cameraId) {
        this.mCameraId = cameraId;
    }

    public void setUseCaseActive(String useCaseId, SessionConfig sessionConfig, UseCaseConfig<?> useCaseConfig) {
        getOrCreateUseCaseAttachInfo(useCaseId, sessionConfig, useCaseConfig).setActive(true);
    }

    public void setUseCaseInactive(String useCaseId) {
        if (this.mAttachedUseCasesToInfoMap.containsKey(useCaseId)) {
            UseCaseAttachInfo useCaseAttachInfo = this.mAttachedUseCasesToInfoMap.get(useCaseId);
            useCaseAttachInfo.setActive(false);
            if (!useCaseAttachInfo.getAttached()) {
                this.mAttachedUseCasesToInfoMap.remove(useCaseId);
            }
        }
    }

    public void setUseCaseAttached(String useCaseId, SessionConfig sessionConfig, UseCaseConfig<?> userCaseConfig) {
        getOrCreateUseCaseAttachInfo(useCaseId, sessionConfig, userCaseConfig).setAttached(true);
    }

    public void setUseCaseDetached(String useCaseId) {
        if (this.mAttachedUseCasesToInfoMap.containsKey(useCaseId)) {
            UseCaseAttachInfo useCaseAttachInfo = this.mAttachedUseCasesToInfoMap.get(useCaseId);
            useCaseAttachInfo.setAttached(false);
            if (!useCaseAttachInfo.getActive()) {
                this.mAttachedUseCasesToInfoMap.remove(useCaseId);
            }
        }
    }

    public boolean isUseCaseAttached(String useCaseId) {
        if (!this.mAttachedUseCasesToInfoMap.containsKey(useCaseId)) {
            return false;
        }
        return this.mAttachedUseCasesToInfoMap.get(useCaseId).getAttached();
    }

    public Collection<UseCaseConfig<?>> getAttachedUseCaseConfigs() {
        return Collections.unmodifiableCollection(getUseCaseConfigs(UseCaseAttachState$$ExternalSyntheticLambda2.INSTANCE));
    }

    public Collection<SessionConfig> getAttachedSessionConfigs() {
        return Collections.unmodifiableCollection(getSessionConfigs(UseCaseAttachState$$ExternalSyntheticLambda1.INSTANCE));
    }

    public Collection<SessionConfig> getActiveAndAttachedSessionConfigs() {
        return Collections.unmodifiableCollection(getSessionConfigs(UseCaseAttachState$$ExternalSyntheticLambda0.INSTANCE));
    }

    static /* synthetic */ boolean lambda$getActiveAndAttachedSessionConfigs$2(UseCaseAttachInfo useCaseAttachInfo) {
        return useCaseAttachInfo.getActive() && useCaseAttachInfo.getAttached();
    }

    public void updateUseCase(String useCaseId, SessionConfig sessionConfig, UseCaseConfig<?> useCaseConfig) {
        if (this.mAttachedUseCasesToInfoMap.containsKey(useCaseId)) {
            UseCaseAttachInfo newUseCaseAttachInfo = new UseCaseAttachInfo(sessionConfig, useCaseConfig);
            UseCaseAttachInfo oldUseCaseAttachInfo = this.mAttachedUseCasesToInfoMap.get(useCaseId);
            newUseCaseAttachInfo.setAttached(oldUseCaseAttachInfo.getAttached());
            newUseCaseAttachInfo.setActive(oldUseCaseAttachInfo.getActive());
            this.mAttachedUseCasesToInfoMap.put(useCaseId, newUseCaseAttachInfo);
        }
    }

    public void removeUseCase(String useCaseId) {
        this.mAttachedUseCasesToInfoMap.remove(useCaseId);
    }

    public SessionConfig.ValidatingBuilder getActiveAndAttachedBuilder() {
        SessionConfig.ValidatingBuilder validatingBuilder = new SessionConfig.ValidatingBuilder();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, UseCaseAttachInfo> attachedUseCase : this.mAttachedUseCasesToInfoMap.entrySet()) {
            UseCaseAttachInfo useCaseAttachInfo = attachedUseCase.getValue();
            if (useCaseAttachInfo.getActive() && useCaseAttachInfo.getAttached()) {
                validatingBuilder.add(useCaseAttachInfo.getSessionConfig());
                list.add(attachedUseCase.getKey());
            }
        }
        Logger.d(TAG, "Active and attached use case: " + list + " for camera: " + this.mCameraId);
        return validatingBuilder;
    }

    public SessionConfig.ValidatingBuilder getAttachedBuilder() {
        SessionConfig.ValidatingBuilder validatingBuilder = new SessionConfig.ValidatingBuilder();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, UseCaseAttachInfo> attachedUseCase : this.mAttachedUseCasesToInfoMap.entrySet()) {
            UseCaseAttachInfo useCaseAttachInfo = attachedUseCase.getValue();
            if (useCaseAttachInfo.getAttached()) {
                validatingBuilder.add(useCaseAttachInfo.getSessionConfig());
                list.add(attachedUseCase.getKey());
            }
        }
        Logger.d(TAG, "All use case: " + list + " for camera: " + this.mCameraId);
        return validatingBuilder;
    }

    private UseCaseAttachInfo getOrCreateUseCaseAttachInfo(String useCaseId, SessionConfig sessionConfig, UseCaseConfig<?> useCaseConfig) {
        UseCaseAttachInfo useCaseAttachInfo = this.mAttachedUseCasesToInfoMap.get(useCaseId);
        if (useCaseAttachInfo != null) {
            return useCaseAttachInfo;
        }
        UseCaseAttachInfo useCaseAttachInfo2 = new UseCaseAttachInfo(sessionConfig, useCaseConfig);
        this.mAttachedUseCasesToInfoMap.put(useCaseId, useCaseAttachInfo2);
        return useCaseAttachInfo2;
    }

    private Collection<SessionConfig> getSessionConfigs(AttachStateFilter attachStateFilter) {
        List<SessionConfig> sessionConfigs = new ArrayList<>();
        for (Map.Entry<String, UseCaseAttachInfo> attachedUseCase : this.mAttachedUseCasesToInfoMap.entrySet()) {
            if (attachStateFilter == null || attachStateFilter.filter(attachedUseCase.getValue())) {
                sessionConfigs.add(attachedUseCase.getValue().getSessionConfig());
            }
        }
        return sessionConfigs;
    }

    private Collection<UseCaseConfig<?>> getUseCaseConfigs(AttachStateFilter attachStateFilter) {
        List<UseCaseConfig<?>> useCaseConfigs = new ArrayList<>();
        for (Map.Entry<String, UseCaseAttachInfo> attachedUseCase : this.mAttachedUseCasesToInfoMap.entrySet()) {
            if (attachStateFilter == null || attachStateFilter.filter(attachedUseCase.getValue())) {
                useCaseConfigs.add(attachedUseCase.getValue().getUseCaseConfig());
            }
        }
        return useCaseConfigs;
    }

    private static final class UseCaseAttachInfo {
        private boolean mActive = false;
        private boolean mAttached = false;
        private final SessionConfig mSessionConfig;
        private final UseCaseConfig<?> mUseCaseConfig;

        UseCaseAttachInfo(SessionConfig sessionConfig, UseCaseConfig<?> useCaseConfig) {
            this.mSessionConfig = sessionConfig;
            this.mUseCaseConfig = useCaseConfig;
        }

        /* access modifiers changed from: package-private */
        public UseCaseConfig<?> getUseCaseConfig() {
            return this.mUseCaseConfig;
        }

        /* access modifiers changed from: package-private */
        public SessionConfig getSessionConfig() {
            return this.mSessionConfig;
        }

        /* access modifiers changed from: package-private */
        public boolean getAttached() {
            return this.mAttached;
        }

        /* access modifiers changed from: package-private */
        public void setAttached(boolean attached) {
            this.mAttached = attached;
        }

        /* access modifiers changed from: package-private */
        public boolean getActive() {
            return this.mActive;
        }

        /* access modifiers changed from: package-private */
        public void setActive(boolean active) {
            this.mActive = active;
        }
    }
}
