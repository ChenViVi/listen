package com.yellowzero.listen.observer;

import android.app.Activity;
import android.text.TextUtils;

import com.allen.library.base.BaseObserver;
import com.allen.library.utils.ToastUtils;

import java.lang.ref.WeakReference;

import androidx.fragment.app.Fragment;
import io.reactivex.disposables.Disposable;


/**
 * Created by Allen on 2017/10/31.
 *
 * @author Allen
 * <p>
 * 自定义Observer 处理string回调
 */

public abstract class StringObserver extends BaseObserver<String> {

    private boolean initWithActivity = false;
    private boolean initWithFragment = false;
    private WeakReference<Activity> activity;
    private WeakReference<Fragment> fragment;

    public StringObserver() { }

    public StringObserver(Activity activity) {
        this.activity = new WeakReference<>(activity);
        initWithActivity = true;
    }

    public StringObserver(Fragment fragment) {
        this.fragment = new WeakReference<>(fragment);
        initWithFragment = true;
    }

    /**
     * 失败回调
     *
     * @param errorMsg 错误信息
     */
    protected void onError(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg))
            ToastUtils.showToast(errorMsg);
    }

    /**
     * 成功回调
     *
     * @param data 结果
     */
    protected void onSuccess(String data) {

    }

    @Override
    public void doOnSubscribe(Disposable d) {
    }

    @Override
    public void doOnError(String errorMsg) {
        onError(errorMsg);
    }

    @Override
    public void doOnNext(String string) {
        if (!initWithActivity && !initWithFragment)
            onSuccess(string);
        else if (initWithActivity
                && activity != null
                && activity.get()!= null
                && activity.get().isFinishing())
            onSuccess(string);
        else if (initWithFragment
                && fragment != null
                && fragment.get() != null
                && fragment.get().getActivity() != null
                && fragment.get().getActivity().isFinishing())
            onSuccess(string);
    }

    @Override
    public void doOnCompleted() {
    }
}
