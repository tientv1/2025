package com.assignment.selenium;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class TestRetry implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY_COUNT = 2;

    public TestRetry() {
        // Default constructor
    }

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (count < MAX_RETRY_COUNT) {
                count++;
                return true;
            }
        }
        return false;
    }
} 