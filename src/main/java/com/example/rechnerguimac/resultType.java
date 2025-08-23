package com.example.rechnerguimac;

public class resultType {
    private String result;
    private boolean isError;

    public String getResult() {
        return result;
    }

    public boolean isError() {
        return isError;
    }

    public resultType(double dResult, boolean isError) {
        this.result = dResult + "";
        this.isError = isError;
    }
}
