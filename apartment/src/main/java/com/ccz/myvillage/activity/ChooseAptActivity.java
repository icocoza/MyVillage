package com.ccz.myvillage.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.dialog.AlertManager;
import com.ccz.myvillage.activity.dialog.IDialogListResultListener;
import com.ccz.myvillage.common.GeoUtils;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.BuildingInfo;
import com.ccz.myvillage.form.response.ResGpsSearch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseAptActivity extends CommonActivity implements IWsListener {

    static public final int REQUEST_LOCATION = 1;
    private Location lastKnownLocation = null;

    List<BuildingInfo> buildingList = null;

    BuildingInfo buildingInfo;

    LocationListener locationListener;
    private FusedLocationProviderClient mFusedLocationClient;
    OnCompleteListener<Location> mCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_apt);

        WsMgr.getInst().setOnWsListener(this, this);

        if (this.getIntent().hasExtra("buildingInfo"))
            buildingInfo = (BuildingInfo) this.getIntent().getSerializableExtra("buildingInfo");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            getCurrentLocation();
        }
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        getCurrentLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void getCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location loc = task.getResult();
                    Log.i("[GPS]", "lat: " + loc.getLatitude() + ", lon: " + loc.getLongitude());
                    Log.i("[GPS]", "lat: " + GeoUtils.toDMS(loc.getLatitude()) + ", lon: " + GeoUtils.toDMS(loc.getLongitude()));
                    Toast.makeText(ChooseAptActivity.this, "lat: " + loc.getLatitude() + ", lon: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    requestBuildingsList(loc.getLongitude(), loc.getLatitude());
                }
            }
        };
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, mCompleteListener);
    }

    public void onClickGps(View view) {
        view.setEnabled(false);
        getLocationPermission();
    }

    public void onClickNext(View view) {
        if(buildingInfo == null) {
            AlertManager.showYes(this, null, this.getString(R.string.select_apartment_to_next), null);
        }else {
            Intent intent = new Intent(this, ChooseIdActivity.class);
            intent.putExtra("buildingInfo", buildingInfo);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if(ECmd.gps_search == cmd) {
            showBuildingsList(origMessage);
            ((ImageView)findViewById(R.id.ivGps)).setEnabled(true);
        }
        return true;
    }

    private void requestBuildingsList(double lon, double lat) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.gps_search);
        node.put("lon", lon);
        node.put("lat", lat);
        WsMgr.getInst().send(node.toString());
    }

    private void showBuildingsList(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResGpsSearch res = mapper.readValue(message, ResGpsSearch.class);
        buildingList = res.getBuildings();
        List<String> buildingNameList = buildingList.stream().map(x->x.getBuildName()).collect(Collectors.toList());

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                this, // Context
                android.R.layout.simple_list_item_single_choice, // Layout
                buildingNameList // List
        );
        AlertManager.showList(this, getString(R.string.select_apartment), arrayAdapter, new IDialogListResultListener() {
            @Override
            public void onDialogResult(boolean yesOrNo, int type) {
                if(yesOrNo==true && buildingInfo != null) {
                    TextView tvName = ChooseAptActivity.this.findViewById(R.id.tvName);
                    tvName.setText(buildingInfo.getBuildName());
                }else if(yesOrNo==false) {
                    buildingInfo = null;
                }
            }

            @Override
            public void onItemSelected(int which) {
                buildingInfo = buildingList.get(which);
            }
        });
    }
}
