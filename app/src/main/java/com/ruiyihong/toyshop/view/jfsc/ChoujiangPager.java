package com.ruiyihong.toyshop.view.jfsc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.bean.JianpinBean;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.GsonUtil;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.OkHttpUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 81521 on 2017/8/21.
 * 积分抽奖页面
 */

public class ChoujiangPager {

    private static final int MSG_JIANG = 0;
    private static final int MSG_GET_PIC = 1;
    private static final int MSG_LOTTERY_SUCESS = 2;
    private static final String JIANG_BUFFER = "jiang_buffer";
    private final Context mContext;
    public View rootView;
    private LotteryView mLotteryView;
    private String Image_base = "http://appadmin.y91edu.com/hbimage/";
    private int count = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_JIANG:
                    parseJianpin((String) msg.obj);
                    break;
                case MSG_GET_PIC:
                    LogUtil.e("Msg_get_pic");
                    initData();
                    break;
                case MSG_LOTTERY_SUCESS:
                    String string = (String) msg.obj;
                    String[] split = string.split(",");
                    showDialog(split[1],split[0]);
                    break;
            }
        }
    };
    private String[] prizesInfo;
    private ArrayList<Bitmap> bitmaps;
    private List<JianpinBean.DataBean> data;
    private OnDataGotListener listener;
    private AlertDialog sucessDialog;
    private int xhjf;

    private void parseJianpin(String json) {

        JianpinBean jianpinBean = GsonUtil.parseJsonWithGson(json, JianpinBean.class);
        if (jianpinBean != null) {
            xhjf = jianpinBean.getXhjifen();

            data = jianpinBean.getData();
            if (data != null) {

                prizesInfo = new String[data.size()];//奖品信息
                bitmaps = new ArrayList<>();

                for (int i = 0; i < data.size(); i++) {
                    prizesInfo[i] = "红包" + data.get(i).getPnum() + "元";
                    bitmaps.add(null);
                    try {
                        getPic(Image_base + data.get(i).getPic(), i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void getPic(String url, final int i) throws IOException {
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.code() == 200) {
                    InputStream is = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (bitmaps!=null) {
                        bitmaps.set(i, bitmap);
                    }
                    count++;
                    if (count ==data.size()) {
                        handler.sendEmptyMessage(MSG_GET_PIC);
                    }
                }
            }
        });
    }
    public ChoujiangPager(Context context) {
        this.mContext = context;
        String buffer = SPUtil.getString(mContext, JIANG_BUFFER, "");
        if (!TextUtils.isEmpty(buffer)){
            //有缓存
            parseJianpin(buffer);
        }
        try {
            //从服务器获取奖品
            getDataFromNet();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getDataFromNet() throws IOException {
        String url = AppConstants.SERVE_URL + "index/prize/cjym";
        OkHttpUtil.get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("奖品设置===" + result);

                if (result != null) {
                    SPUtil.setString(mContext,JIANG_BUFFER,result);//缓存
                    Message msg = Message.obtain();
                    msg.what = MSG_JIANG;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }

            }
        });
    }

    public void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.page_choujiang, null);
        mLotteryView = rootView.findViewById(R.id.nl);

        mLotteryView.mHolder.setFormat(PixelFormat.TRANSLUCENT);

        if (listener!=null){
            listener.onDataGot();
        }
    }

    public void initData() {
        //// TODO: 2017/9/15  奖品设置中返回 奖品 几等奖之后，按照奖品等级设置权重，来控制中奖几率
        // int[] prizesIcon={R.drawable.hongbao,R.drawable.hongbao,R.drawable.hongbao,R.drawable.tutu,R.drawable.jf_lkcj,R.drawable.hongbao,R.drawable.hongbao,R.drawable.hongbao,R.drawable.tutu};
        // String[] prizesInfo = {"红包2元","红包2元","红包2元","兔兔么么哒","","红包2元","红包2元","红包2元","兔兔么么哒"};
        final List<Prize> prizes = new ArrayList<Prize>();
        for (int x = 0; x < 9; x++) {
            Prize lottery = new Prize();
            lottery.setId(x + 1);
            lottery.setName("Lottery" + (x + 1));
           // Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), prizesIcon[x]);

            if (x<=3) {
                lottery.setIcon(bitmaps.get(x));
                lottery.setInfo(prizesInfo[x]);
                lottery.setId(data.get(x).getId());
                lottery.setWeight(getWeight(data.get(x).getJiang()));//todo 假数据，待改
                lottery.setJiang_dengji(data.get(x).getJiang());
            }
            if (x==4){
                //中间位不需要奖品
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.jf_lkcj);
                lottery.setIcon(bitmap);
                lottery.setInfo("");
                lottery.setId(-1);
                lottery.setWeight(0);//todo 假数据，待改
                lottery.setJiang_dengji(0);
            }
            if (x>4){
                lottery.setIcon(bitmaps.get(x-1));
                lottery.setInfo(prizesInfo[x-1]);
                lottery.setId(data.get(x-1).getId());
                lottery.setWeight(getWeight(data.get(x-1).getJiang()));//todo 假数据，待改
                lottery.setJiang_dengji(data.get(x-1).getJiang());
            }
            if ((x + 1) % 2 == 0) {
                lottery.setBgColor(0xfffdeded);//#fdeded
            } else if (x == 4) {
                lottery.setBgColor(0xfffcd601);//fcd601
            } else {
                lottery.setBgColor(0xfffdeded);
            }

            prizes.add(lottery);
        }

        initView();

        mLotteryView.setJF(xhjf);

        //释放bitmap
        bitmaps.clear();
        bitmaps = null;
        mLotteryView.setPrizes(prizes);
        mLotteryView.setOnTransferWinningListener(new LotteryView.OnTransferWinningListener() {

            @Override
            public void onWinning(int position) {
                //中奖的回调
                int id = prizes.get(position).getId();
                String info = prizes.get(position).getInfo();
                LogUtil.e("奖品id"+prizes.get(position).getWeight());
                try {
                    postNet(id,info);
                } catch (IOException e) {
                    ToastHelper.getInstance().displayToastShort("系统繁忙，请稍后再试！");
                    e.printStackTrace();
                }
            }
        });
    }

    private int getWeight(int jiang) {
        if (jiang ==1){
            return 2;
        }else if (jiang ==2){
            return 3;

        }else if (jiang ==3){
            return 5;

        }else if (jiang ==4){
            return 5;

        }else if (jiang ==5){
            return 5;

        }else if (jiang ==6){

            return 5;
        }else if (jiang ==7){

            return 5;
        }else if (jiang ==8){

            return 70;
        }else {
            return jiang / 100;
        }
    }

    private void postNet(final int id, final String info) throws IOException {
        String url = AppConstants.SERVE_URL+"index/prize/cjzj";
        HashMap<String, String> map = new HashMap<>();
        String[] uids = SPUtil.getUid(mContext);
        String uid = "";
        if (uids!=null){
            uid = uids[0];
        }
        map.put("uid",uid);
        map.put("qid",id+"");

        OkHttpUtil.postString(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = OkHttpUtil.getResult(response);
                LogUtil.e("抽奖结果===="+result);
                if (result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        Message msg = Message.obtain();
                        String statusAndInfo = status+","+info;
                        msg.obj = statusAndInfo;
                        msg.what = MSG_LOTTERY_SUCESS;
                        handler.sendMessage(msg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void showDialog(String info, String status) {
        String message = "";
        if ("1".equals(status)){
            //抽奖成功
            message  = "恭喜您抽中"+info+",红包将放入您的优惠券中,请在我的-我的优惠券中查看,注意红包的有效期限,及时使用";
        }else if ("-1".equals(status)){
            //抽奖失败
            message  = "网络繁忙，请稍后再试";
        }else if ("0".equals(status)){
            //积分不足
            message  = "对不起，您的积分不足";
        }else if ("-2".equals(status)){
            //当日已抽过
            message  = "您今天没有抽奖机会了，明天再来吧，积分已退回您的账户";
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sucessDialog!=null){
                    sucessDialog.dismiss();
                }
            }
        });
        sucessDialog = builder.create();
        sucessDialog.show();
    }

    public interface OnDataGotListener{
        void onDataGot();
    }
    public void setOnDataGotListener(OnDataGotListener listener){
        this.listener = listener;
    }

}
