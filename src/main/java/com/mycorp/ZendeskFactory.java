package com.mycorp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.uri.Uri;

import java.util.regex.Pattern;

public class ZendeskFactory {

    private static final Pattern RESTRICTED_PATTERN = Pattern.compile("%2B", Pattern.LITERAL);

    private ZendeskFactory() {

    }

    public static Realm newRealm(final String username, final String password) {
        return new Realm.RealmBuilder()
                .setScheme(Realm.AuthScheme.BASIC)
                .setPrincipal(username)
                .setPassword(password)
                .setUsePreemptiveAuth(true)
                .build();
    }

    public static byte[] json(final ObjectMapper mapper, final Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new ZendeskException(e.getMessage(), e);
        }
    }

    public static Request req(final Realm realm,
                              final String oauthToken,
                              final String method,
                              final Uri template,
                              final String contentType,
                              final byte[] body) {
        RequestBuilder builder = new RequestBuilder(method);
        if (realm != null) {
            builder.setRealm(realm);
        } else {
            builder.addHeader("Authorization", "Bearer " + oauthToken);
        }
        builder.setUrl(RESTRICTED_PATTERN.matcher(template.toString()).replaceAll("+")); //replace out %2B with + due to API restriction
        builder.addHeader("Content-type", contentType);
        builder.setBody(body);
        return builder.build();
    }

    public static Uri cnst(final String url, final String template) {
        return Uri.create(url + template);
    }
}
