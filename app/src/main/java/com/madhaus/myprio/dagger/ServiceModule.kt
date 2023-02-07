package com.madhaus.myprio.dagger

import dagger.Module

// TODO we can probably delete this
@Module(includes = [RepositoryModule::class])
class ServiceModule {

//    @Provides
//    @Singleton
//    fun providePushNotifService(context: Application, pushNotifRepository: PushNotifRepository): PushNotifService =
//        PushNotifServiceImpl(context, pushNotifRepository)
}