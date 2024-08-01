package com.emerson.rs.proact.appmonitor.alert;

import com.emerson.rs.proact.appmonitor.bean.AppExceptionMessage;

public interface ExceptionConsumeAndAlert {
    public void exceptionMessageConsumer(String errorMessage);
    public void notifyAlert(AppExceptionMessage exceptionBean);
}
