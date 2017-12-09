package com.jaus.albertogiunta.readit.networking

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface LinkService {

    @GET
    fun contactWebsite(@Url url: String): Observable<ResponseBody>

}