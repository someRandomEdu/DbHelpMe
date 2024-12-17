package library;

@FunctionalInterface
public interface UpdateObserver<T> {
    public abstract void onUpdate(T oldValue, T newValue);
}
