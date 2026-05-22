package jsim.api;

/**
 * Native Vector3 backed by C++ core (JNI).
 */
public class Vector3 {
    private long nativePtr;

    public Vector3(double x, double y, double z) {
        nativePtr = nativeCreate(x, y, z);
    }

    Vector3(long ptr) {
        this.nativePtr = ptr;
    }

    long getNativePtr() { return nativePtr; }

    public double norm() { return nativeNorm(nativePtr); }

    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    private native long nativeCreate(double x, double y, double z);
    private native double nativeNorm(long ptr);
    private native void nativeDelete(long ptr);
}
