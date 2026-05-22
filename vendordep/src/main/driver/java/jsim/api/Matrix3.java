package jsim.api;

/**
 * Native Matrix3 backed by C++ core (JNI).
 */
public class Matrix3 {
    private long nativePtr;

    public Matrix3() {
        nativePtr = nativeCreate();
    }

    public static Matrix3 identity() {
        return new Matrix3();
    }

    public Vector3 multiply(Vector3 v) {
        long ptr = nativeMultiply(this.nativePtr, v.getNativePtr());
        return new Vector3(ptr);
    }

    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    private native long nativeCreate();
    private native long nativeMultiply(long mPtr, long vPtr);
    private native void nativeDelete(long ptr);
}
