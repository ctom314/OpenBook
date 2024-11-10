package com.ctom314.openbook;

public class Result
{
    // For account login, return a result based on success or failure

    private final boolean success;
    private final String message;

    public Result(boolean s, String m)
    {
        success = s;
        message = m;
    }

    public static Result success()
    {
        return new Result(true, null);
    }

    public static Result failure(String m)
    {
        return new Result(false, m);
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        if (!success)
        {
            return message;
        }
        else
        {
            return null;
        }
    }
}
