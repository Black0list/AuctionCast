package com.bidly.coreservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new IllegalStateException("Product not found");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}