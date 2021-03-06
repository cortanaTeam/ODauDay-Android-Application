package com.odauday.di.modules;

import com.odauday.data.local.notification.NotificationServiceImp;
import com.odauday.data.local.notification.NotificationEntityDao;
import com.odauday.data.local.notification.NotificationService;
import com.odauday.data.local.favorite.FavoritePropertyDao;
import com.odauday.data.local.favorite.FavoriteServiceImpl;
import com.odauday.data.local.place.RecentSearchPlaceDao;
import com.odauday.data.local.place.RecentSearchPlaceService;
import com.odauday.data.local.place.RecentSearchPlaceServiceImpl;
import com.odauday.data.local.tag.RecentTagDao;
import com.odauday.data.local.tag.RecentTagService;
import com.odauday.data.local.tag.RecentTagServiceImpl;
import com.odauday.data.remote.AdminService;
import com.odauday.data.remote.FavoriteService;
import com.odauday.data.remote.HistoryService;
import com.odauday.data.remote.SavedSearchService;
import com.odauday.data.remote.autocompleteplace.AutoCompletePlaceService;
import com.odauday.data.remote.direction.DirectionService;
import com.odauday.data.remote.geoinfo.GeoInfoService;
import com.odauday.data.remote.image.ImageService;
import com.odauday.data.remote.note.NoteService;
import com.odauday.data.remote.premium.PremiumService;
import com.odauday.data.remote.property.PropertyService;
import com.odauday.data.remote.property.SearchService;
import com.odauday.data.remote.similar.SimilarPropertyService;
import com.odauday.data.remote.user.UserService;
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
    SearchService provideSearchService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(SearchService.class);
    }
    
    @Provides
    @Singleton
    AutoCompletePlaceService provideAutoCompletePlaceService(
        @Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(AutoCompletePlaceService.class);
    }
    
    @Provides
    @Singleton
    GeoInfoService provideGeoInfoService(
        @Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(GeoInfoService.class);
    }
    
    @Provides
    @Singleton
    FavoriteService provideFavoriteService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(FavoriteService.class);
    }
    
    @Provides
    @Singleton
    SavedSearchService provideSavedSearchService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(SavedSearchService.class);
    }
    
    @Provides
    @Singleton
    PropertyService provideProtectPropertyService(
        @Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(PropertyService.class);
    }
    
    @Provides
    @Singleton
    HistoryService provideHistoryService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(HistoryService.class);
    }
    
    @Provides
    @Singleton
    ImageService provideImageService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(ImageService.class);
    }
    @Provides
    @Singleton
    AdminService provideAdminService(@Named("protectRetrofit")Retrofit retrofit){
        return  retrofit.create(AdminService.class);
    }
    @Provides
    @Singleton
    com.odauday.data.remote.NotificationService provideNotificationServiceRemote(@Named("protectRetrofit")Retrofit retrofit){
        return retrofit.create(com.odauday.data.remote.NotificationService.class);
    }
    
    @Provides
    @Singleton
    DirectionService provideDirectionService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(DirectionService.class);
    }
    
    @Provides
    @Singleton
    NoteService provideNoteService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(NoteService.class);
    }
    
    @Provides
    @Singleton
    PremiumService providePremiumService(@Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(PremiumService.class);
    }
    
    @Provides
    @Singleton
    SimilarPropertyService provideSimilarPropertyService(
        @Named("protectRetrofit") Retrofit retrofit) {
        return retrofit.create(SimilarPropertyService.class);
    }
    
    //--------------------------LOCAL---------------------------//
    
    @Provides
    @Singleton
    RecentTagService provideRecentTagService(RecentTagDao recentTagDao) {
        return new RecentTagServiceImpl(recentTagDao);
    }
    
    @Provides
    @Singleton
    RecentSearchPlaceService provideRecentSearchPlaceService(
        RecentSearchPlaceDao recentSearchPlaceDao) {
        return new RecentSearchPlaceServiceImpl(recentSearchPlaceDao);
    }
    
    @Provides
    @Singleton
    NotificationService provideNotificationService(NotificationEntityDao notificationEntityDao){
        return new NotificationServiceImp(notificationEntityDao);
    }
    
    com.odauday.data.local.favorite.FavoriteService provideFavoriteServiceLocal(
        FavoritePropertyDao favoritePropertyDao) {
        return new FavoriteServiceImpl(favoritePropertyDao);
    }
    
}
