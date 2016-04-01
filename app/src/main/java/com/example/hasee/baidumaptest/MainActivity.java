package com.example.hasee.baidumaptest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mlocationClient;
    private MyLocationListenter myLocationListenter;
    private boolean isFirstIn=true;
    private Context context;
    private double mLatitude;
    private  double mLonglitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.content_main);
        this.context=this;
        //获取地图控件引用
       initView();
        initLocation();
    }

    private void initLocation() {
        mlocationClient=new LocationClient(this);
        myLocationListenter=new MyLocationListenter();
        mlocationClient.registerLocationListener(myLocationListenter);
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
       mlocationClient.setLocOption(option);
    }

    private void initView() {
        mMapView= (MapView) findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(17f);
       mBaiduMap.setMapStatus(msu);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!mlocationClient.isStarted())
            mlocationClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mlocationClient.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通（off）");
                }else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通（on)");
                }
            case R.id.id_map_location:
                centerTop(mLatitude, mLonglitude);
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
//定位代码
    private void centerTop(double mLatitude, double mLonglitude) {
        LatLng latLng = new LatLng(mLatitude, mLonglitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    private class MyLocationListenter implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            MyLocationData data=new MyLocationData.Builder().accuracy(bdLocation.getRadius())//
                    .latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(data);
            mLatitude=bdLocation.getLatitude();
            mLonglitude=bdLocation.getLongitude();
            if (isFirstIn){
                centerTop(bdLocation.getLatitude(), bdLocation.getLongitude());
                isFirstIn=false;
                Toast.makeText(context, bdLocation.getAddrStr(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
