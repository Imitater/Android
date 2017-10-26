package com.ruiyihong.toyshop.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/8/20.
 * 是否兑换代金券/图书/玩具
 */

public class DuihuanDialogFragment extends DialogFragment {

    private static final int EXCHANGE_DJQ = 0;
    private Button btn_ok;
    private Button btn_cancel;
    private int mQid = -1;
    private ExChangeFinishListener listener;

    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            switch (msg.what){
                case EXCHANGE_DJQ:
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String result_msg = jsonObject.getString("msg");
                        ToastHelper.getInstance().displayToastShort(result_msg);
                        if (listener!=null){
                            listener.onExChangeSucess();
                        }
                        dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_dialog_insure_duihuan, null);
        btn_ok = view.findViewById(R.id.btn_jifenduihuan_yes);
        btn_cancel = view.findViewById(R.id.btn_jifenduihuan_no);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mQid!=-1) {
                    exChange(mQid);
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setData(final  int qid) {
       this.mQid = qid;
    }

    private void exChange(int qid) {
        //兑换
        String[] uids = SPUtil.getUid(getActivity());
        if (uids == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("uid", uids[0]);
                map.put("qid", qid+"");
                OkHttpUtil.postString(AppConstants.JIFEN_DUIHUAN_EXCHANGE_DJQ, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (listener!=null){
                            listener.onExChangeFailure();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = OkHttpUtil.getResult(response);
                        if (!TextUtils.isEmpty(result)){
                            LogUtil.e("兑换代金券"+result);
                            Message msg = Message.obtain();
                            msg.obj = result;
                            msg.what = EXCHANGE_DJQ;
                            handler.sendMessage(msg);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ExChangeFinishListener{
        void onExChangeSucess();
        void onExChangeFailure();
    }
    public void setExchangeFinishListener(ExChangeFinishListener listener){
        this.listener = listener;
    }
}
