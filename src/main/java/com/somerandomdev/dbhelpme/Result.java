package com.somerandomdev.dbhelpme;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

// NOTE: Could also be an interface...
public abstract class Result<T, E> {
    public abstract boolean isSuccess();
    public abstract T getSuccessValue();
    public abstract E getErrorValue();

    public Optional<T> tryGetSuccessValue() {
        return isSuccess() ? Optional.of(getSuccessValue()) : Optional.empty();
    }

    public Optional<E> tryGetErrorValue() {
        return isSuccess() ? Optional.of(getErrorValue()) : Optional.empty();
    }

    public T unwrapOr(T value) {
        return isSuccess() ? getSuccessValue() : value;
    }

    public T unwrapOrElse(Supplier<T> func) {
        return isSuccess() ? getSuccessValue() : func.get();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Result<?,?> other && (isSuccess() ? getSuccessValue().equals(other.getSuccessValue()) :
            getErrorValue().equals(other.getErrorValue()));
    }

    @Override
    public int hashCode() {
        return isSuccess() ? Objects.hashCode(getSuccessValue()) : Objects.hashCode(getErrorValue());
    }

    @Override
    public String toString() {
        return isSuccess() ? Objects.toString(getSuccessValue()) : Objects.toString(getErrorValue());
    }

    public static <T> Result<T, ?> success(T value) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return true;
            }

            @Override
            public T getSuccessValue() {
                return value;
            }

            @Override
            public Object getErrorValue() {
                return null;
            }
        };
    }

    public static <E> Result<?, E> error(E error) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public Object getSuccessValue() {
                return null;
            }

            @Override
            public E getErrorValue() {
                return error;
            }
        };
    }

    public static <T, E> Result<T, E> successReified(T value, Class<E> cls) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return true;
            }

            @Override
            public T getSuccessValue() {
                return value;
            }

            @Override
            public E getErrorValue() {
                return null;
            }
        };
    }

    public static <T, E> Result<T, E> errorReified(E error, Class<T> cls) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public T getSuccessValue() {
                return null;
            }

            @Override
            public E getErrorValue() {
                return error;
            }
        };
    }

    @SafeVarargs
    public static <T, E> Result<T, E> successReified(T value, E... type) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return true;
            }

            @Override
            public T getSuccessValue() {
                return value;
            }

            @Override
            public E getErrorValue() {
                return null;
            }
        };
    }

    @SafeVarargs
    public static <T, E> Result<T, E> errorReified(E error, T... type) {
        return new Result<>() {
            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public T getSuccessValue() {
                return null;
            }

            @Override
            public E getErrorValue() {
                return error;
            }
        };
    }
}
