package com.rolnik.alcoholapp.restUtils;

import io.reactivex.disposables.Disposable;

public interface ResponseHandler<T> {
    void onSubscribe(Disposable d);
    void onNext(T t);
    void onComplete();
    void onSocketTimeout();
    void onNotAuthorized();
    void onBadRequest();
    void onUnknownError();
    void showError(String message);
}
