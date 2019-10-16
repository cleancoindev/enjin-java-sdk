package com.enjin.sdk.service.roles.impl;

import java.io.IOException;
import java.util.List;

import com.enjin.sdk.graphql.GraphQLResponse;
import com.enjin.sdk.http.HttpCallback;
import com.enjin.sdk.http.HttpResponse;
import com.enjin.sdk.http.SchemaProvider;
import com.enjin.sdk.model.service.roles.CreateRole;
import com.enjin.sdk.model.service.roles.DeleteRole;
import com.enjin.sdk.model.service.roles.GetRoles;
import com.enjin.sdk.model.service.roles.Role;
import com.enjin.sdk.model.service.roles.UpdateRole;
import com.enjin.sdk.service.GraphQLServiceBase;
import com.enjin.sdk.service.roles.RolesService;

import retrofit2.Retrofit;

public class RolesServiceImpl extends GraphQLServiceBase implements RolesService {

    private final RolesRetrofitService service;
    private final SchemaProvider schemaProvider;

    public RolesServiceImpl(Retrofit retrofit, SchemaProvider schemaProvider) {
        this.service = retrofit.create(RolesRetrofitService.class);
        this.schemaProvider = schemaProvider;
    }

    @Override
    public void getRolesAsync(GetRoles query, HttpCallback<GraphQLResponse<List<Role>>> callback) {
        enqueueGraphQLCall(this.service.getRoles(schemaProvider.get(), query), callback);
    }

    @Override
    public void createRoleAsync(CreateRole query, HttpCallback<GraphQLResponse<Role>> callback) {
        enqueueGraphQLCall(this.service.createRole(schemaProvider.get(), query), callback);
    }

    @Override
    public void deleteRoleAsync(DeleteRole query, HttpCallback<GraphQLResponse<Role>> callback) {
        enqueueGraphQLCall(this.service.deleteRole(schemaProvider.get(), query), callback);
    }

    @Override
    public void updateRoleAsync(UpdateRole query, HttpCallback<GraphQLResponse<Role>> callback) {
        enqueueGraphQLCall(this.service.updateRole(schemaProvider.get(), query), callback);
    }

    @Override
    public HttpResponse<GraphQLResponse<List<Role>>> getRolesSync(GetRoles query) throws IOException {
        return executeGraphQLCall(this.service.getRoles(schemaProvider.get(), query));
    }

    @Override
    public HttpResponse<GraphQLResponse<Role>> createRoleSync(CreateRole query) throws IOException {
        return executeGraphQLCall(this.service.createRole(schemaProvider.get(), query));
    }

    @Override
    public HttpResponse<GraphQLResponse<Role>> deleteRoleSync(DeleteRole query) throws IOException {
        return executeGraphQLCall(this.service.deleteRole(schemaProvider.get(), query));
    }

    @Override
    public HttpResponse<GraphQLResponse<Role>> updateRoleSync(UpdateRole query) throws IOException {
        return executeGraphQLCall(this.service.updateRole(schemaProvider.get(), query));
    }
}
