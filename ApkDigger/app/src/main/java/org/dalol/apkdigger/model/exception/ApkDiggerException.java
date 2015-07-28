package org.dalol.apkdigger.model.exception;

/**
 * Created by Filippo-TheAppExpert on 7/27/2015.
 */
public class ApkDiggerException extends RuntimeException {

    public ApkDiggerException() {
        super();
    }

    public ApkDiggerException(String detailMessage) {
        super(detailMessage);
    }

    public ApkDiggerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ApkDiggerException(Throwable throwable) {
        super(throwable);
    }
}