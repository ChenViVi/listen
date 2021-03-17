package com.yellowzero.listen.observer;

import android.app.Activity;
import android.text.TextUtils;

import com.allen.library.base.BaseObserver;
import com.allen.library.bean.BaseData;
import com.allen.library.utils.ToastUtils;

import java.lang.ref.WeakReference;

import androidx.fragment.app.Fragment;
import io.reactivex.disposables.Disposable;

/**
 * Created by Allen on 2017/10/31.
 *
 * @author Allen
 * <p>
 * 针对特定格式的时候设置的通用的Observer
 * 用户可以根据自己需求自定义自己的类继承BaseDataObserver<T>即可
 * 适用于
 * {
 * "code":200,
 * "msg":"成功"
 * "data":{
 * "userName":"test"
 * "token":"abcdefg123456789"
 * "uid":"1"}
 * }
 */

public class DataObserver<T> extends BaseObserver<BaseData<T>> {

    private boolean initWithActivity = false;
    private boolean initWithFragment = false;
    private WeakReference<Activity> activity;
    private WeakReference<Fragment> fragment;

    public DataObserver() { }

    public DataObserver(Activity activity) {
        this.activity = new WeakReference<>(activity);
        initWithActivity = true;
    }

    public DataObserver(Fragment fragment) {
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
            if (!initWithActivity && !initWithFragment)
                ToastUtils.showToast(errorMsg);
            else if (initWithActivity
                    && activity != null
                    && activity.get()!= null
                    && activity.get().isFinishing())
                ToastUtils.showToast(errorMsg);
            else if (initWithFragment
                    && fragment != null
                    && fragment.get() != null
                    && fragment.get().getActivity() != null
                    && fragment.get().getActivity().isFinishing())
                ToastUtils.showToast(errorMsg);
    }

    /**
     * 成功回调
     *
     * @param data 结果
     */
    protected void onSuccess(T data) {

    }

    @Override
    public void doOnSubscribe(Disposable d) {
    }

    @Override
    public void doOnError(String errorMsg) {
        onError(errorMsg);
    }

    @Override
    public void doOnNext(BaseData<T> data) {
        if (data.getCode() == 200)
            if (!initWithActivity && !initWithFragment)
                onSuccess(data.getData());
            else if (initWithActivity
                    && activity != null
                    && activity.get()!= null
                    && !activity.get().isFinishing())
                onSuccess(data.getData());
            else if (initWithFragment
                    && fragment != null
                    && fragment.get() != null
                    && fragment.get().getActivity() != null
                    && !fragment.get().getActivity().isFinishing())
                onSuccess(data.getData());
        else
            onError(data.getMsg());
    }

    @Override
    public void doOnCompleted() {
    }
}
