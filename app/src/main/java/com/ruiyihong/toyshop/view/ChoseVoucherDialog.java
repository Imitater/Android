package com.ruiyihong.toyshop.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.MinePreferentialActivity;
import com.ruiyihong.toyshop.activity.SettleActivity;
import com.ruiyihong.toyshop.bean.DjqBean;
import com.ruiyihong.toyshop.bean.PopAgeBean;
import com.ruiyihong.toyshop.bean.mine.MyPreferentialBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by 81521 on 2017/8/24.
 * 选择代金券对话框
 */

public class ChoseVoucherDialog extends Dialog {

    private static final String HB_IMAGE_BASE = "http://appadmin.y91edu.com/hbimage/";//红包图片路径
    private static ChoseVoucherDialog voucherDialog;
    private RecyclerView rv_djq;
    private ArrayList<Object> mDataList;
    private ImageButton back;
    private Button btn_ok;
    private int selectedPosition = -1;// 选中的位置
    private OnInsureButtonClickListener mListener;
    private int mDjqId=-1;
    private HashMap<Integer, Boolean> map_hasSelected;
    private boolean has_selected;//是否有选中的代金券

    public ChoseVoucherDialog(@NonNull Context context) {
        super(context);
        initView();
        initEvent();
    }
    public ChoseVoucherDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
        initEvent();
    }
    protected ChoseVoucherDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
        initEvent();
    }
    public static ChoseVoucherDialog getDialog(Context context){
        if (voucherDialog==null){
            voucherDialog = new ChoseVoucherDialog(context,R.style.Dialog_Find_Image);
        }
        return voucherDialog;
    }
    private void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.popup_seledjq_layout);
        rv_djq = findViewById(R.id.rv_djqPicker);
        back = findViewById(R.id.back);
        btn_ok = findViewById(R.id.btn_chose_djq_ok);

    }
    public void setData(ArrayList<Object> dataList, int mDjqID){
        this.mDataList = dataList;
        this.mDjqId = mDjqID;
        map_hasSelected = new HashMap<>();
        for (int i = 0; i < mDataList.size(); i++) {
            map_hasSelected.put(i,false);
        }

        rv_djq.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_djq.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST,1, getContext().getResources().getColor(R.color.divider_line)));
        rv_djq.setAdapter(new DjqPickerAdapter());
    }

    private void initEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < map_hasSelected.size(); i++) {
                    Boolean aBoolean = map_hasSelected.get(i);
                    if (aBoolean==true){
                        has_selected = true;
                        break;
                    }
                }
                if (!has_selected){
                    selectedPosition=-1;
                }
                if (selectedPosition!=-1){
                    Object o = mDataList.get(selectedPosition);
                    if (o instanceof MyPreferentialBean.Data1Bean ){
                        //红包
                        MyPreferentialBean.Data1Bean dataBean = (MyPreferentialBean.Data1Bean) o;
                        if (mListener!=null){
                            LogUtil.e("选择id==="+dataBean.getId());
                            mListener.onInsureButtonClick(dataBean.getId(),dataBean.getPnum());
                        }

                    }else if (o instanceof  MyPreferentialBean.DataBean){
                        //代金券
                        MyPreferentialBean.DataBean dataBean = (MyPreferentialBean.DataBean) o;
                        if (mListener!=null){
                            LogUtil.e("选择id==="+dataBean.getId());
                            mListener.onInsureButtonClick(dataBean.getId(),dataBean.getDmian());
                        }
                    }
                   // LogUtil.e("选择了代金券id= " + item.getId() + " 面额为 " + item.getDmian());


                }else {
                    if (mListener!=null){
                        mListener.onInsureButtonClick(-1,-1);
                    }
                }
            }
        });
    }
    class DjqPickerAdapter extends RecyclerView.Adapter<DjqPickerAdapter.mDjqPickerHolder> {
        @Override
        public mDjqPickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new mDjqPickerHolder(LayoutInflater.from(getContext()).inflate(R.layout.djqselect_item_layout, parent,false));
        }

        @Override
        public void onBindViewHolder(final mDjqPickerHolder holder, final int position) {
            Object o = mDataList.get(position);
            if (o instanceof MyPreferentialBean.Data1Bean ) {
                //红包
                MyPreferentialBean.Data1Bean item = (MyPreferentialBean.Data1Bean) o;
                Picasso.with(getContext()).load(HB_IMAGE_BASE + item.getWdhb()).placeholder(R.mipmap.lunbo_default).into(holder.img);
                holder.cb_djq.setTag(position);
                if (item.getId()==mDjqId){
                    holder.cb_djq.setChecked(true);
                }
                holder.cb_djq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            //选中
                            int positon = (int) holder.cb_djq.getTag();
                            if (positon!=ChoseVoucherDialog.this.selectedPosition ) {
                                ChoseVoucherDialog.this.selectedPosition = positon;
                                notifyDataSetChanged();
                            }
                            map_hasSelected.put(position,true);
                        }else{
                            map_hasSelected.put(position,false);
                        }
                        mDjqId = -1;
                    }
                });

                if (position==selectedPosition ){
                    holder.cb_djq.setChecked(true);
                }else if (position!=selectedPosition && mDjqId==-1){
                    holder.cb_djq.setChecked(false);
                }
            }
            else if (o instanceof MyPreferentialBean.DataBean){
                //代金券
                MyPreferentialBean.DataBean item = (MyPreferentialBean.DataBean) o;
                Picasso.with(getContext()).load(AppConstants.IMG_BASE_URL + item.getDimg()).placeholder(R.mipmap.lunbo_default).into(holder.img);
                holder.cb_djq.setTag(position);
                if (item.getId()==mDjqId){
                    holder.cb_djq.setChecked(true);
                }
                holder.cb_djq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            //选中
                            int positon = (int) holder.cb_djq.getTag();
                            if (positon!=ChoseVoucherDialog.this.selectedPosition ) {
                                ChoseVoucherDialog.this.selectedPosition = positon;
                                notifyDataSetChanged();
                            }
                            map_hasSelected.put(position,true);
                        }else{
                            map_hasSelected.put(position,false);
                        }
                        mDjqId = -1;
                    }
                });

                if (position==selectedPosition ){
                    holder.cb_djq.setChecked(true);
                }else if (position!=selectedPosition && mDjqId==-1){
                    holder.cb_djq.setChecked(false);
                }
            }


        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        class mDjqPickerHolder extends RecyclerView.ViewHolder {

            private ImageView img;
            private CheckBox cb_djq;

            public mDjqPickerHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.iv_djq_sel_img);
                cb_djq = itemView.findViewById(R.id.cb_djq);
            }
        }
    }
    public interface OnInsureButtonClickListener{
        void onInsureButtonClick(int djqId, int djqMian);
    }
    public void setOnInsureButtonClickListener(OnInsureButtonClickListener listener){
        this.mListener = listener;
    }
}
