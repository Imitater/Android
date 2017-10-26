package com.ruiyihong.toyshop.view.jfsc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.LoginActivity;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ToastHelper;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LotteryView extends SurfaceView implements Callback{

    /**
     * holder
     */
    public SurfaceHolder mHolder;


    private List<Prize>prizes;
    private boolean flags;

    private int lottery=6;   //设置中奖号码

    private int current=2;   //抽奖开始的位置

    private int count=0;   //旋转次数累计

    private int countDown;    //倒计次数，快速旋转完成后，需要倒计多少次循环才停止


    private int transfer= 0xfffbfa2c;

    private int MAX=50;   //最大旋转次数

    private OnTransferWinningListener listener;
    private int jifen = -1;

    public void setOnTransferWinningListener(OnTransferWinningListener listener){
        this.listener=listener;
    }
    public void setJF(int jifen){
        this.jifen = jifen;
    }

    public interface OnTransferWinningListener{
        /**
         * 中奖回调
         * @param position
         */
        void onWinning(int position);
    }


    /**
     * 设置中奖号码
     * @param lottery
     */
    public void setLottery(int lottery) {
        if(prizes!=null&&Math.round(prizes.size()/2)==0){
            throw new RuntimeException("开始抽奖按钮不能设置为中奖位置！");
        }
        this.lottery = lottery;
    }

    /**
     * 设置转盘颜色
     * @param transfer
     */
    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    /**
     * 设置奖品集合
     * @param prizes
     */
    public void setPrizes(List<Prize>prizes){
        this.prizes=prizes;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event);
        return super.onTouchEvent(event);

    }

    /**
     * 触摸
     * @param event
     */
    public void handleTouch(MotionEvent event) {

        Point touchPoint=new Point((int)event.getX()-getLeft(),(int)event.getY());

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                int i = Math.round(prizes.size()) / 2;
                Prize prize = prizes.get(Math.round(prizes.size())/2);
                if(prize.isClick(touchPoint)){
                    if(!flags){
                        //确认是否抽奖
                        String[] uid = SPUtil.getUid(getContext());
                        if (uid!=null){
                            showInsureDialog(prize);
                        }else {
                            //未登录跳转登录页面
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            getContext().startActivity(intent);
                        }
//                        setStartFlags(true);
//                        prize.click();

                    }
                }
                break ;
            default:
                break ;
        }
    }

    /**
     * 是否抽奖对话框
     * @param prize
     */
    private void showInsureDialog(final Prize prize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.fragment_dialog_insure_duihuan, null);
        TextView tv_is_choujiang = view.findViewById(R.id.tv_shifouduihuan);
        TextView info = view.findViewById(R.id.tv_duihuan_info);
        Button btn_yes = view.findViewById(R.id.btn_jifenduihuan_yes);
        Button btn_no = view.findViewById(R.id.btn_jifenduihuan_no);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        tv_is_choujiang.setText("是否抽奖？");
        info.setText("抽奖一次将消耗您"+jifen+" 萝卜币");
        btn_yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //确定抽奖
                if (alertDialog!=null){
                    alertDialog.dismiss();
                }
                setStartFlags(true);
                prize.click();
            }
        });
        btn_no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消抽奖
                if (alertDialog!=null){
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }

    private class SurfaceRunnable implements Runnable{
        @Override
        public void run() {
            while(flags){
                Canvas canvas=null;
                try {
                    canvas = mHolder.lockCanvas();

                    drawBg(canvas);

                    drawTransfer(canvas);

                    drawPrize(canvas);

                    controllerTransfer();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    if(canvas!=null)
                        mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    //绘制背景
    private void drawBg(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#dd0b18"));
        //canvas.drawColor(Color.WHITE,Mode.CLEAR);
        //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.dzp_bg),0,0,null);

        int l = getPaddingLeft();
        int r = getPaddingRight();

        int width = (getMeasuredWidth()-l-r)/3;
        int x1=0;
        int y1=0;

        int x2=0;
        int y2=0;
        int len = (int) Math.sqrt(prizes.size());

        for(int x=0;x<len*len;x++){

            Prize prize = prizes.get(x);

            int index=x;
            x1=getPaddingLeft()+width*(Math.abs(index)%len)+5;
            y1=getPaddingTop()+width*(index/len)+5;

            x2=x1+width-10;
            y2=y1+width-10;
            RectF rectF = new RectF(x1, y1, x2, y2);

            Paint paint=new Paint();
            paint.setColor(prize.getBgColor());
           // canvas.drawRect(rect, paint);
            canvas.drawRoundRect(rectF,45,45,paint);
        }
    }

    //绘制旋转的方块
    private void drawTransfer(Canvas canvas) {
        int width = getMeasuredWidth()/3;
        int x1;
        int y1;

        int x2;
        int y2;
        int len = (int) Math.sqrt(prizes.size());
        current=next(current, len);
        x1=getPaddingLeft()+width*(Math.abs(current)%len)+5;
        y1=getPaddingTop()+width*((current)/len)+5;

        x2=x1+width-10;
        y2=y1+width-10;

     //   Rect rect=new Rect(x1,y1,x2,y2);
        RectF rectF = new RectF(x1, y1, x2, y2);

        Paint paint=new Paint();
        paint.setColor(transfer);
       // canvas.drawRect(rect, paint);
        canvas.drawRoundRect(rectF,45,45,paint);
    }

    //控制旋转
    private void controllerTransfer() {
        if(count>MAX){
            countDown++;
            SystemClock.sleep(count*5);
        }else{
            SystemClock.sleep(count*2);
        }

        count++;
        if(countDown>2){
            if(lottery==current){
                countDown=0;
                count=0;
                setStartFlags(false);
                if(listener!=null){
                    //切换到主线程中运行
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //随机抽选奖品
                            int prizeIndex = getPrizeIndex(prizes);
                            listener.onWinning(prizeIndex);
                        }
                    });
                }
            }
        }
    }

    public void setStartFlags(boolean flags){
        this.flags=flags;
    }

    //绘制奖品
    private void drawPrize(Canvas canvas) {
        int l = getPaddingLeft();
        int r = getPaddingRight();

        int width = (getMeasuredWidth()-l-r)/3;
        int x1=0;
        int y1=0;

        int x2=0;
        int y2=0;

        int bx1= 0;
        int by1= 0;

        int len = (int) Math.sqrt(prizes.size());

        for(int x=0;x<len*len;x++){

            Prize prize = prizes.get(x);
            int index=x;
            x1=getPaddingLeft()+width*(Math.abs(index)%len);
            y1=getPaddingTop()+width*(index/len);

            x2=x1+width;
            y2=y1+width;
            Rect rect=new Rect(x1+width/3,y1+width/6,x2-width/4,y2-width/3);

            if (x==Math.round(prizes.size())/2){
                //中心抽奖按钮位置
                rect=new Rect(x1+5,y1+5,x2-10,y2-10);
            }

            prize.setRect(rect);
            canvas.drawBitmap(prize.getIcon(), null, rect, null);

            //绘制文字
            Paint paint = new Paint();
            paint.setTextSize(40);

            int textx = (int) (x1+((x2-x1)/2-paint.measureText(prize.getInfo())/2));

            canvas.drawText(prize.getInfo(),textx,y2- DensityUtil.dp2px(12),paint);

        }
    }


    public void start() {
        setLottery(getRandom());
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SurfaceRunnable());
    }

    //获取随机中奖数，实际开发中一般中奖号码是服务器告诉我们的
    private int getRandom(){
        Random r=new Random();
        int nextInt =r.nextInt(prizes.size());
        if(nextInt%(Math.round(prizes.size()/2))==0){
            //随机号码等于中间开始位置，需要继续摇随机号
            return getRandom();
        }
        return nextInt;
    }

    //下一步
    public int next(int position,int len){
        int current=position;
        if(current+1<len){
            return ++current;
        }

        if((current+1)%len==0&&current<len*len-1){
            return current+=len;
        }

        if(current%len==0){
            return current-=len;
        }

        if(current<len*len){
            return --current;
        }

        return current;
    }


    public LotteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = this.getHolder();
        mHolder.addCallback(this);
    }

    public LotteryView(Context context) {
        this(context,null);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas=null;
        try {
            canvas = mHolder.lockCanvas();
            drawBg(canvas);
            drawPrize(canvas);

            Prize prize = prizes.get(Math.round(prizes.size()/2));
            prize.setListener(new Prize.OnClickListener() {
                @Override
                public void onClick() {
                    start();
                }
            });
        } catch (Exception e) {
            LogUtil.e("抽奖异常了===="+e.getMessage());
            e.printStackTrace();
        }finally{
            if(canvas!=null)
                mHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setStartFlags(false);
    }

    /**
     * 重新测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    /**
     * 根据Math.random()产生一个double型的随机数，判断每个奖品出现的概率
     * @param prizes
     * @return random：奖品列表prizes中的序列（prizes中的第random个就是抽中的奖品）
     *
     * 根据权重，来分配概率  如中权重为10
     * 奖品权重为 1,2,3,5  那么中奖率为0.1；0.2；0.3；0.5
     */
    public static int getPrizeIndex(List<Prize> prizes) {
        DecimalFormat df = new DecimalFormat("######0.00");
        int random = -1;
        try{
            //计算总权重
            double sumWeight = 0;
            for(Prize p : prizes){
                sumWeight += p.getWeight();
            }

            //产生随机数
            double randomNumber;
            randomNumber = Math.random();

            //根据随机数在所有奖品分布的区域并确定所抽奖品
            double d1 = 0;
            double d2 = 0;
            for(int i=0;i<prizes.size();i++){
                d2 += Double.parseDouble(String.valueOf(prizes.get(i).getWeight()))/sumWeight;
                if(i==0){
                    d1 = 0;
                }else{
                    d1 +=Double.parseDouble(String.valueOf(prizes.get(i-1).getWeight()))/sumWeight;
                }
                if(randomNumber >= d1 && randomNumber <= d2){
                    random = i;
                    break;
                }
            }
        }catch(Exception e){
            ToastHelper.getInstance().displayToastShort("系统繁忙，请稍后再试！");
        }
        return random;
    }
}
