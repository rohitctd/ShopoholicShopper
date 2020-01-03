package com.shopoholic.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.shopoholic.R;
import com.shopoholic.models.ProductLocation;

public class ClusterLocation extends DefaultClusterRenderer<ProductLocation> {
    private IconGenerator iconGenerator;
    private View clusterView;
    private TextView clusterCount;

    public ClusterLocation(Context mContext, GoogleMap mMap, ClusterManager<ProductLocation> mClusterManager) {
        super(mContext, mMap, mClusterManager);
        clusterView = LayoutInflater.from(mContext).inflate(R.layout.item_cluster, null, false);
        iconGenerator = new IconGenerator(mContext);
        iconGenerator.setBackground(null);
        clusterCount = clusterView.findViewById(R.id.tv_marker);
        iconGenerator.setContentView(clusterView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ProductLocation item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        if (item.getType().equals("1")) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_pink_mark));
        }else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_purple_mark));
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ProductLocation> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_purple_mark));
        clusterCount.setText(getClusterText(cluster.getSize()));
        Bitmap bitmap = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
    }

    @Override
    protected String getClusterText(int bucket) {
        return String.valueOf(bucket);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }
}