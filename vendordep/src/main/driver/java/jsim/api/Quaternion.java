package jsim.api;

/**
 * Native Quaternion backed by C++ core (JNI).
 */
public class Quaternion {
    private long nativePtr;

    public Quaternion(double w, double x, double y, double z) {
        nativePtr = nativeCreate(w, x, y, z);
    }

    public static Quaternion fromAxisAngle(Vector3 axis, double angleRad) {
        long ptr = ((axis != null) ? axis.getNativePtr() : 0);
        long qptr = nativeFromAxisAngle(ptr, angleRad);
        Quaternion q = new Quaternion(0,0,0,0);
        q.nativePtr = qptr;
        return q;
    }

    public Quaternion multiply(Quaternion o) {
        long ptr = nativeMultiply(this.nativePtr, o.nativePtr);
        Quaternion q = new Quaternion(0,0,0,0);
        q.nativePtr = ptr;
        return q;
    }

    public Vector3 rotate(Vector3 v) {
        long vptr = (v != null) ? v.getNativePtr() : 0;
        long ptr = nativeRotate(this.nativePtr, vptr);
        return new Vector3(ptr);
    }

    public void dispose() {
        nativeDelete(nativePtr);
        nativePtr = 0;
    }

    private native long nativeCreate(double w, double x, double y, double z);
    private static native long nativeFromAxisAngle(long axisPtr, double angle);
    private native long nativeMultiply(long ptrA, long ptrB);
    private native long nativeRotate(long qPtr, long vPtr);
    private native void nativeDelete(long ptr);

    long getNativePtr() { return nativePtr; }
}
