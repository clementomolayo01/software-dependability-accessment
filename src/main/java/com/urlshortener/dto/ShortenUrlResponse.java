package com.urlshortener.dto;

public class ShortenUrlResponse {
    private String shortUrl;
    private String shortCode;
    private String originalUrl;

    public ShortenUrlResponse() {
    }

    public ShortenUrlResponse(String shortUrl, String shortCode, String originalUrl) {
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}

