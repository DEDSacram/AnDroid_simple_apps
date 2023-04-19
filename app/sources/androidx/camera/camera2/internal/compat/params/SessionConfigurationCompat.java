package androidx.camera.camera2.internal.compat.params;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.os.Build;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class SessionConfigurationCompat {
    public static final int SESSION_HIGH_SPEED = 1;
    public static final int SESSION_REGULAR = 0;
    private final SessionConfigurationCompatImpl mImpl;

    private interface SessionConfigurationCompatImpl {
        Executor getExecutor();

        InputConfigurationCompat getInputConfiguration();

        List<OutputConfigurationCompat> getOutputConfigurations();

        Object getSessionConfiguration();

        CaptureRequest getSessionParameters();

        int getSessionType();

        CameraCaptureSession.StateCallback getStateCallback();

        void setInputConfiguration(InputConfigurationCompat inputConfigurationCompat);

        void setSessionParameters(CaptureRequest captureRequest);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SessionMode {
    }

    public SessionConfigurationCompat(int sessionType, List<OutputConfigurationCompat> outputsCompat, Executor executor, CameraCaptureSession.StateCallback cb) {
        if (Build.VERSION.SDK_INT < 28) {
            this.mImpl = new SessionConfigurationCompatBaseImpl(sessionType, outputsCompat, executor, cb);
        } else {
            this.mImpl = new SessionConfigurationCompatApi28Impl(sessionType, outputsCompat, executor, cb);
        }
    }

    private SessionConfigurationCompat(SessionConfigurationCompatImpl impl) {
        this.mImpl = impl;
    }

    public static SessionConfigurationCompat wrap(Object sessionConfiguration) {
        if (sessionConfiguration != null && Build.VERSION.SDK_INT >= 28) {
            return new SessionConfigurationCompat(new SessionConfigurationCompatApi28Impl(sessionConfiguration));
        }
        return null;
    }

    static List<OutputConfigurationCompat> transformToCompat(List<OutputConfiguration> outputConfigurations) {
        ArrayList<OutputConfigurationCompat> outList = new ArrayList<>(outputConfigurations.size());
        for (OutputConfiguration outputConfiguration : outputConfigurations) {
            outList.add(OutputConfigurationCompat.wrap(outputConfiguration));
        }
        return outList;
    }

    public static List<OutputConfiguration> transformFromCompat(List<OutputConfigurationCompat> outputConfigurations) {
        ArrayList<OutputConfiguration> outList = new ArrayList<>(outputConfigurations.size());
        for (OutputConfigurationCompat outputConfiguration : outputConfigurations) {
            outList.add((OutputConfiguration) outputConfiguration.unwrap());
        }
        return outList;
    }

    public int getSessionType() {
        return this.mImpl.getSessionType();
    }

    public List<OutputConfigurationCompat> getOutputConfigurations() {
        return this.mImpl.getOutputConfigurations();
    }

    public CameraCaptureSession.StateCallback getStateCallback() {
        return this.mImpl.getStateCallback();
    }

    public Executor getExecutor() {
        return this.mImpl.getExecutor();
    }

    public InputConfigurationCompat getInputConfiguration() {
        return this.mImpl.getInputConfiguration();
    }

    public void setInputConfiguration(InputConfigurationCompat input) {
        this.mImpl.setInputConfiguration(input);
    }

    public CaptureRequest getSessionParameters() {
        return this.mImpl.getSessionParameters();
    }

    public void setSessionParameters(CaptureRequest params) {
        this.mImpl.setSessionParameters(params);
    }

    public Object unwrap() {
        return this.mImpl.getSessionConfiguration();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SessionConfigurationCompat)) {
            return false;
        }
        return this.mImpl.equals(((SessionConfigurationCompat) obj).mImpl);
    }

    public int hashCode() {
        return this.mImpl.hashCode();
    }

    private static final class SessionConfigurationCompatBaseImpl implements SessionConfigurationCompatImpl {
        private final Executor mExecutor;
        private InputConfigurationCompat mInputConfig = null;
        private final List<OutputConfigurationCompat> mOutputConfigurations;
        private CaptureRequest mSessionParameters = null;
        private final int mSessionType;
        private final CameraCaptureSession.StateCallback mStateCallback;

        SessionConfigurationCompatBaseImpl(int sessionType, List<OutputConfigurationCompat> outputs, Executor executor, CameraCaptureSession.StateCallback cb) {
            this.mSessionType = sessionType;
            this.mOutputConfigurations = Collections.unmodifiableList(new ArrayList(outputs));
            this.mStateCallback = cb;
            this.mExecutor = executor;
        }

        public int getSessionType() {
            return this.mSessionType;
        }

        public List<OutputConfigurationCompat> getOutputConfigurations() {
            return this.mOutputConfigurations;
        }

        public CameraCaptureSession.StateCallback getStateCallback() {
            return this.mStateCallback;
        }

        public Executor getExecutor() {
            return this.mExecutor;
        }

        public InputConfigurationCompat getInputConfiguration() {
            return this.mInputConfig;
        }

        public void setInputConfiguration(InputConfigurationCompat input) {
            if (this.mSessionType != 1) {
                this.mInputConfig = input;
                return;
            }
            throw new UnsupportedOperationException("Method not supported for high speed session types");
        }

        public CaptureRequest getSessionParameters() {
            return this.mSessionParameters;
        }

        public void setSessionParameters(CaptureRequest params) {
            this.mSessionParameters = params;
        }

        public Object getSessionConfiguration() {
            return null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SessionConfigurationCompatBaseImpl)) {
                return false;
            }
            SessionConfigurationCompatBaseImpl other = (SessionConfigurationCompatBaseImpl) obj;
            if (!Objects.equals(this.mInputConfig, other.mInputConfig) || this.mSessionType != other.mSessionType || this.mOutputConfigurations.size() != other.mOutputConfigurations.size()) {
                return false;
            }
            for (int i = 0; i < this.mOutputConfigurations.size(); i++) {
                if (!this.mOutputConfigurations.get(i).equals(other.mOutputConfigurations.get(i))) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            int i;
            int h = ((1 << 5) - 1) ^ this.mOutputConfigurations.hashCode();
            int i2 = (h << 5) - h;
            InputConfigurationCompat inputConfigurationCompat = this.mInputConfig;
            if (inputConfigurationCompat == null) {
                i = 0;
            } else {
                i = inputConfigurationCompat.hashCode();
            }
            int h2 = i2 ^ i;
            return ((h2 << 5) - h2) ^ this.mSessionType;
        }
    }

    private static final class SessionConfigurationCompatApi28Impl implements SessionConfigurationCompatImpl {
        private final SessionConfiguration mObject;
        private final List<OutputConfigurationCompat> mOutputConfigurations;

        SessionConfigurationCompatApi28Impl(Object sessionConfiguration) {
            this.mObject = (SessionConfiguration) sessionConfiguration;
            this.mOutputConfigurations = Collections.unmodifiableList(SessionConfigurationCompat.transformToCompat(((SessionConfiguration) sessionConfiguration).getOutputConfigurations()));
        }

        SessionConfigurationCompatApi28Impl(int sessionType, List<OutputConfigurationCompat> outputs, Executor executor, CameraCaptureSession.StateCallback cb) {
            this(new SessionConfiguration(sessionType, SessionConfigurationCompat.transformFromCompat(outputs), executor, cb));
        }

        public int getSessionType() {
            return this.mObject.getSessionType();
        }

        public List<OutputConfigurationCompat> getOutputConfigurations() {
            return this.mOutputConfigurations;
        }

        public CameraCaptureSession.StateCallback getStateCallback() {
            return this.mObject.getStateCallback();
        }

        public Executor getExecutor() {
            return this.mObject.getExecutor();
        }

        public InputConfigurationCompat getInputConfiguration() {
            return InputConfigurationCompat.wrap(this.mObject.getInputConfiguration());
        }

        public void setInputConfiguration(InputConfigurationCompat input) {
            this.mObject.setInputConfiguration((InputConfiguration) input.unwrap());
        }

        public CaptureRequest getSessionParameters() {
            return this.mObject.getSessionParameters();
        }

        public void setSessionParameters(CaptureRequest params) {
            this.mObject.setSessionParameters(params);
        }

        public Object getSessionConfiguration() {
            return this.mObject;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SessionConfigurationCompatApi28Impl)) {
                return false;
            }
            return Objects.equals(this.mObject, ((SessionConfigurationCompatApi28Impl) obj).mObject);
        }

        public int hashCode() {
            return this.mObject.hashCode();
        }
    }
}
