package com.ruiyihong.toyshop.view.find;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.find.FindCommentBean;
import com.ruiyihong.toyshop.bean.find.FindHotTuijianBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/8/10.
 * 发现的评论布局
 */

public class FindPlView {

    private static final int COMMENT_LIST_DATA = 0;
    private static final int MSG_SEND_COMMENT = 1;
    private static final int DELETE_COMMENT = 2;
    private static final int DELETE_COMMENT_FAIL = 3;
    private final Activity mContext;
    private final int mTid;//热门推荐id
    private final String mUid;//用户昵称
    private final TextView tv_pinglun;
    private final FindHotTuijianBean.ListBean listBean;
    public View mRootView;
    private RecyclerView rv_pinglun;
    private EditText et_find_pinglun;
    private TextView tv_send;
    private List<FindCommentBean.DataBean> commentList;
    private CommentAdapter commentAdapter;
    private PopupWindow pop;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case COMMENT_LIST_DATA:
                   String result = (String) msg.obj;
                   parseCommentData(result);
                   break;
               case MSG_SEND_COMMENT:
                   if (commentAdapter==null){
                       commentAdapter = new CommentAdapter();
                       rv_pinglun.setLayoutManager(new LinearLayoutManager(mContext));
                       rv_pinglun.setAdapter(commentAdapter);
                       //清空edittext
                       et_find_pinglun.setText("");
                       //刷新评论数量
                       tv_pinglun.setText("评论 ("+(listBean.getPlnum()+1)+")");
                   }else{
                       commentAdapter.notifyDataSetChanged();
                       //清空edittext
                       et_find_pinglun.setText("");
                       //刷新评论数量
                       tv_pinglun.setText("评论 ("+(listBean.getPlnum()+1)+")");
                   }
                   break;
               case DELETE_COMMENT:
                   int position = msg.arg1;
                   commentList.remove(position);
                   commentAdapter.notifyDataSetChanged();
                   //刷新评论数量
                   int i = listBean.getPlnum() - 1;
                   if (i<0){
                       i=0;
                   }
                   tv_pinglun.setText("评论 ("+i+")");
                   if (pop!=null){
                       pop.dismiss();
                   }
                   break;
               case DELETE_COMMENT_FAIL:
                   ToastHelper.getInstance().displayToastShort("删除失败，请稍后再试");
                   break;
           }
        }
    };
    public FindPlView(Activity context, int tid, String pname, TextView tv_pinglun, FindHotTuijianBean.ListBean listBean){
        this.mContext = context;
        this.mTid = tid;
        this.mUid = pname;
        this.tv_pinglun = tv_pinglun;
        this.listBean = listBean;
        initView();
        initData();
        initEvent();

    }
    private void initView(){
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.find_pinglun, null);
        rv_pinglun = mRootView.findViewById(R.id.rv_find_pinglun);
        et_find_pinglun = mRootView.findViewById(R.id.et_find_pinglun);
        tv_send = mRootView.findViewById(R.id.tv_send);

    }
    private void initData(){
        String result = SPUtil.getString(mContext, mTid+"_COMMENT", "");
        if (!TextUtils.isEmpty(result)){
            parseCommentData(result);
        }
        //获取评论
        // par: tid(热门推荐id)
        HashMap<String, String> map = new HashMap<>();
        map.put("tid",mTid+"");
        try {
            OkHttpUtil.postString(AppConstants.FIND_HOTTUIJIAN_PINGLUN_LIST, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = OkHttpUtil.getResult(response);
                    if (result!=null){
                        //评论列表
                        LogUtil.e("评论列表======"+result);
                        //缓存数据
                        SPUtil.setString(mContext,mTid+"_COMMENT",result);
                        Message msg = Message.obtain();
                        msg.what = COMMENT_LIST_DATA;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCommentData(String result) {
        FindCommentBean bean = GsonUtil.parseJsonWithGson(result, FindCommentBean.class);
        commentList = bean.getData();
        if (commentList!=null && commentList.size()>0) {
            rv_pinglun.setLayoutManager(new LinearLayoutManager(mContext));
            commentAdapter = new CommentAdapter();
            rv_pinglun.setAdapter(commentAdapter);
        }
    }

    private void initEvent() {
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        final String comment = et_find_pinglun.getText().toString().trim();
        HashMap<String, String> map = new HashMap<>();
        map.put("tid",mTid+"");
        map.put("uid",mUid);
        map.put("pcontent",comment);
        try {
            OkHttpUtil.postString(AppConstants.FIND_HOTTUIJIAN_PINGLUN, map, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //评论的数据
                    String result = OkHttpUtil.getResult(response);
                    LogUtil.e("发表评论======"+result);
                    if (result!=null){
                        try {
                            JSONObject object = new JSONObject(result);
                            String msg = object.getString("msg");
                            int id = object.getInt("id");
                            if (msg!=null && msg.contains("成功")){
                                FindCommentBean.DataBean dataBean = new FindCommentBean.DataBean();
                                dataBean.setPcontent(comment);
                                dataBean.setUid(SPUtil.getUid(mContext)[0]);//uid

                                dataBean.setId(id);//该条评论的id
                                //SPUtil
                                dataBean.setPname(SPUtil.getUid(mContext)[2]);//昵称
                                dataBean.setTid(mTid);
                                if (commentList==null) {
                                    commentList = new ArrayList<>();
                                }
                                commentList.add(0,dataBean);
                                //刷新界面
                                handler.sendEmptyMessage(MSG_SEND_COMMENT);
                            }else{
                                ToastHelper.getInstance().displayToastShort("评论失败，请稍后再试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment_list, parent,false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv_pname.setText(commentList.get(position).getPname()+":");
            holder.tv_content.setText(commentList.get(position).getPcontent());

            holder.ll_comment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //判断是否是自己的评论
                    String uid = commentList.get(position).getUid();
                    LogUtil.e("uid==========="+uid);
                    if (listBean!=null && uid.equals(mUid)){
                        int id = commentList.get(position).getId();
                        LogUtil.e("评论的id==="+id);
                        holder.ll_comment.setBackgroundColor(Color.alpha(Color.GRAY));
                        showDelePop(view,position,id);
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tv_pname;
            private TextView tv_content;
            private LinearLayout ll_comment;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_pname = itemView.findViewById(R.id.tv_pname);
                tv_content = itemView.findViewById(R.id.tv_content);
                ll_comment = itemView.findViewById(R.id.ll_comment);

            }
        }
    }

    private void showDelePop(View view, final int position, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否删除您的该条评论？");
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                       deleteComment(position,id);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
            }
        });
        builder.setPositiveButton("取消",null);
        builder.show();
    }
    private void deleteComment(final int position, int id) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        LogUtil.e("删除评论==uid"+mUid);
        map.put("pid",id+"");
        map.put("uid",mUid);
        OkHttpUtil.postString(AppConstants.FIND_HOTTUIJIAN_DELETE_PINLUN, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("删除评论==="+result);
                if (result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String msg = jsonObject.getString("msg");
                        Message message = Message.obtain();
                        if (msg.contains("成功")){
                            message.what = DELETE_COMMENT;
                            message.arg1 = position;
                        }else{
                            message.what = DELETE_COMMENT_FAIL;
                        }
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}