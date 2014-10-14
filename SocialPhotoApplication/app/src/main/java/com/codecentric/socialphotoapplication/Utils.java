package com.codecentric.socialphotoapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Arnold on 10/14/2014.
 */
public class Utils {

    public static final Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //onSessionStateChange(session, state, exception);
            System.out.println("ON SESSION STATE CHANGED");
        }
    };

    public static final FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };

    public static Bitmap decodeFile(String path, int scaling) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/scaling, photoH/scaling);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    public static void openFacebookSessionAndPost(final String path, final Activity activity, final UiLifecycleHelper uiHelper) {
        openActiveSession(activity, true, Arrays.asList(new String[0]), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d("Facebook", "Session State: " + session.getState());
                if (exception != null) {
                    Log.d("Facebook", exception.getMessage());
                }
                else {
                    FacebookDialog fbd = new FacebookDialog.PhotoShareDialogBuilder(activity)
                            .addPhotos(Arrays.asList(new Bitmap[]{Utils.decodeFile(path, 200)})).build();
                    if (FacebookDialog.canPresentShareDialog(activity,
                            FacebookDialog.ShareDialogFeature.PHOTOS)) {
                        System.out.println("can");
                        uiHelper.trackPendingDialogCall(fbd.present());
                    }
                    else {
                        Request request = Request.newUploadPhotoRequest(
                                session, Utils.decodeFile(path, 200), new Request.Callback() {

                                    @Override
                                    public void onCompleted(Response response) {
                                        System.out.println(response);
                                        try {
                                            response.getError().getException().printStackTrace();
                                        }
                                        catch (Exception e) {

                                        }
                                    }
                                });
                        RequestAsyncTask task = new RequestAsyncTask(request);
                        task.execute();
                    }
                    System.out.println("done");
                }
            }
        });
    }

    private static Session openActiveSession(Activity activity, boolean allowLoginUI, List permissions, Session.StatusCallback callback) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

}
