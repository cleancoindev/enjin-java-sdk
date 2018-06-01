package com.enjin.enjincoin.sdk.client;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.enjin.enjincoin.sdk.client.cookiejar.ClearableCookieJar;
import com.enjin.enjincoin.sdk.client.cookiejar.PersistentCookieJar;
import com.enjin.enjincoin.sdk.client.cookiejar.cache.SetCookieCache;
//import com.enjin.enjincoin.sdk.client.cookiejar.persistence.SharedPrefsCookiePersistor;
import com.enjin.enjincoin.sdk.client.cookiejar.persistence.CookiePersistor;
import com.enjin.enjincoin.sdk.client.cookiejar.persistence.MemoryCookiePersistor;
import com.enjin.enjincoin.sdk.client.serialization.retrofit.JsonStringConverterFactory;
import com.enjin.enjincoin.sdk.client.service.GraphQLRetrofitService;
import com.enjin.enjincoin.sdk.client.service.identities.IdentitiesService;
import com.enjin.enjincoin.sdk.client.service.identities.impl.IdentitiesServiceImpl;
import com.enjin.enjincoin.sdk.client.service.platform.PlatformService;
import com.enjin.enjincoin.sdk.client.service.platform.impl.PlatformServiceImpl;
import com.enjin.enjincoin.sdk.client.service.requests.RequestsService;
import com.enjin.enjincoin.sdk.client.service.tokens.TokensService;
import com.enjin.enjincoin.sdk.client.service.tokens.impl.TokensServiceImpl;
import com.enjin.enjincoin.sdk.client.service.users.UsersService;
import com.enjin.enjincoin.sdk.client.service.users.impl.UsersServiceImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.enjin.enjincoin.sdk.client.service.notifications.NotificationsService;
import com.enjin.enjincoin.sdk.client.service.notifications.impl.NotificationsServiceImpl;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientImpl implements Client {

    private int appId;
    private OkHttpClient client;
    private Retrofit retrofit;
    private GraphQLRetrofitService graphQLService;
    private IdentitiesService identitiesService;
    private UsersService userService;
    private RequestsService requestsService;
    private TokensService tokensService;
    private PlatformService platformService;
    private NotificationsService notificationsService;

    private ClearableCookieJar cookieJar;

    public ClientImpl(String url, int appId, boolean log) {
        this.appId = appId;

        cookieJar = new PersistentCookieJar(new SetCookieCache(), new MemoryCookiePersistor());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.cookieJar(cookieJar).build();

        if (log) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }

        Converter.Factory gsonFactory = GsonConverterFactory.create(getGsonInstance());

        this.client = clientBuilder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(this.client)
                .addConverterFactory(new JsonStringConverterFactory(gsonFactory))
                .addConverterFactory(gsonFactory)
                .build();
    }

    @Override
    public int getAppId() {
        return this.appId;
    }

    public GraphQLRetrofitService getGraphQLService() {
        if (this.graphQLService == null) {
            this.graphQLService = this.retrofit.create(GraphQLRetrofitService.class);
        }
        return this.graphQLService;
    }

    @Override
    public IdentitiesService getIdentitiesService() {
        if (this.identitiesService == null) {
            this.identitiesService = new IdentitiesServiceImpl(getGraphQLService());
        }
        return this.identitiesService;
    }

    @Override
    public UsersService getUsersService() {
        if (this.userService == null) {
            this.userService = new UsersServiceImpl(getGraphQLService());
        }
        return this.userService;
    }
    @Override
    public RequestsService getRequestsService() {
        if (this.requestsService == null) {
            this.requestsService = null;
        }
        return this.requestsService;
    }

    @Override
    public TokensService getTokensService() {
        if (this.tokensService == null) {
            this.tokensService = new TokensServiceImpl(this.graphQLService);
        }
        return this.tokensService;
    }

    @Override
    public PlatformService getPlatformService() {
        if (this.platformService == null) {
            this.platformService = new PlatformServiceImpl(this.graphQLService);
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
        if (this.notificationsService != null)
            this.notificationsService.shutdown();
    }
}