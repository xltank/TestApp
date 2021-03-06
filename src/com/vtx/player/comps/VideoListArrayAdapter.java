package com.vtx.player.comps;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vtx.player.R;
import com.vtx.player.control.ImageManager;
import com.vtx.player.utils.CommonUtil;
import com.vtx.player.vo.Video;


public class VideoListArrayAdapter extends ArrayAdapter<Video> {

	public VideoListArrayAdapter(Context context, int resourceId, List<Video> objects)
	{
		super(context, resourceId, objects);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
//		System.out.println(position);
		View view = convertView;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_video_list, null, false);
		}
		
		ImageView imageView = (ImageView) view.findViewById(R.id.image_item_video_list);
		TextView titleText = (TextView) view.findViewById(R.id.title_item_video_list);
		TextView durationText = (TextView) view.findViewById(R.id.duration_item_video_list);
		
		Video video = getItem(position);
		
//		imageView.setImageURI(Uri.parse(video.getSnapshotUrl()));
		
//		System.out.println("url : " + CommonUtil.getHTTPFileName(video.getThumbnailUrl()));
		ImageManager.ins().loadImage(video.getThumbnailUrl(), imageView, 2);
		
		titleText.setText(video.getTitle());
		durationText.setText(CommonUtil.formatDuration(video.getDuration()));
		
		return view;
    }
}
