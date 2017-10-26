/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.location;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.ruiyihong.toyshop.R;
import com.ruiyihong.toyshop.activity.MapBaseActivity;
import com.ruiyihong.toyshop.util.AppConstants;
import com.ruiyihong.toyshop.util.LogUtil;
import com.ruiyihong.toyshop.util.SPUtil;
import com.ruiyihong.toyshop.util.ScreenUtil;
import com.ruiyihong.toyshop.util.StatusBarUtil;
import com.ruiyihong.toyshop.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;



public class Location extends MapBaseActivity {

    private MapView mapView=null;
    private BaiduMap baiduMap;
    private LocationClient locationClient=null;
    private BDLocationListener locationListener=new MyLocationListener();
    private double latitude,latitudeLocation;
    private double longitude,longitudeLocation;
    private String addressLocation;
    private GeoCoder search=null;
    private ListView fjLocation;

    private ArrayList<String> LocationList;
    private EditText edit;
    private FrameLayout searchFragment;
    private TextView tv_qjd;
    private ListView lv_result;

    private static List<PoiInfo> poiInfo;
    private static SearchLocationAdapter adapter;
    private static PoiSearch poiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_location);
        /**竖屏**/
        setOrientationPortrait();
        initView();
    }



    protected void initView(){
        mapView= findViewById(R.id.mapview);
        mapView.setFocusable(true);
        fjLocation = findViewById(R.id.location_address);
        tv_qjd = findViewById(R.id.text_notuse);
        tv_qjd.requestFocus();
        //用这个来抢焦点
        edit = findViewById(R.id.tv_input_address);
        //用来替换其中的地图那部分
        searchFragment = findViewById(R.id.container_map);

        //lv_searchResult
        lv_result = findViewById(R.id.lv_searchResult);
        poiInfo = new ArrayList<PoiInfo>();
        adapter = new SearchLocationAdapter(Location.this, poiInfo);
        poiSearch = PoiSearch.newInstance();
        lv_result.setAdapter(adapter);

        lv_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.e("单机"+poiInfo.get(i).name);
                finish_thisForResult(poiInfo.get(i).name);
            }
        });

        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    searchFragment.setVisibility(View.VISIBLE);
                }else{
                  searchFragment.setVisibility(View.GONE);
                }
            }
        });

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initData(edit.getText().toString());
            }
        });

        baiduMap=mapView.getMap();
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(locationListener);
        initLocation();
        locationClient.start();


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish_thisForResult("");
            }
        });


        /**滑屏触发地图状态改变监听器**/
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                //TODO 改变需要随时读取位置数据
            }

            /**屏幕中间的经纬度**/
            @Override
            public void onMapStatusChangeFinish(final MapStatus mapStatus) {
                latitude = mapStatus.target.latitude;
                longitude = mapStatus.target.longitude;
                LatLng ptCenter = new LatLng(latitude, longitude);

                search.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
                search.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            ToastHelper.getInstance()._toast("抱歉，未能找到结果");
                            return;
                        }
                        if(baiduMap!=null){
                            baiduMap.clear();
                            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
                            addressLocation = result.getAddress();
                        }

                        List<PoiInfo> poiList = result.getPoiList();
                        LocationList.clear();
                        if(poiList!=null)
                        for (PoiInfo p : poiList) {
                            LogUtil.e(p.address);
                            LocationList.add(p.name+":"+ p.address);
                        }
                        //TODO 获取推荐地址列表
                        fjNameListAdapter();
                        getCity();
                    }
                });
            }
        });

        search= GeoCoder.newInstance();
        /**根据经纬度得到屏幕中心点地址**/



        /**返回定位点**/
        findViewById(R.id.rl_locating_point).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location(latitudeLocation,longitudeLocation);
            }
        });
    }

    private void finish_thisForResult(String location) {
        Intent intent = new Intent();
        intent.putExtra("location",location);
        setResult(0,intent);
        finish();
    }

    private void initData(String key){
        OnGetPoiSearchResultListener poiSearchResultListener=new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                poiInfo.clear();
                if(poiResult.getAllPoi()!=null){
                    poiInfo.addAll(poiResult.getAllPoi());
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };
        poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        String city = SPUtil.getString(this, AppConstants.LAST_LOCATION, "");
        if (TextUtils.isEmpty(city)) {
             city = "北京";
        }
        poiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(key));
    }

    private void fjNameListAdapter() {

        fjLocation.setDivider(new ColorDrawable(getResources().getColor(R.color.divider)));
        fjLocation.setDividerHeight(1);
       fjLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               finish_thisForResult(LocationList.get(i));
           }
       });
        fjLocation.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return LocationList.size();
            }

            @Override
            public String getItem(int i) {
                return LocationList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView tv1 = new TextView(Location.this);
                if(i==0)
                    tv1.setTextColor(getResources().getColor(R.color.tab_selected));
                String s = LocationList.get(i);
                String[] strArr = s.split(":");
                if(strArr.length>0)
                tv1.setText(strArr[0]);
                tv1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tv1.setTextSize(18);
                TextView tv2 = new TextView(Location.this);
                if(strArr.length>1)
                tv2.setText(strArr[1]+"\n");
                tv2.setTextSize(11);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, ScreenUtil.dp2px(Location.this,2), 0, 0);//左上右下
                tv1.setLayoutParams(lp);
                lp.setMargins(0, ScreenUtil.dp2px(Location.this,5), 0, 0);//左上右下
                tv2.setLayoutParams(lp);
                LinearLayout ln = new LinearLayout(Location.this);
                ln.setOrientation(LinearLayout.VERTICAL);
                ln.addView(tv1);
                ln.addView(tv2);
                return ln;
            }
        });
    }


    /**经纬度地址动画显示在屏幕中间**/
    private void location(double latitude,double longitude){
        LatLng ll = new LatLng(latitude, longitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(msu);
    }

    /**接收异步返回的定位结果**/
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            locationClient.stop();
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据

            LocationList=new ArrayList<>();

            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    LocationList.add(p.getName());
                }
            }
            Log.e("BaiduLocationApiDem", sb.toString());

            fjNameListAdapter();
            showCurrentPosition(location);
        }
    }

    /**配置定位SDK参数**/
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
//        int span=1000;
//        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.setEnableSimulateGps(false);
        // MapStatusUpdateFactory.zoomTo(20) 就是设置缩放等级的，
        // 有时候定位成功了，在当前的位置要显示自己的的位置，如果你的缩放等级不够的话
        // 显示的范围会很大，用户体验不够好。
        // 最后面是设置20的时候的页面，基本上满足需求了。
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19));
        mapView.showZoomControls(false);
        locationClient.setLocOption(option);
    }

    /**定位**/
    private void showCurrentPosition(BDLocation location){
        baiduMap.setMyLocationEnabled(true);
        MyLocationData locationData=new MyLocationData.Builder()
                // .accuracy(location.getRadius())
                .accuracy(0)//定位的那个圈
                // .direction(3000)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

       MyLocationConfiguration.LocationMode locationMode=MyLocationConfiguration.LocationMode.NORMAL;
       BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.location_circle);
       MyLocationConfiguration config = new MyLocationConfiguration(locationMode, true, mCurrentMarker);

       baiduMap.setMyLocationConfigeration(config);
        baiduMap.setMyLocationData(locationData);
        latitudeLocation=location.getLatitude();
        longitudeLocation=location.getLongitude();
        addressLocation=location.getAddrStr();
        location(latitudeLocation, longitudeLocation);
    }

    /**根据搜索页面地名的经纬度定位**/
    protected void onActivityResult(int RequestCode,int ResultCode,Intent data){
        if(RequestCode==0){
            if(ResultCode==1){
                location(Double.parseDouble(data.getStringExtra("latitude")),Double.parseDouble(data.getStringExtra("longitude")));
            }
        }
    }

    /**得到当前所在城市**/
    private String getCity(){
        if(addressLocation!=null&&!addressLocation.equals("")){
            int indexProvince=addressLocation.indexOf("省");
            int indexCity=addressLocation.indexOf("市");
            return addressLocation.substring(indexProvince + 1, indexCity);
        }
        return null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected  void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
        if(locationClient!=null){
            locationClient.stop();
            locationClient.unRegisterLocationListener(locationListener);
        }
    }

    @Override
    public void onBackPressed() {
        finish_thisForResult("");
    }
}
