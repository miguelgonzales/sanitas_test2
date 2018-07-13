package com.mycorp;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.Response;
import org.slf4j.Logger;

import static com.mycorp.ZendeskLogger.logResponse;
import static com.mycorp.ZendeskUtils.isStatus2xx;

public final class BasicAsyncCompletionHandler<T> extends ZendeskAsyncCompletionHandler<T> {
    private final Class<T> clazz;
    private final String name;
    private final Class[] typeParams;
    private final Logger logger;
    private final ObjectMapper mapper;

    public BasicAsyncCompletionHandler(final Logger logger,
                                       final ObjectMapper mapper,
                                       final Class clazz,
                                       final String name,
                                       final Class... typeParams) {
        this.clazz = clazz;
        this.name = name;
        this.typeParams = typeParams;
        this.logger = logger;
        this.mapper = mapper;
    }

    @Override
    public T onCompleted(Response response) throws Exception {
        logResponse(this.logger, response);
        if (isStatus2xx(response)) {
            if (typeParams.length > 0) {
                JavaType type = mapper.getTypeFactory().constructParametricType(clazz, typeParams);
                return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), type);
            }
            return mapper.convertValue(mapper.readTree(response.getResponseBodyAsStream()).get(name), clazz);
        } else if (isRateLimitResponse(response)) {
            throw new ZendeskException(response.toString());
        }
        if (response.getStatusCode() == 404) {
            return null;
        }
        throw new ZendeskException(response.toString());
    }

    private boolean isRateLimitResponse(Response response) {
        return response.getStatusCode() == 429;
    }
}
