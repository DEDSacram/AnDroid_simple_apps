package androidx.camera.core.processing;

interface ShaderProvider {
    public static final ShaderProvider DEFAULT = new ShaderProvider() {
    };

    String createFragmentShader(String samplerVarName, String fragCoordsVarName) {
        return null;
    }
}
