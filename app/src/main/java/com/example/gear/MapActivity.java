package com.example.gear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.supermap.data.Color;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EngineType;
import com.supermap.data.Environment;
import com.supermap.data.GeoLine;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.GeometryType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Rectangle2D;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicLine;
import com.supermap.mapping.dyn.DynamicPoint;
import com.supermap.mapping.dyn.DynamicPolygon;
import com.supermap.mapping.dyn.DynamicStyle;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.services.DataDownloadService;
import com.supermap.services.FeatureSet;
import com.supermap.services.ResponseCallback;

import org.apache.log4j.chainsaw.Main;

public class MapActivity extends AppCompatActivity {
    ImageButton mBack, mLayers;
    private MapControl m_mapcontrol = null;
    private MapView m_mapView = null;
    private DynamicView mDynamicView = null;
    private final String DYN_TAG_POINT_PIT = "DYN_TAG_POINT_PIT";
    private final String DYN_TAG_LINE_PIT = "DYN_TAG_PIT";
    private final String DYN_TAG_POLY_BLAST = "DYN_TAG_POLY_BLAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String rootPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        rootPath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

        //Set path which system will use
        Environment.setLicensePath(rootPath + "/SuperMap/license/");
        Environment.setTemporaryPath(rootPath + "/SuperMap/temp/");
        Environment.setWebCacheDirectory(rootPath + "/SuperMap/WebCache/");

        //Component function could be used only after the initialization of Environment.
        Environment.initialization(this);

        //If font to be displayed is not in machine, you could put font files in path standed for by parameter.
        //E.g., If arabic charachter is to be displayed (no font files in system before). You could put font files in path standed for by parameter.

