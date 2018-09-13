package com.arudanovsky.exchange.domain.interactor

import com.arudanovsky.exchange.data.api.ApiClient

abstract class BaseInteractor {
    protected val client = ApiClient.getRetrofitClient()
}