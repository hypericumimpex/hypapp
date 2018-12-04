package com.hypericum.hypapp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crash.FirebaseCrash;
import com.hypericum.hypapp.R;
import com.hypericum.hypapp.activity.CartActivity;
import com.hypericum.hypapp.activity.HomeActivity;
import com.hypericum.hypapp.activity.NotificationActivity;
import com.hypericum.hypapp.activity.SearchFromHomeActivity;
import com.hypericum.hypapp.customview.textview.TextViewBold;
import com.hypericum.hypapp.customview.textview.TextViewRegular;
import com.hypericum.hypapp.helper.DatabaseHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnResponseListner {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    public ImageView ivNotification, ivSearch;
    public SharedPreferences sharedpreferences;
    public CustomProgressDialog progressDialog;
    public String lat, lon;
    AsyncProgressDialog ad;
    Location mLastLocation;
    String language;
    private ImageView ivBack, ivLogo;
    private TextViewBold tvTitle;
    private FrameLayout flCart;
    private TextViewRegular tvToolCart;
    private CoordinatorLayout crMain;
    private DatabaseHelper databaseHelper;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread thread, Throwable ex) {
                // get the crash info
                //log it into the file
                if (defaultHandler != null && ex != null && ex.getMessage() != null) {
                    FirebaseCrash.report(new Exception(ex));
                    defaultHandler.uncaughtException(thread, ex);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (URLS.isUrlBlank() == null) {
            new APIS();
        }
    }

    public void settvTitle(String title) {
        tvTitle = (TextViewBold) findViewById(R.id.tvTitle);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivLogo.setVisibility(View.GONE);
        tvTitle.setText(title);
    }

    public void setHomecolorTheme(String color) {
        LinearLayout llSearch = findViewById(R.id.llSearch);
        LinearLayout llDrawer = findViewById(R.id.llDrawer);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));

        llSearch.setBackgroundColor(Color.parseColor(color));
        if (llDrawer != null) {
            llDrawer.setBackgroundColor(Color.parseColor(color));

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }

        TextView tvCategory = findViewById(R.id.tvCategory);
        tvCategory.setBackgroundColor(Color.parseColor(color));
    }

    public void setLocaleByLanguageChange(String lang) {


        String languageToLoad = lang; // your language
        if (lang.contains("-")) {
            String[] array = lang.split("-");
            if (array.length > 0) {
                languageToLoad = array[0];
            } else {
                languageToLoad = lang;
            }
        } else {
            languageToLoad = lang;

        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        recreate();
        Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void setEmptyColor() {

        TextViewRegular tvContinueShopping = findViewById(R.id.tvContinueShopping);
        ImageView ivGo = findViewById(R.id.ivGo);
        tvContinueShopping.setTextColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(5, Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvContinueShopping.setBackground(gradientDrawable);
        ivGo.setColorFilter(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    public void clearCookiesAndCache(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }


    public void setToolbarTheme() {

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }

    }

    public void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }

    }

    public void settvImage() {
        tvTitle = findViewById(R.id.tvTitle);
        ivLogo = findViewById(R.id.ivLogo);
        ivBack = findViewById(R.id.ivBack);
        tvTitle.setVisibility(View.GONE);
        ivLogo.setVisibility(View.VISIBLE);
        if (Constant.APPLOGO_LIGHT != null && !Constant.APPLOGO_LIGHT.equals("")) {
            Picasso.with(this).load(Constant.APPLOGO_LIGHT).error(R.drawable.logo).into(ivLogo);
        }
    }

    public SharedPreferences getPreferences() {

        sharedpreferences = getSharedPreferences(
                Constant.MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences;
    }


    public void setScreenLayoutDirection() {
        crMain = findViewById(R.id.crMain);
        if (Constant.IS_RTL) {
            if (Build.VERSION.SDK_INT >= 17) {
                crMain.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                // Do something for lollipop and above versions
            } else {
                ViewCompat.setLayoutDirection(crMain, View.LAYOUT_DIRECTION_RTL);
                // do something for phones running an SDK before lollipop
            }
        } else {
            if (Build.VERSION.SDK_INT >= 17) {
                crMain.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                // Do something for lollipop and above versions
            } else {
                ViewCompat.setLayoutDirection(crMain, View.LAYOUT_DIRECTION_LTR);
                // do something for phones running an SDK before lollipop
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), null, Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
            setTaskDescription(taskDescription);
        }


    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    public void showSearch() {
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setVisibility(View.VISIBLE);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, SearchFromHomeActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(100); // Update location every second


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    lat = String.valueOf(mLastLocation.getLatitude());
                    lon = String.valueOf(mLastLocation.getLongitude());
                    Log.e("lat", lat);
                    Log.e("Long", lon);
//                    SharedPreferences.Editor pre = getPreferences().edit();
//                    pre.putFloat(RequestParamUtils.LATITUDE, (float) mLastLocation.getLatitude());
//                    pre.putFloat(RequestParamUtils.LONGITUDE, (float) mLastLocation.getLongitude());
//                    pre.commit();
                }
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

//        lat = 21.21981 + "";
//        lon = "" + 72.7808673;
        Location locationA = new Location("Location1");
        locationA.setLatitude(getPreferences().getFloat(RequestParamUtils.LATITUDE, 0));
        locationA.setLongitude(getPreferences().getFloat(RequestParamUtils.LONGITUDE, 0));

        Location locationB = new Location("Location2");
        locationB.setLatitude(Float.parseFloat(lat));
        locationB.setLongitude(Float.parseFloat(lon));


        if (locationA.distanceTo(locationB) >= Constant.DISTANCERANGE && !getPreferences().getString(RequestParamUtils.DEVICE_TOKEN, "").equals("")) {
            Log.e("Old LatLong is ", locationA.getLatitude() + ", " + locationA.getLongitude());
            Log.e("New LatLong is ", lat + "," + lon);
            geoFencingCall(getPreferences().getFloat(RequestParamUtils.LATITUDE, Float.parseFloat(lat)) + "", getPreferences().getFloat(RequestParamUtils.LONGITUDE, Float.parseFloat(lon)) + "");
            SharedPreferences sharedpreferences = getSharedPreferences(
                    Constant.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor pre = sharedpreferences.edit();
            pre.putFloat(RequestParamUtils.LATITUDE, Float.parseFloat(lat));
            pre.putFloat(RequestParamUtils.LONGITUDE, Float.parseFloat(lon));
            pre.commit();
        }
    }


    public void geoFencingCall(String lat, String lng) {
        if (Utils.isInternetConnected(this)) {
            PostApi postApi = new PostApi(BaseActivity.this, RequestParamUtils.geoFencingCall, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.DEVICE_TOKEN, Constant.DEVICE_TOKEN);
                jsonObject.put(RequestParamUtils.LATITUDE, lat);
                jsonObject.put(RequestParamUtils.LONGITUDE, lng);
                jsonObject.put(RequestParamUtils.DEVICE_TYPE, "2");
                postApi.callPostApi(new URLS().GEOFENCING, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.geoFencingCall)) {
            if (response != null && response.length() > 0) {
                try {
                    Log.e("Response is ", response);
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        lat = String.valueOf(mLastLocation.getLatitude());
                        lon = String.valueOf(mLastLocation.getLongitude());

                        SharedPreferences.Editor pre = getPreferences().edit();
                        pre.putFloat(RequestParamUtils.LATITUDE, (float) mLastLocation.getLatitude());
                        pre.putFloat(RequestParamUtils.LONGITUDE, (float) mLastLocation.getLongitude());
                        pre.commit();

                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showCart() {
        flCart = findViewById(R.id.flCart);
        tvToolCart = findViewById(R.id.tvToolCart);
        databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.getFromCart(0).size() > 0) {
            tvToolCart.setText(databaseHelper.getFromCart(0).size() + "");
            tvToolCart.setVisibility(View.VISIBLE);
        } else {
            tvToolCart.setVisibility(View.GONE);
        }
        flCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        flCart.setVisibility(View.VISIBLE);
    }


    public void hideSearchNotification() {
        ivSearch = findViewById(R.id.ivSearch);
        ivNotification = findViewById(R.id.ivNotification);
        ivSearch.setVisibility(View.GONE);
        ivNotification.setVisibility(View.GONE);
    }

    public void showNotification() {
        ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setVisibility(View.VISIBLE);
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showBackButton() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void hideBackButton() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);

    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


//    public void showProgress(String msg) {
//        if (!isFinishing()) {
//
//            try {
//                if (ad != null && ad.isShowing()) {
//                    return;
//                }
//
//                ad = AsyncProgressDialog.getInstant(this);
//                ad.setColor();
//                ad.show(msg);
//            } catch (Exception e) {
//                Log.e("Show Progress   ", e.getMessage());
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void dismissProgress() {
//        try {
//            if (ad != null) {
//                ad.dismiss();
//            }
//        } catch (Exception e) {
//            Log.e("dismissProgress dialog ", e.getMessage());
//        }
//    }

    //TODO : Show Progress
    public void showProgress(String val) {
        if (progressDialog != null) {
            progressDialog.dissmissDialog();
        }
        progressDialog = new CustomProgressDialog(BaseActivity.this);
        progressDialog.showCustomDialog(BaseActivity.this);
    }

    //TODO : Dismiss progress
    public void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dissmissDialog();
        }

    }

    @Override
    public void attachBaseContext(Context base) {
        Log.e("Attache", "called");
        super.attachBaseContext(updateBaseContextLocale(base));

    }

    public Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constant.MyPREFERENCES, Context.MODE_PRIVATE);
        // String language = sharedPref.getString(RequestParamUtils.LANGUAGE, "en");
        if (sharedPref.getString(RequestParamUtils.LANGUAGE, "").isEmpty()) {
            if (!sharedPref.getString(RequestParamUtils.DEFAULTLANGUAGE, "").isEmpty()) {
                language = sharedPref.getString(RequestParamUtils.DEFAULTLANGUAGE, "");
            } else {
                language = "en";
            }
        } else {
            language = sharedPref.getString(RequestParamUtils.LANGUAGE, "");
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public String getlanuage() {
        String lng = getPreferences().getString(RequestParamUtils.LANGUAGE, "");
        if (!lng.equals("") && lng != null) {
            return lng;
        } else {
            if (Constant.IS_WPML_ACTIVE) {
                String defaultLng = getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "");
                if (!defaultLng.equals("") && defaultLng != null) {
                    return defaultLng;
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }
    }


}