        Environment.setFontsPath(rootPath + "/SuperMap/fonts/");
        setContentView(R.layout.activity_map);

        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapActivity.this, MainActivity.class));
            }
        });
        mLayers = findViewById(R.id.layer);

        m_mapView = (MapView)findViewById(R.id.Map_view);
        m_mapcontrol = m_mapView.getMapControl();

        mDynamicView = new DynamicView(this, m_mapcontrol.getMap());
        m_mapView.addDynamicView(mDynamicView);

        ZoomControls m_zoom = findViewById(R.id.zoomControls1);
        m_zoom.setIsZoomInEnabled(true);
        m_zoom.setIsZoomOutEnabled(true);

        //Zoom in button
        m_zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_mapcontrol.getMap().zoom(2);
                m_mapcontrol.getMap().refresh();
            }
        });

        //Zoom out button
        m_zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_mapcontrol.getMap().zoom(0.5);
                m_mapcontrol.getMap().refresh();
            }
        });

        openOrtho();

    }

    private void openOrtho() {
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setAlias("Ortho_MTBU");
        info.setEngineType(EngineType.Rest);
        info.setServer("http://128.199.133.7:8091/iserver/services/map-pama_mtbu/rest/maps/T2112_Ortho_Pit_MTBU_UTM48S");

        //if not call this, will be FC.
        // Need to clear basemap layer first before close datasource
        m_mapcontrol.getMap().getLayers().clear();
        boolean isClosed = m_mapcontrol.getMap().getWorkspace().getDatasources().close("Ortho_MTBU");

        Datasource datasource = m_mapcontrol.getMap().getWorkspace().getDatasources().open(info);
        Dataset dtsBasemap = datasource.getDatasets().get(0);
        dtsBasemap.setName("ortho_mtbu");

        m_mapcontrol = m_mapView.getMapControl();
        m_mapcontrol.getMap().getLayers().add(dtsBasemap,true);
        m_mapcontrol.getMap().refresh();

        Rectangle2D rectangle2D = m_mapcontrol.getMap().getLayers().get(0).getDataset().getBounds();
        m_mapcontrol.getMap().setViewBounds(rectangle2D);

//        DownServiceDesainPitPoint();
        DownServiceDesainPit();
        DownServiceInvBlast();

    }

    private void DownServiceDesainPit() {
        //Example URL:http://103.193.14.22:8091/iserver/services/data-data_site/rest/data/datasources/data_48s/datasets/MTBU_Desain_Pit_Line_2D/features

        DataDownloadService downloadService = new DataDownloadService("http://103.193.14.22:8091");

        String serviceName = "data-data_site/rest";
        String datasourceName = "data_48s";
        String datasetName = "MTBU_Desain_Pit_Line_2D";

        downloadService.setResponseCallback(new ResponseCallback() {
            @Override
            public void requestFailed(String s) {
                Toast.makeText(MapActivity.this, "Request Failed: "+s, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void requestSuccess() {
                Toast.makeText(MapActivity.this, "Request Success", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void addFeatureSuccess(int i) {

            }
            @Override
            public void receiveResponse(FeatureSet featureSet) {
                Toast.makeText(MapActivity.this, "Receive Response", Toast.LENGTH_SHORT).show();

                mDynamicView.removeByTag(DYN_TAG_LINE_PIT);

                DynamicStyle dynStyle = new DynamicStyle();
                dynStyle.setLineColor(16711680);
                dynStyle.setSize(1);

                DynamicLine dynamicLine = new DynamicLine();
                dynamicLine.setTag(DYN_TAG_LINE_PIT);
                dynamicLine.setStyle(dynStyle);

                featureSet.moveFirst();

                while (!featureSet.isEOF()) {
                    Geometry geo = featureSet.getGeometry();
                    if (geo == null) {
                        featureSet.moveNext();
                        continue;
                    }

                    GeometryType geometryType = featureSet.getGeometry().getType();

                    if (geometryType == GeometryType.GEOPOINT ) {

                    }
                    else if (geometryType == GeometryType.GEOLINE ) {
                        GeoLine line1 = (GeoLine) featureSet.getGeometry();

                        // ambil semua vertex dalam line
                        for (int i = 0 ; i < line1.getPartCount(); i ++){
                            Point2Ds point2Ds =  line1.getPart(i);
                            for (int j = 0 ; j < point2Ds.getCount() ; j++){
                                Point2D pt = point2Ds.getItem(j);
                                dynamicLine.addPoint(pt);
                            }
                        }
                    }
                    else if (geometryType == GeometryType.GEOREGION) {

                    }
                    else {
//                    Log.e("Feature" , " : "+ featureSet.ge);
                    }
                    featureSet.moveNext();
                }

                mDynamicView.addElement(dynamicLine);
                mDynamicView.refresh();
            }
            @Override
            public void dataServiceFinished(String s) {
                Log.e("APP" , "dataServiceFinished result : "+ s);
            };
        });

//          downloadService.downloadAll(serviceName,datasourceName,datasetName);
            downloadService.download(serviceName, datasourceName, datasetName,1,50);
            downloadService.download(serviceName, datasourceName, datasetName,51,100);

    }

    private void DownServiceInvBlast() {
        // http://103.193.14.22:8091/iserver/services/data-data_site/rest/data/datasources/data_48s/datasets/MTBU_Inventory_Blast_Area_2D/features

        DataDownloadService downloadService = new DataDownloadService("http://103.193.14.22:8091");

        String serviceName = "data-data_site/rest";
        String datasourceName = "data_48s";
        String datasetName = "MTBU_Inventory_Blast_Area_2D";

        downloadService.setResponseCallback(new ResponseCallback() {

            @Override
            public void requestFailed(String s) {
                Toast.makeText(MapActivity.this, "Request Failed: "+s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestSuccess() {
                Toast.makeText(MapActivity.this, "Request Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void addFeatureSuccess(int i) {

            }

            @Override
            public void receiveResponse(FeatureSet featureSet) {
                Toast.makeText(MapActivity.this, "Receive Response", Toast.LENGTH_SHORT).show();

                mDynamicView.removeByTag(DYN_TAG_POLY_BLAST);

                DynamicStyle dynStyle = new DynamicStyle();
                dynStyle.setLineColor(16776960);
                dynStyle.setSize(2);

                DynamicPolygon dynamicPoly = new DynamicPolygon();
                dynamicPoly.setTag(DYN_TAG_POLY_BLAST);
                dynamicPoly.setStyle(dynStyle);

                featureSet.moveFirst();

                while (!featureSet.isEOF()) {
                    Geometry geo = featureSet.getGeometry();
                    if (geo == null) {
                        featureSet.moveNext();
                        continue;
                    }

                    GeometryType geometryType = featureSet.getGeometry().getType();

                    if (geometryType == GeometryType.GEOPOINT ) {

                    }
                    else if (geometryType == GeometryType.GEOLINE ) {

                    }
                    else if (geometryType == GeometryType.GEOREGION ) {
                        GeoRegion region = (GeoRegion) featureSet.getGeometry();

                        // ambil semua vertex dalam line
                        for (int i = 0 ; i < region.getPartCount(); i ++){
                            Point2Ds point2Ds =  region.getPart(i);
                            for (int j = 0 ; j < point2Ds.getCount() ; j++){
                                Point2D pt = point2Ds.getItem(j);
                                dynamicPoly.addPoint(pt);
                            }
                        }
                    }
                    else {
//                    Log.e("Feature" , " : "+ featureSet.ge);
                    }
                    featureSet.moveNext();
                }

                mDynamicView.addElement(dynamicPoly);
                mDynamicView.refresh();
            }

            @Override
            public void dataServiceFinished(String s) {
                Log.e("APP" , "dataServiceFinished result : "+ s);
            };
        });

//        downloadService.downloadAll(serviceName,datasourceName,datasetName);
            downloadService.download(serviceName, datasourceName, datasetName, 1, 99999);

    }
    private void DownServiceDesainPitPoint() {
        //Example URL:http://103.193.14.22:8091/iserver/services/data-data_site/rest/data/datasources/data_48s/datasets/MTBU_Desain_Pit_Point_2D/features

        DataDownloadService downloadService = new DataDownloadService("http://103.193.14.22:8091");

        String serviceName = "data-data_site/rest";
        String datasourceName = "data_48s";
        String datasetName = "MTBU_Desain_Pit_Point_2D";

        downloadService.setResponseCallback(new ResponseCallback() {
            @Override
            public void requestFailed(String s) {
                Toast.makeText(MapActivity.this, "Request Failed: "+s, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void requestSuccess() {
                Toast.makeText(MapActivity.this, "Request Success", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void addFeatureSuccess(int i) {

            }
            @Override
            public void receiveResponse(FeatureSet featureSet) {
                Toast.makeText(MapActivity.this, "Receive Response", Toast.LENGTH_SHORT).show();

                mDynamicView.removeByTag(DYN_TAG_POINT_PIT);

                DynamicStyle dynStyle = new DynamicStyle();
                dynStyle.setPointColor(16711680);
                dynStyle.setSize(20);

                DynamicPoint dynamicPoint = new DynamicPoint();
                dynamicPoint.setTag(DYN_TAG_POINT_PIT);
                dynamicPoint.setStyle(dynStyle);

                featureSet.moveFirst();

                while (!featureSet.isEOF()) {
                    Geometry geo = featureSet.getGeometry();
                    if (geo == null) {
                        featureSet.moveNext();
                        continue;
                    }

                    GeometryType geometryType = featureSet.getGeometry().getType();

                    if (geometryType == GeometryType.GEOPOINT ) {
                        GeoPoint point = (GeoPoint) featureSet.getGeometry();

                        double nilaiX = point.getX();
                        double nilaiY = point.getY();
                        Point2D pt = new Point2D(nilaiX,nilaiY);
                        dynamicPoint.addPoint(pt);

//                        // ambil semua vertex dalam line
//                        for (int i = 0; i < point.getPartCount(); i ++){
//                            Point2Ds point2Ds =  point.getPart(i);
//                            for (int j = 0 ; j < point2Ds.getCount() ; j++){
//                                Point2D pt = point2Ds.getItem(j);
//                                dynamicPoint.addPoint(pt);
//                            }
//                        }

                    }
                    else if (geometryType == GeometryType.GEOLINE ) {

                    }
                    else if (geometryType == GeometryType.GEOREGION) {

                    }
                    else {
//                    Log.e("Feature" , " : "+ featureSet.ge);
                    }
                    featureSet.moveNext();
                }

                mDynamicView.addElement(dynamicPoint);
                mDynamicView.refresh();
            }
            @Override
            public void dataServiceFinished(String s) {
                Log.e("APP" , "dataServiceFinished result : "+ s);
            };
        });

//          downloadService.downloadAll(serviceName,datasourceName,datasetName);
        downloadService.download(serviceName, datasourceName, datasetName,1,50);

    }
}