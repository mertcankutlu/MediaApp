package com.media.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // İlerleyen fazlarda Data katmanı implementasyonları yazıldıkça 
    // @Binds fonksiyonları buraya eklenecektir.
}
