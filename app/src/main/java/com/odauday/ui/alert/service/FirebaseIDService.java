package com.odauday.ui.alert.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import timber.log.Timber;

/**
 * Created by kunsubin on 5/17/2018.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.tag(TAG).d("Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    
    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
    
}
