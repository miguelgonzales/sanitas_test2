package com.mycorp;

import com.ning.http.client.AsyncHttpClient;

public class ZendeskBuilder {

    private AsyncHttpClient client = null;
    private final String url;
    private String username = null;
    private String password = null;
    private String token = null;
    private String oauthToken = null;

    public ZendeskBuilder(String url) {
        this.url = url;
    }

    public ZendeskBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public ZendeskBuilder setPassword(String password) {
        this.password = password;
        if (password != null) {
            this.token = null;
            this.oauthToken = null;
        }
        return this;
    }

    public ZendeskBuilder setToken(String token) {
        this.token = token;
        if (token != null) {
            this.password = null;
            this.oauthToken = null;
        }
        return this;
    }

    public Zendesk build() {
        if (token != null) {
            return new Zendesk(client, url, username + "/token", token);
        }
        return new Zendesk(client, url, username, password);
    }
}
