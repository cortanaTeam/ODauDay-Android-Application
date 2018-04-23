package com.odauday.ui.search.autocomplete;

import com.odauday.data.AutoCompletePlaceRepository;
import com.odauday.data.remote.autocompleteplace.model.AutoCompletePlace;
import com.odauday.viewmodel.BaseViewModel;
import com.odauday.viewmodel.model.Resource;
import io.reactivex.disposables.Disposable;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by infamouSs on 4/22/18.
 */
public class AutoCompletePlaceViewModel extends BaseViewModel {
    
    
    private final AutoCompletePlaceRepository mAutoCompletePlaceRepository;
    
    private AutoCompletePlaceContract mContract;
    
    @Inject
    public AutoCompletePlaceViewModel(AutoCompletePlaceRepository autoCompletePlaceRepository) {
        this.mAutoCompletePlaceRepository = autoCompletePlaceRepository;
    }
    
    public void search(String keyword) {
        Disposable disposable = mAutoCompletePlaceRepository
                  .search(keyword)
                  .doOnSubscribe(onSubscribe -> response.setValue(Resource.loading(null)))
                  .subscribe(success -> response.setValue(Resource.success(success)),
                            error -> response.setValue(Resource.error(error)));
        
        mCompositeDisposable.add(disposable);
    }
    
    public void searchLocal() {
        Disposable disposable = mAutoCompletePlaceRepository
                  .getRecentSearchLocal()
                  .subscribe(success -> mContract.onSuccessGetRecentSearchFromLocal(success), error -> Timber.d(error.getMessage()));
        
        mCompositeDisposable.add(disposable);
    }
    
    public void create(AutoCompletePlace autoCompletePlace) {
        Disposable disposable = mAutoCompletePlaceRepository
                  .createRecentSearch(autoCompletePlace)
                  .subscribe(success -> Timber.d("Create successull"), error -> Timber.d(error.getMessage()));
        
        mCompositeDisposable.add(disposable);
    }
    
    public void delete(AutoCompletePlace autoCompletePlace) {
        Disposable disposable = mAutoCompletePlaceRepository
                  .deleteRecentSearch(autoCompletePlace)
                  .subscribe(success -> mContract.onSuccessDeleteRecentSearchFromLocal(autoCompletePlace), error -> Timber.d(error.getMessage()));
        
        mCompositeDisposable.add(disposable);
    }
    
    public void setContract(AutoCompletePlaceContract contract) {
        mContract = contract;
    }
}
