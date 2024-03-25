package com.gunt.commerceapp.di

import com.gunt.data.repository.ProductRepositoryImpl
import com.gunt.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository
}