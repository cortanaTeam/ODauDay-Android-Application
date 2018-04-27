package com.odauday.di.modules;

import com.odauday.data.remote.FavoriteService;
import com.odauday.data.remote.HistoryService;
import com.odauday.data.remote.PropertyService;
import com.odauday.data.remote.SavedSearchService;
import com.odauday.data.remote.TagService;
import com.odauday.data.remote.UserService;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import retrofit2.Retrofit;

/**
 * Created by infamouSs on 2/28/18.
 */

@Module
public class ServiceBuildersModule {
    
    @Provides
    @Singleton
    UserService.Public PublicUserService(@Named("publicRetrofit") Retrofit retrofit) {
        return retrofit.create(UserService.Public.class);
    }
    
    @Provides
    @Singleton
    UserService.Protect provideProtectUserService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(UserService.Protect.class);
    }
    
    @Provides
    @Singleton
    TagService.Public providePublicTagService(@Named("publicRetrofit") Retrofit retrofit) {
        return retrofit.create(TagService.Public.class);
    }
    
    @Provides
    @Singleton
    TagService.Protect provideProtectTagService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(TagService.Protect.class);
    }
    
    @Provides
    @Singleton
    FavoriteService provideFavoriteService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(FavoriteService.class);
    }
    
    @Provides
    @Singleton
    SavedSearchService provideSearchService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(SavedSearchService.class);
    }
    
    @Provides
    @Singleton
    PropertyService.Public providePublicPropertyService(
        @Named("publicRetrofit") Retrofit retrofit) {
        return retrofit.create(PropertyService.Public.class);
    }
    
    @Provides
    @Singleton
    PropertyService.Protect provideProtectPropertyService(
        @Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(PropertyService.Protect.class);
    }
    
    @Provides
    @Singleton
    HistoryService provideHistoryService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(HistoryService.class);
    }
}
