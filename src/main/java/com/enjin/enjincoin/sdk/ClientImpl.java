package com.enjin.enjincoin.sdk;

import com.enjin.enjincoin.sdk.authentication.AuthenticationInterceptor;
import com.enjin.enjincoin.sdk.converter.GraphConverter;
import com.enjin.enjincoin.sdk.converter.JsonStringConverter;
import com.enjin.enjincoin.sdk.cookiejar.ClearableCookieJar;
import com.enjin.enjincoin.sdk.cookiejar.PersistentCookieJar;
import com.enjin.enjincoin.sdk.cookiejar.cache.SetCookieCache;
import com.enjin.enjincoin.sdk.cookiejar.persistence.MemoryCookiePersistor;
import com.enjin.enjincoin.sdk.service.auth.AuthRetrofitService;
import com.enjin.enjincoin.sdk.service.auth.vo.AuthBody;
import com.enjin.enjincoin.sdk.service.auth.vo.AuthData;
import com.enjin.enjincoin.sdk.service.identities.IdentitiesService;
import com.enjin.enjincoin.sdk.service.identities.impl.IdentitiesServiceImpl;
import com.enjin.enjincoin.sdk.service.notifications.NotificationsService;
import com.enjin.enjincoin.sdk.service.notifications.impl.NotificationsServiceImpl;
import com.enjin.enjincoin.sdk.service.platform.PlatformService;
import com.enjin.enjincoin.sdk.service.platform.impl.PlatformServiceImpl;
import com.enjin.enjincoin.sdk.service.requests.RequestsService;
import com.enjin.enjincoin.sdk.service.requests.impl.RequestsServiceImpl;
import com.enjin.enjincoin.sdk.service.tokens.TokensService;
import com.enjin.enjincoin.sdk.service.tokens.impl.TokensServiceImpl;
import com.enjin.enjincoin.sdk.service.users.UsersService;
import com.enjin.enjincoin.sdk.service.users.impl.UsersServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class ClientImpl implements Client {

    private String               url;
    private String               appId;
    private OkHttpClient         client;
    private Retrofit             retrofit;
    private AuthRetrofitService  authRetrofitService;
    private IdentitiesService    identitiesService;
    private UsersService         userService;
    private RequestsService      requestsService;
    private TokensService        tokensService;
    private PlatformService      platformService;
    private NotificationsService notificationsService;

    private ClearableCookieJar cookieJar;

    public ClientImpl(final String url, final String appId, final boolean log) {
        this.url = url;
        this.appId = appId;
        this.cookieJar = new PersistentCookieJar(new SetCookieCache(), new MemoryCookiePersistor());

        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.cookieJar(this.cookieJar);
        clientBuilder.addInterceptor(new AuthenticationInterceptor(this.cookieJar));

        if (log) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }

        final Converter.Factory gsonFactory = GsonConverterFactory.create(getGsonInstance());

        this.client = clientBuilder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(this.client)
                .addConverterFactory(GraphConverter.create())
                .addConverterFactory(new JsonStringConverter(gsonFactory))
                .addConverterFactory(gsonFactory)
                .build();
    }

    public Response<AuthData> auth(String clientSecret) throws IOException {
        Call<AuthData> call = getAuthRetrofitService()
                .auth(new AuthBody("client_credentials", this.appId, clientSecret));
        // Failure needs to be handled by the callee.
        retrofit2.Response<AuthData> response = call.execute();

        if (response == null) {
            return null;
        }

        if (response.isSuccessful()) {
            AuthData data = response.body();
            this.cookieJar.addCookie(new Cookie.Builder()
                                             .domain(this.retrofit.baseUrl().host()).name("laravel_session_type")
                                             .value(data.getTokenType())
                                             .build());
            this.cookieJar.addCookie(new Cookie.Builder()
                                             .domain(this.retrofit.baseUrl().host()).name("laravel_session")
                                             .value(String.format("%s@%s", this.appId, data.getAccessToken()))
                                             .build());
        }

        return new Response<>(response.code(), response.body());
    }

    private AuthRetrofitService getAuthRetrofitService() {
        if (this.authRetrofitService == null) {
            this.authRetrofitService = this.retrofit.create(AuthRetrofitService.class);
        }
        return this.authRetrofitService;
    }

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public IdentitiesService getIdentitiesService() {
        if (this.identitiesService == null) {
            this.identitiesService = new IdentitiesServiceImpl(retrofit);
        }
        return this.identitiesService;
    }

    @Override
    public UsersService getUsersService() {
        if (this.userService == null) {
            this.userService = new UsersServiceImpl(retrofit);
        }
        return this.userService;
    }

    @Override
    public RequestsService getRequestsService() {
        if (this.requestsService == null) {
            this.requestsService = new RequestsServiceImpl(retrofit);
        }
        return this.requestsService;
    }

    @Override
    public TokensService getTokensService() {
        if (this.tokensService == null) {
            this.tokensService = new TokensServiceImpl(retrofit);
        }
        return this.tokensService;
    }

    @Override
    public PlatformService getPlatformService() {
        if (this.platformService == null) {
            this.platformService = new PlatformServiceImpl(retrofit);
        }
        return this.platformService;
    }


    @Override
    public NotificationsService getNotificationsService() {
        if (this.notificationsService == null) {
            this.notificationsService = new NotificationsServiceImpl(getPlatformService(), this.appId);
        }

        return this.notificationsService;
    }

    private Gson getGsonInstance() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory())
                .create();
    }

    @Override
    public void close() throws IOException {
        this.client.dispatcher().executorService().shutdown();
        if (this.notificationsService != null) {
            this.notificationsService.shutdown();
        }
    }
}