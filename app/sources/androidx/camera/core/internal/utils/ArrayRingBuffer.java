package androidx.camera.core.internal.utils;

import androidx.camera.core.internal.utils.RingBuffer;
import java.util.ArrayDeque;

public class ArrayRingBuffer<T> implements RingBuffer<T> {
    private static final String TAG = "ZslRingBuffer";
    private final ArrayDeque<T> mBuffer;
    private final Object mLock;
    final RingBuffer.OnRemoveCallback<T> mOnRemoveCallback;
    private final int mRingBufferCapacity;

    public ArrayRingBuffer(int ringBufferCapacity) {
        this(ringBufferCapacity, (RingBuffer.OnRemoveCallback) null);
    }

    public ArrayRingBuffer(int ringBufferCapacity, RingBuffer.OnRemoveCallback<T> onRemoveCallback) {
        this.mLock = new Object();
        this.mRingBufferCapacity = ringBufferCapacity;
        this.mBuffer = new ArrayDeque<>(ringBufferCapacity);
        this.mOnRemoveCallback = onRemoveCallback;
    }

    public void enqueue(T element) {
        T removedItem = null;
        synchronized (this.mLock) {
            if (this.mBuffer.size() >= this.mRingBufferCapacity) {
                removedItem = dequeue();
            }
            this.mBuffer.addFirst(element);
        }
        RingBuffer.OnRemoveCallback<T> onRemoveCallback = this.mOnRemoveCallback;
        if (onRemoveCallback != null && removedItem != null) {
            onRemoveCallback.onRemove(removedItem);
        }
    }

    public T dequeue() {
        T removeLast;
        synchronized (this.mLock) {
            removeLast = this.mBuffer.removeLast();
        }
        return removeLast;
    }

    public int getMaxCapacity() {
        return this.mRingBufferCapacity;
    }

    public boolean isEmpty() {
        boolean isEmpty;
        synchronized (this.mLock) {
            isEmpty = this.mBuffer.isEmpty();
        }
        return isEmpty;
    }
}
