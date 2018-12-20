package com.example.administrator.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {
    AMap aMap = null;
    private PoiSearch.Query query;
    private MapView mapView;
    private AMapLocationClient mLocationClient;

    private AMapLocationClientOption mLocationOption;
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    private double latitude;
    private double longitude;
    private GeocodeSearch geocoderSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //定义了一个地图view
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。
//初始化地图控制器对象

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        dingwei();

    }
    private void sousuo() {
        query = new PoiSearch.Query("宏福苑", "", "北京");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);
        PoiSearch poiSearch = new PoiSearch(this, query);
        //设置搜索回调
        poiSearch.setOnPoiSearchListener(this);
        //发送搜索请求
        poiSearch.searchPOIAsyn();
    }

    private void dingwei() {

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        getPositioning();
//        zhongdian();
    }

    class MyAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(final AMapLocation aMapLocation) {
            latitude = aMapLocation.getLatitude();//获取纬度
            longitude = aMapLocation.getLongitude();//获取经度
            String aoiName = aMapLocation.getAoiName();
            String aoiName2 = aMapLocation.getPoiName();//获取当前所在地址
            String address = aMapLocation.getCity();//获取的当前所在城市
            Log.e("liangxq", "latitude=" + "\n" + latitude + "\n" + "longitude=" + longitude);
            zhongdian();
        }
    }
    public void getPositioning() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //启动定位
        mLocationClient.startLocation();
    }
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    poiResult = poiResult;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    }
                }
            }
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /*private void qidian() {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                zhongdian();
            }

        });
        //起点默认设置为鹤壁 也可以自己定义位置
        GeocodeQuery query = new GeocodeQuery("鹤壁", "");
        geocoderSearch.getFromLocationNameAsyn(query);
    }*/

    // 设置终点的方法
    private LatLonPoint latLonPoint1;

    private void zhongdian() {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                latLonPoint1 = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                // 调用路线规划
                luxian();
            }
        });
        GeocodeQuery query = new GeocodeQuery("翼城", "");
        geocoderSearch.getFromLocationNameAsyn(query);
    }


    private RouteSearch routeSearch;

    // 路线规划的方法
    private void luxian() {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
                aMap.clear();
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getPaths() != null) {
                        if (result.getPaths().size() > 0) {
                            final DrivePath drivePath = result.getPaths()
                                    .get(0);
                            if (drivePath == null) {
                                return;
                            }
                            DrivingRouteOverlay drivingRouteOverlay;
                            drivingRouteOverlay = new DrivingRouteOverlay(
                                    MainActivity.this, aMap, drivePath,
                                    result.getStartPos(),
                                    result.getTargetPos(), null);
                            drivingRouteOverlay.setNodeIconVisibility(false);
                            drivingRouteOverlay.setIsColorfulline(true);
                            drivingRouteOverlay.removeFromMap();
                            drivingRouteOverlay.addToMap();
                            drivingRouteOverlay.zoomToSpan();
                        } else if (result != null && result.getPaths() == null) {
//                            ToastUtil.show(mContext, R.string.no_result);
                        }

                    } else {
//                        ToastUtil.show(mContext, R.string.no_result);
                    }
                } else {
//                    ToastUtil.showerror(MainActivity.this, errorCode);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });


        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(latitude,longitude), latLonPoint1);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, 0, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }
}
