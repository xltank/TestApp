package com.jasonxuli.test.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jasonxuli.test.R;
import com.jasonxuli.test.ViewVideoActivity;
import com.jasonxuli.test.comps.PopupConfirm;
import com.jasonxuli.test.comps.VideoListArrayAdapter;
import com.jasonxuli.test.constants.APIConstant;
import com.jasonxuli.test.constants.MessageConstant;
import com.jasonxuli.test.control.Facade;
import com.jasonxuli.test.utils.CommonUtil;
import com.jasonxuli.test.utils.GlobalData;
import com.jasonxuli.test.utils.VideoUtil;
import com.jasonxuli.test.vo.Video;
import com.jasonxuli.test.vo.VideoInfo;

public class VideoListFragment extends Fragment {

	private ListView videoList;
	
	private String curVideoInfoJSON ;
	protected VideoInfo curVideoInfo = null; 
	
	
	public VideoListFragment() {
	}

	
	@Override
	public void onAttach (Activity activity)
	{
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_video_list, container, false);
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();

		if(videoList == null)
		{
			videoList = (ListView) getActivity().findViewById(R.id.videoList);
			videoList.setOnItemClickListener(onVideoListItemClickHandler);
		}
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		Facade.ins().getRecentVideos(onGetRecentVideosHandler, "20", "0");
	}

	
	final Handler onGetRecentVideosHandler = new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		super.handleMessage(msg);
    		
    		String result = msg.getData().getString("result");
    		GlobalData.videos = new ArrayList<Video>();
    		try {
				JSONArray videos = (JSONArray) new JSONTokener(result).nextValue();
				for(int i=0; i<videos.length(); i++)
				{
					GlobalData.videos.add(new Video(videos.getJSONObject(i)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    		
    		VideoListArrayAdapter adapter = new VideoListArrayAdapter(getActivity(), R.layout.item_video_list, GlobalData.videos);
    		videoList.setAdapter(adapter);
    	}
    };
    
    
    final OnItemClickListener onVideoListItemClickHandler = new OnItemClickListener() 
    {
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    	{
    		final Video video = (Video) parent.getItemAtPosition(position);
    		if(!GlobalData.mobile_data_allowed && !CommonUtil.isWIFI(getActivity()))
    		{
    			final PopupConfirm popup = new PopupConfirm(getActivity(), 
    					getString(R.string.warning), 
    					getString(R.string.no_wifi_message));
    			popup.setOKButton(new OnClickListener() 
    			{
					@Override
					public void onClick(View v) 
					{
						popup.dismiss();
						GlobalData.mobile_data_allowed = true;
						getVideoInfo(video);
					}
				});
    			popup.show();
    		}
    		else {
    			getVideoInfo(video);
    		}
    	}
	};
	
	
	/////////// video
	private void getVideoInfo(Video video)
	{
		Facade.ins().getVideoInfo(
				onVideoInfoHandler, 
				video.getId(), 
				video.getPublisherId(), 
				APIConstant.DEFAULT_RESULT_FORMAT, 
				APIConstant.VIDEO_TYPE_MP4);
	}
	final Handler onVideoInfoHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			
			curVideoInfoJSON = msg.getData().getString("result");
			curVideoInfo = VideoUtil.parseVideoInfoJSON(curVideoInfoJSON);
			if(curVideoInfo==null || curVideoInfo.renditions.size() == 0)
			{
				System.err.println("ERROR: video info error or no playable rendition");
				return ;
			}
			viewVideo();
		}
	};

	
	public void viewVideo()
    {
    	Intent intent = new Intent(getActivity(), ViewVideoActivity.class);
    	intent.putExtra(MessageConstant.VIDEO_INFO_JSON, curVideoInfoJSON);
    	startActivity(intent);
    }
	
}
