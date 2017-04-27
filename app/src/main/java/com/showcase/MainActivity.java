package com.showcase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.showcase.adapter.SlideMenuAdapter;
import com.showcase.fragments.CameraFragment2;
import com.showcase.fragments.GalleryFragment;
import com.showcase.fragments.VideoFragment;
import com.showcase.fragments.VideoFragment2;
import com.showcase.helper.UIHelper;
import com.showcase.model.SlideData;

import java.util.ArrayList;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements SlideMenuAdapter.SlideMenuAdapterInterface {

    private Context mContext;
    private Toolbar toolbar;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment currentFragment = null;

    private ListView slidingList;
    private SlideMenuAdapter mSlideMenuAdapter;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mContext = MainActivity.this;
        initializeActionBar();
        initialCalling();

    }

    @Override
    public void onBackPressed() {
        if (Drawer.isDrawerOpen(Gravity.LEFT)) {
            Drawer.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemDelete, itemShare, itemDeselect;
        getMenuInflater().inflate(R.menu.main, menu);
        itemDeselect = menu.findItem(R.id.action_unSelect);
        itemDeselect.setVisible(false);

        itemShare = menu.findItem(R.id.action_shareImages);
        itemShare.setVisible(false);

        itemDelete = menu.findItem(R.id.action_deleteImages);
        itemDelete.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.action_changePassword) {
            Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
            boolean isFromOption = true;
            intent.putExtra(AppConstants.INTENT_IS_FROM_OPTION, isFromOption);
            intent.putExtra(AppConstants.INTENT_PRIVACY_SETTING, AppConstants.PrivacySetting.changePassword);
            UIHelper.fireIntent(MainActivity.this, intent, true);
        }
        if (id == R.id.action_removePassword) {
            Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
            boolean isFromOption = true;
            intent.putExtra(AppConstants.INTENT_IS_FROM_OPTION, isFromOption);
            intent.putExtra(AppConstants.INTENT_PRIVACY_SETTING, AppConstants.PrivacySetting.removePassword);
            UIHelper.fireIntent(MainActivity.this, intent, true);
        }
*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void slideRowClickEvent(int postion) {
        if (currentPosition == postion) {
            closeDrware();
            return;
        }
        currentPosition = postion;
        getFragment(postion);
        attachedFragment();
    }

    private void initializeActionBar() {
        UIHelper.initToolbar(MainActivity.this, toolbar, "");
        slidingList = (ListView) findViewById(R.id.sliding_listView);
        mSlideMenuAdapter = new SlideMenuAdapter(mContext, getSlideList());
        mSlideMenuAdapter.setSlidemenuadapterinterface(this);
        slidingList.setAdapter(mSlideMenuAdapter);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar,
                R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


    }

    private void closeDrware() {
        if (Drawer.isDrawerOpen(Gravity.LEFT)) {
            Drawer.closeDrawer(Gravity.LEFT);
        }
    }

    private void initialCalling() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        getFragment(0);
        attachedFragment();
    }


    private void attachedFragment() {
        try {
            if (currentFragment != null) {
                if (fragmentTransaction.isEmpty()) {
                    fragmentTransaction.add(R.id.fragment_container, currentFragment, "" + currentFragment.toString());
                    fragmentTransaction.commit();
                    toolbar.setTitle(title[currentPosition]);
                } else {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, currentFragment, "" + currentFragment.toString());
                    fragmentTransaction.commit();
                    toolbar.setTitle(title[currentPosition]);
                }

            }
            closeDrware();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getFragment(int postion) {
        switch (postion) {
            case 0:
                currentFragment = new GalleryFragment();
                break;
            case 1:
                currentFragment = new CameraFragment2();
                break;
            case 2:
                currentFragment = new VideoFragment2();
                break;

            default:
                break;
        }
    }


    /**
     * Slide Menu List Array.
     */
    private String[] title = {"All Images", "Camera", "Video"};
    private int[] titleLogo = {R.drawable.ic_image_gallery, R.drawable.ic_camera_alt, R.drawable.ic_video_cam};

    private ArrayList<SlideData> getSlideList() {
        ArrayList<SlideData> arrayList = new ArrayList<SlideData>();
        for (int i = 0; i < title.length; i++) {
            SlideData mSlideData = new SlideData();
            mSlideData.setIcon(titleLogo[i]);
            mSlideData.setName(title[i]);
            mSlideData.setState((i == 0) ? 1 : 0);
            arrayList.add(mSlideData);
        }
        return arrayList;
    }

}
