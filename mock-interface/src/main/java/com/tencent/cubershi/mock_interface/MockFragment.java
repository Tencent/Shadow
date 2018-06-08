package com.tencent.cubershi.mock_interface;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileDescriptor;
import java.io.PrintWriter;

@SuppressWarnings("unused")
public class MockFragment {
    public MockFragment() {

    }

    public void setArguments(Bundle args) {

    }


    public void setInitialSavedState(Fragment.SavedState state) {

    }


    public void setTargetFragment(Fragment fragment, int requestCode) {

    }


    public Context getContext() {
        return null;
    }


    public void onHiddenChanged(boolean hidden) {

    }


    public void setRetainInstance(boolean retain) {

    }


    public void setHasOptionsMenu(boolean hasMenu) {

    }


    public void setMenuVisibility(boolean menuVisible) {

    }


    public void setUserVisibleHint(boolean isVisibleToUser) {

    }


    public boolean getUserVisibleHint() {
        return false;
    }


    public LoaderManager getLoaderManager() {
        return null;
    }


    public void startActivity(Intent intent) {

    }


    public void startActivity(Intent intent, Bundle options) {

    }


    public void startActivityForResult(Intent intent, int requestCode) {

    }


    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {

    }


    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }


    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }


    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        return null;
    }


    public void onInflate(AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onAttachFragment(Fragment childFragment) {

    }


    public void onAttach(Context context) {

    }


    public void onAttach(Activity activity) {

    }


    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return null;
    }


    public void onCreate(Bundle savedInstanceState) {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


    public View getView() {
        return null;
    }


    public void onActivityCreated(Bundle savedInstanceState) {

    }


    public void onViewStateRestored(Bundle savedInstanceState) {

    }


    public void onStart() {

    }


    public void onResume() {

    }


    public void onSaveInstanceState(Bundle outState) {

    }


    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {

    }


    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {

    }


    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

    }


    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {

    }


    public void onConfigurationChanged(Configuration newConfig) {

    }


    public void onPause() {

    }


    public void onStop() {

    }


    public void onLowMemory() {

    }


    public void onTrimMemory(int level) {

    }


    public void onDestroyView() {

    }


    public void onDestroy() {

    }


    public void onDetach() {

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    public void onPrepareOptionsMenu(Menu menu) {

    }


    public void onDestroyOptionsMenu() {

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    public void onOptionsMenuClosed(Menu menu) {

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }


    public void registerForContextMenu(View view) {

    }


    public void unregisterForContextMenu(View view) {

    }


    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }


    public void setEnterSharedElementCallback(SharedElementCallback callback) {

    }


    public void setExitSharedElementCallback(SharedElementCallback callback) {

    }


    public void setEnterTransition(Transition transition) {

    }


    public Transition getEnterTransition() {
        return null;
    }


    public void setReturnTransition(Transition transition) {

    }


    public Transition getReturnTransition() {
        return null;
    }


    public void setExitTransition(Transition transition) {

    }


    public Transition getExitTransition() {
        return null;
    }


    public void setReenterTransition(Transition transition) {

    }


    public Transition getReenterTransition() {
        return null;
    }


    public void setSharedElementEnterTransition(Transition transition) {

    }


    public Transition getSharedElementEnterTransition() {
        return null;
    }


    public void setSharedElementReturnTransition(Transition transition) {

    }


    public Transition getSharedElementReturnTransition() {
        return null;
    }


    public void setAllowEnterTransitionOverlap(boolean allow) {

    }


    public boolean getAllowEnterTransitionOverlap() {
        return false;
    }


    public void setAllowReturnTransitionOverlap(boolean allow) {

    }


    public boolean getAllowReturnTransitionOverlap() {
        return false;
    }


    public void postponeEnterTransition() {

    }


    public void startPostponedEnterTransition() {

    }


    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {

    }
}
