package com.moeko.admin.repositorysearcher;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService{
    @GET("search/repositories")
    Call<Repositories> getRepositories(@Query("q") String query,
                                       @Query("sort")String sort);
}