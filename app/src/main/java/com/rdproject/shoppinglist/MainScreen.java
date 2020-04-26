package com.rdproject.shoppinglist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rdproject.shoppinglist.adapters.RVAdapter;
import com.rdproject.shoppinglist.dialogs.AddDialog;
import com.rdproject.shoppinglist.dialogs.AddListDialog;
import com.rdproject.shoppinglist.dialogs.ChooseListDialog;
import com.rdproject.shoppinglist.dialogs.ChooseSharedListDialog;
import com.rdproject.shoppinglist.dialogs.ModifyItemDialog;
import com.rdproject.shoppinglist.dialogs.ModifyListDialog;
import com.rdproject.shoppinglist.dialogs.RenameListDialog;
import com.rdproject.shoppinglist.login.LoginActivity;
import com.rdproject.shoppinglist.login.User;
import com.rdproject.shoppinglist.modules.App;
import com.shoppinglist.rdproject.shoppinglist.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddDialog.OnTextInputListener, AddListDialog.OnListNameInputListener,
        RenameListDialog.OnListListRenameListener, ChooseListDialog.OnChooseListListener, ModifyListDialog.OnModifyListListener,
        ModifyItemDialog.OnItemModifyListener, ChooseSharedListDialog.OnChooseSharedListListener {
    public static final String TAG = "MainActivityTAG";
    public static final int LOGIN_RESULT = 2121;
    public static final String APP_PREFERENCES = "listsettings";
    public static final String APP_PREFERENCES_LIST_NAME = "listName";
    public static final String APP_PREFERENCES_MAP_OF_LISTS = "mapOfLists";
    public static final String APP_PREFERENCES_USER_EMAIL = "userEmail";
    public static final String APP_PREFERENCES_IS_ADS_FREE = "isAdsfree";
    private static final int FIND_USER_TO_SHARE = 3131;

    public static boolean isAdsfree = false;
    public static boolean isAdsfreeForNow = false;

    private User localUser;
    public String userId;

    private RecyclerView rViewToDo;
    private RecyclerView rViewDone;
    private RVAdapter rAdapterToDo;
    private RVAdapter rAdapterDone;
    private List<Product> shoppingList = new ArrayList<>();
    private List<Product> doneList = new ArrayList<>();
    private List<SharedList> sharedLists = new ArrayList<>();
    private String listName;
    private String pathToList;
    private List<String> listOfListsToDisplay;
    private Map<String, String> mapOfLists;
    private Spinner chooseListSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    @Inject
    SharedPreferences mSettings;
    @Inject
    FirebaseAuth mAuth;
    @Inject
    FirebaseDatabase database;
    public DatabaseReference databaseRef;

    private DrawerLayout drawer;
    private ImageView userPicView;
    private TextView userNameView;
    private TextView userMailView;
    private MenuItem logMenuItem;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.colorLightGrey));
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddDialog().show(getFragmentManager(), "AddDialog");

            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // end of template
        // start code
        isAdsfreeForNow = false;

        ((App) getApplicationContext()).getAppComponent().inject(this);

        userId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        loadPreferences();
        bindUserViews();


        rAdapterToDo = new RVAdapter(this, shoppingList, R.id.lis_to_do);
        rViewToDo.setAdapter(rAdapterToDo);

        rAdapterDone = new RVAdapter(this, doneList, R.id.list_done);
        rViewDone.setAdapter(rAdapterDone);

        listOfListsToDisplay = getListOfTablesToDisplay();
        chooseListSpinner = createSpinner();
        localUser = new User();

        pathToList = userId + "/" + listName;
        loadData(pathToList);
        loadSharedLists();

        Log.d(TAG, "end of onCreate   " + FacebookSdk.getApplicationSignature(getApplicationContext()));

        //getHashKey();
    }

    private void loadData(String path) {

        if (path.contains(userId)) {                                // in case we get path to our self list
            this.listName = path.substring(path.lastIndexOf('/') + 1);
            if (mapOfLists.containsKey(listName)) {
                setTitle(mapOfLists.get(listName));
            } else {
                setTitle(listName);
            }
        }
        databaseInitialize(localUser);
        DatabaseReference listRef = databaseRef.child(path);

        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null && !shoppingList.contains(product) && !doneList.contains(product)) {
                    switch (product.getStatus()) {
                        case 0:
                            shoppingList.add(0, product);
                            rAdapterToDo.notifyDataSetChanged();
                            //Log.d(TAG, "shoppingList.contains(product)  - " + product.getName() + "   : " + shoppingList.contains(product));
                            break;
                        case 1:
                            doneList.add(0, product);
                            rAdapterDone.notifyDataSetChanged();
                            //Log.d(TAG, "shoppingList.contains(product)  - " + product.getName() + "   : " + shoppingList.contains(product));
                            break;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Product product = dataSnapshot.getValue(Product.class);

                if (shoppingList.contains(product)) {
                    int index = shoppingList.indexOf(product);
                    shoppingList.remove(index);
                    if (product.getStatus() == 0) {
                        shoppingList.add(index, product);
                        rAdapterToDo.notifyDataSetChanged();
                        rAdapterDone.notifyDataSetChanged();
                    } else {
                        doneList.add(0, product);
                        rAdapterToDo.notifyDataSetChanged();
                        rAdapterDone.notifyDataSetChanged();
                    }
                    return;
                }

                if (doneList.contains(product)) {
                    int index = doneList.indexOf(product);
                    doneList.remove(index);
                    if (product.getStatus() == 0) {
                        shoppingList.add(0, product);
                        rAdapterToDo.notifyDataSetChanged();
                        rAdapterDone.notifyDataSetChanged();
                    } else {
                        doneList.add(index, product);
                        rAdapterToDo.notifyDataSetChanged();
                        rAdapterDone.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (shoppingList.contains(product)) {
                    shoppingList.remove(product);
                    rAdapterToDo.notifyDataSetChanged();
                    return;
                }
                if (doneList.contains(product)) {
                    doneList.remove(product);
                    rAdapterDone.notifyDataSetChanged();
                }
            }

            @Override  // not implemented
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // not using
            }

            @Override  // not implemented
            public void onCancelled(DatabaseError databaseError) {
                // not using
            }
        };

        listRef.removeEventListener(mChildEventListener);
        shoppingList.clear();
        doneList.clear();
        rAdapterToDo.notifyDataSetChanged();
        listRef.addChildEventListener(mChildEventListener);

    }

    public void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void loadSharedLists() {

        ChildEventListener sharedChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SharedList sharedList = dataSnapshot.getValue(SharedList.class);
                String sharedListName = sharedList.getSharedListName();
                String fromUserName = sharedList.getFromUserName();

                if (sharedList.getShowedToUser() == SharedList.NOT_YET_SHOWED) {
                    //String fromUserEmail = sharedList.getFromUserEmail();
                    String messageBody = fromUserName + " " +
                            getString(R.string.want_to_share_list) + sharedListName;
                    sendNotification(messageBody);
                    sharedList.setShowedToUser(SharedList.ALREADY_SHOWED);
                    databaseRef.child(userId).child("sharedlists").child(sharedList.getSharedListName()).setValue(sharedList);
                }

                if (!sharedLists.contains(sharedList)) {
                    sharedLists.add(sharedList);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                SharedList sharedList = dataSnapshot.getValue(SharedList.class);
                if (sharedLists.contains(sharedList)) {
                    sharedLists.remove(sharedList);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseRef.child(userId).child("sharedlists").removeEventListener(sharedChildEventListener);
        sharedLists.clear();
        databaseRef.child(userId).child("sharedlists").addChildEventListener(sharedChildEventListener);
    }

    private void databaseInitialize(User currentUser) {
        databaseRef = database.getReference("Users");
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", localUser.getEmail());
        updates.put("name", localUser.getName());
        updates.put("picUrl", localUser.getPicUrl());

        databaseRef.child(userId).updateChildren(updates);
    }

    @Override  // here we receive users input in Dialog and add it to shopping list
    public void getUserInput(String input, String quantity) {
        Product product = new Product(input, quantity, 0);

        String productId = product.getProductId();
        if (productId == null) {
            // get unique ID from firebase db
            productId = databaseRef.child(pathToList).push().getKey();
            product.setProductId(productId);
        }
        databaseRef.child(pathToList).child(productId).setValue(product);    //  FIREBASE +
    }

    @Override  // here we get name of new list (AddListDialog)
    public void getListNameInput(String input) {
        pathToList = userId + "/" + input;
        mapOfLists.put(input, input);
        loadData(pathToList);
        updateSpinner(input);
    }

    @Override  // here we get NEW name of OLD list (RenameListDialog)
    public void getNewListNameInput(String input) {
        listOfListsToDisplay.remove(mapOfLists.get(listName));
        mapOfLists.put(listName, input);
        setTitle(input);
        updateSpinner(input);
    }

    @Override   //getting user choice and update UI  (ChooseListDialog)
    public void getUserChoice(String listNameToDisplay) {
        for (String key : mapOfLists.keySet()) {
            if (mapOfLists.get(key).equals(listNameToDisplay)) {
                listName = key;
                break;
            }
        }
        setTitle(listNameToDisplay);
        pathToList = userId + "/" + listName;
        loadData(pathToList);
        updateSpinner(listNameToDisplay);
    }

    @Override // from ChooseSharedListDialog
    public void getUserSharedListChoice(String sharedList, String sharedUserID) {
        setTitle(sharedList);
        String sharedListName = sharedList.substring(0, sharedList.indexOf('(')).trim();
        pathToList = sharedUserID + "/" + sharedListName;
        loadData(pathToList);
        //updateSpinner(sharedList);
    }

    @Override // from Modify List Dialog
    public void getUserConfirm(String option) {

        if (option.equals(getResources().getString(R.string.delete_list))) {
            databaseRef.child(pathToList).removeValue();    //  FIREBASE +
            listOfListsToDisplay.remove(mapOfLists.get(listName));
            mapOfLists.remove(listName);

            if (mapOfLists.isEmpty()) {
                new AddListDialog().show(getFragmentManager(), "AddListDialog");
            } else {
                getUserChoice(listOfListsToDisplay.get(0));
            }
            return;
        } else if (option.equals(getResources().getString(R.string.clear_done))) {
            for (Product p : doneList) {
                databaseRef.child(pathToList).child(p.getProductId()).removeValue();
            }

            return;
        } else if (option.equals(getResources().getString(R.string.clear_all))) {
            databaseRef.child(pathToList).removeValue();

            return;
        }
    }

    private void updateSpinner(String input) {
        if (!listOfListsToDisplay.contains(input)) {
            listOfListsToDisplay.add(input);
        }
        spinnerAdapter.notifyDataSetChanged();
        chooseListSpinner.setSelection(listOfListsToDisplay.indexOf(input));
    }

    @Override  //from modify item dialog
    public void getItemModificationInput(Product p) {
        String newProductName = p.getName();
        if (newProductName != null) {
            databaseRef.child(pathToList).child(p.getProductId()).setValue(p);    //  FIREBASE +
        } else {
            databaseRef.child(pathToList).child(p.getProductId()).removeValue();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ModifyListDialog modifyDialog;
        switch (item.getItemId()) {
            case R.id.share:
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(this, R.string.must_sign_in_to_share, Toast.LENGTH_SHORT).show();
                    return false;
                }
                SharedList sharedList = new SharedList(listName, localUser.getName(), localUser.getEmail(), userId);
                Intent intent = new Intent(this, FindUserToShareList.class);
                intent.putExtra("sharedList", sharedList);
                startActivityForResult(intent, FIND_USER_TO_SHARE);

                return true;
            case R.id.rename:
                new RenameListDialog().show(getFragmentManager(), "RenameListDialog");
                return true;
            case R.id.sort_list:
                Collections.sort(shoppingList, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                rAdapterToDo.notifyDataSetChanged();
                return true;
            case R.id.delete:
                modifyDialog = new ModifyListDialog();
                modifyDialog.setOption(getResources().getString(R.string.delete_list));
                modifyDialog.show(getFragmentManager(), "ModifyListDialog");
                return true;
            case R.id.clear_done:
                modifyDialog = new ModifyListDialog();
                modifyDialog.setOption(getResources().getString(R.string.clear_done));
                modifyDialog.show(getFragmentManager(), "ModifyListDialog");
                return true;
            case R.id.clear_all:
                modifyDialog = new ModifyListDialog();
                modifyDialog.setOption(getResources().getString(R.string.clear_all));
                modifyDialog.show(getFragmentManager(), "ModifyListDialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivityForResult(new Intent(MainScreen.this, SettingsActivity.class), 0);
            return true;
        } else if (id == R.id.check_shared) {
            Log.d(TAG, "sharedLists.size =    " + sharedLists.size());
            if (sharedLists.isEmpty()) {
                Toast.makeText(this, R.string.no_list_available, Toast.LENGTH_SHORT).show();
                return true;
            }
            new ChooseSharedListDialog().show(getFragmentManager(), "ChooseSharedListDialog");
        } else if (id == R.id.log_out) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_RESULT);
            return true;

        } else if (id == R.id.new_list) {
            new AddListDialog().show(getFragmentManager(), "AddListDialog");
        } else if (id == R.id.choose_list) {
            new ChooseListDialog().show(getFragmentManager(), "ChooseListDialog");
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_RESULT: {
                    String userName = data.getStringExtra("userName");
                    String userMail = data.getStringExtra("userMail");
                    Uri userPic = data.getParcelableExtra("userPic");

                    String userPicLink = userPic == null ? null : userPic.toString();

                    localUser = new User(userName, userMail, userPicLink);
                    fillUserDatails(localUser);
                }
            }
        } else {
            fillDefaultUserDatails();
        }
    }

    private void fillDefaultUserDatails() {
        userPicView.setImageResource(R.mipmap.ic_launcher_round);
        userNameView.setText(R.string.app_name);
        userMailView.setText(R.string.make_shopping_easier);
        logMenuItem.setTitle(R.string.login);
    }

    private void fillUserDatails(User user) {
        userNameView.setText(user.getName());
        userMailView.setText(user.getEmail());
        if (user.getPicUrl() != null) {
            Picasso.get().load(user.getPicUrl()).transform(new CropCircleTransformation()).into(userPicView);
        } else {
            userPicView.setImageResource(R.mipmap.ic_launcher_round);
        }
        logMenuItem.setTitle(R.string.logout);
    }

    @Override
    protected void onPause() {  // need more tests
        super.onPause();
        // Remember data
        savePreferences();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            localUser = initializeLocalUser(currentUser);
            fillUserDatails(localUser);
        }
        preferenceChecker();
        Log.d(TAG, "onStart called   isAdsfree = " + isAdsfree + "  isAdsfreeForNow =   " + isAdsfreeForNow);
        Log.d(TAG, "listOfListsToDisplay = " + listOfListsToDisplay + "  mapOfLists =   " + mapOfLists);
    }

    private User initializeLocalUser(FirebaseUser currentUser) {
        String userName;
        String userMail;
        String picUrl;

        userName = currentUser.getDisplayName();
        if (userName == null || userName.equals("")) {
            userName = "NoNameUser";
        }
        userMail = currentUser.getEmail();
        if (userMail == null || userMail.equals("")) {
            userMail = mSettings.getString(APP_PREFERENCES_USER_EMAIL, getResources().getString(R.string.make_shopping_easier));
        }
        Uri uri = currentUser.getPhotoUrl();
        if (uri == null) {
            //need to check it out
            picUrl = Uri.parse("android.resource://com.shoppinglist.rdproject.shoppinglist/" + R.mipmap.ic_launcher_round).toString();
        } else {
            picUrl = uri.toString();
        }
        return new User(userName, userMail, picUrl);
    }

    @Override
    protected void onResume() {
        loadData(pathToList);
        preferenceChecker();
        super.onResume();
    }

    // to check out if lock screen mode on/off and lang
    private void preferenceChecker() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("lock_screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
        String localPref = sp.getString("language_settings", "100");
        String localCode;
        switch (localPref) {
            case "100":
                return;
            case "1":
                localCode = "en";
                break;
            case "0":
                localCode = "ru";
                break;
            case "2":
                localCode = "cs";
                break;
            default:
                localCode = "en";
        }
        setLocale(localCode);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    @Override
    protected void onDestroy() {
        // Remember data
        savePreferences();
        //sign out if required
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("log_out_on_exit", false) && mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
        super.onDestroy();
    }

    private void bindUserViews() {
        rViewToDo = findViewById(R.id.lis_to_do);
        rViewToDo.setHasFixedSize(true);
        rViewToDo.setLayoutManager(new LinearLayoutManager(this));
        rViewDone = findViewById(R.id.list_done);
        rViewDone.setHasFixedSize(true);
        rViewDone.setLayoutManager(new LinearLayoutManager(this));
        NavigationView mNavigationView = findViewById(R.id.nav_view_header_only);
        View mHeaderView = mNavigationView.getHeaderView(0);
        NavigationView navMenuView = findViewById(R.id.nav_view);
        logMenuItem = navMenuView.getMenu().findItem(R.id.log_out);
        userPicView = mHeaderView.findViewById(R.id.imageView);
        userNameView = mHeaderView.findViewById(R.id.user_name);
        userMailView = mHeaderView.findViewById(R.id.user_mail);
    }

    void savePreferences() {
        SharedPreferences.Editor ed = mSettings.edit();
        ed.putString(APP_PREFERENCES_LIST_NAME, listName);
        ed.putString(APP_PREFERENCES_MAP_OF_LISTS, saveMapToString(mapOfLists));
        ed.putBoolean(APP_PREFERENCES_IS_ADS_FREE, isAdsfree);
        ed.apply();
    }

    void loadPreferences() {
        if (mSettings.contains(APP_PREFERENCES_LIST_NAME)) {
            listName = mSettings.getString(APP_PREFERENCES_LIST_NAME, "Newlist1");
            String savedMap = mSettings.getString(APP_PREFERENCES_MAP_OF_LISTS, "");
            mapOfLists = getSavedMap(savedMap);
            isAdsfree = mSettings.getBoolean(APP_PREFERENCES_IS_ADS_FREE, false);
            Log.d("TEST", listName);
        } else {
            listName = "Newlist1";
            mapOfLists = new HashMap<>();
            mapOfLists.put(listName, listName);
        }
        preferenceChecker();
    }

    private Map<String, String> getSavedMap(String mapSavedToString) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(mapSavedToString);
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = (String) jsonObject.get(key);
                map.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String saveMapToString(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    public List<String> getListOfTablesToDisplay() {
        List<String> list = new ArrayList<>();
        for (String s : mapOfLists.keySet()) {
            list.add(mapOfLists.get(s));
            Log.d(TAG, "mapOfLists.get(s)  =  " + mapOfLists.get(s));
        }
        if (mapOfLists.get(listName) != null) {
            list.remove(mapOfLists.get(listName));
            list.add(0, mapOfLists.get(listName));
        }
        return list;
    }

    private Spinner createSpinner() {
        Spinner spinner = findViewById(R.id.spinner_list);
        spinnerAdapter = getSpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getUserChoice(listOfListsToDisplay.get(i));
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return spinner;
    }

    @NonNull
    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, listOfListsToDisplay);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public List<SharedList> getSharedLists() {
        return sharedLists;
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.shoppinglist.rdproject.shoppinglist",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
    }
}
