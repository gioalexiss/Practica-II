package com.example.pr_garg_ii.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

// Error de la API
public class ErrorResponse {
    @SerializedName("status")
    public Integer status;

    @SerializedName("error")
    public String error;

    @SerializedName("mensajes")
    public Map<String, String> mensajes;
}
