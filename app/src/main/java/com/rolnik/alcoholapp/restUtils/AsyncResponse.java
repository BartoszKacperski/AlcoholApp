package com.rolnik.alcoholapp.restUtils;


import java.net.SocketTimeoutException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class AsyncResponse<T> {
    private Observable<T> observable;
    private ResponseHandler<T> responseHandler;
    private AuthorizationService authorizationService;

    public AsyncResponse(Observable<T> observable, ResponseHandler<T> responseHandler){
        this.observable = observable;
        this.responseHandler = responseHandler;
        this.authorizationService = new AuthorizationService();
    }

    public void execute(){
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                responseHandler.onSubscribe(d);
            }

            @Override
            public void onNext(T t) {
                responseHandler.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof HttpException){
                    switch(((HttpException) e).code()){
                        case 400: {
                            responseHandler.onBadRequest();
                            break;
                        }
                        case 401: {
                            responseHandler.onNotAuthorized();
                            authorizationService.renewCookie();
                            break;
                        }
                        default:{
                            responseHandler.onUnknownError();
                            break;
                        }
                    }
                } else if (e instanceof SocketTimeoutException){
                    responseHandler.onSocketTimeout();
                } else {
                    responseHandler.onUnknownError();
                }
            }

            @Override
            public void onComplete() {
                responseHandler.onComplete();
            }
        });
    }
}
