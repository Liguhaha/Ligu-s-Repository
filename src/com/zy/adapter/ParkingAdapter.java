package com.zy.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zy.iparking.R;
import com.zy.model.ParkInfo;

public class ParkingAdapter extends BaseAdapter {
//	private Context context = null;
	private List<ParkInfo> parkInfos = null;
	private LayoutInflater mInflater = null;

	public ParkingAdapter(Context context, List<ParkInfo> parkInfos) {
//		this.context = context;
		this.parkInfos = parkInfos;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return parkInfos.size();
	}

	public Object getItem(int position) {
		return parkInfos.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_parking, parent,
					false);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tvAddress = (TextView) convertView
					.findViewById(R.id.tv_address);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(parkInfos.get(position).getName());
		holder.tvAddress.setText(parkInfos.get(position).getAddress());
		return convertView;
	}

	public final class ViewHolder {
		private TextView tvName;
		private TextView tvAddress;
	}

}
