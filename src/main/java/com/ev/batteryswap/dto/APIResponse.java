package com.ev.batteryswap.dto;

import lombok.Getter;

@Getter
public class APIResponse {
    private final boolean Success;
    private final String Message;

    public APIResponse(boolean success, String message) {
        this.Success = success;
        this.Message = message;
    }
}
