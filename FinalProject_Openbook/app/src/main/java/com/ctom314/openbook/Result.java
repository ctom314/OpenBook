package com.ctom314.openbook;

import android.util.Log;

public class Result
{
    // For account login, return a result based on success or failure

    private final boolean success;

    public Result(boolean s)
    {
        success = s;
    }

    public static Result success()
    {
        return new Result(true);
    }

    public static Result failure(String message)
    {
        Log.e("Result", message);
        return new Result(false);
    }

    public boolean isSuccess()
    {
        return success;
    }
}
