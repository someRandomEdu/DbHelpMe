package library;

public final class Observable<T> {
    private T value;
    private final UpdateObserver<T> updateObserver;

    public Observable(T value, UpdateObserver<T> updateObserver) {
        this.value = value;
        this.updateObserver = updateObserver;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        updateObserver.onUpdate(this.value, value);
        this.value = value;
    }
}
