package com.huawei.rtcdemo.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.rtc.utils.HRTCEnums;
import com.huawei.rtcdemo.R;
import com.huawei.rtcdemo.activities.LiveActivity;
import com.huawei.rtcdemo.bean.BeanRoomMember;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class RoomMembersAdapter extends RecyclerView.Adapter {

    private LiveActivity mContext;
    private List<BeanRoomMember> mEntityList;
    private int mUserThreshold;

    public RoomMembersAdapter(LiveActivity context, List<BeanRoomMember> entityList, int threshold) {
        this.mContext = context;
        this.mEntityList = entityList;
        this.mUserThreshold = threshold;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_room_member, viewGroup, false);
        final RoomMemberRecyclerHolder viewHolder=new RoomMemberRecyclerHolder(view);
        viewHolder.videoImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (viewHolder.videoImg.getTag().equals("unSelect")){
                    if(mContext.getNumberOfPlaying() >= mUserThreshold){
                        Toast.makeText(v.getContext(),"open " + viewHolder.userIdTv.getText() + " view failed, current displayed view reaches maximum!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewHolder.videoImg.setTag("select");
                    Toast.makeText(v.getContext(),"already opened " + viewHolder.userIdTv.getText() + " view",Toast.LENGTH_SHORT).show();
                    if(viewHolder.userIdTv.getText().toString().contains("AUX_")){
                        mContext.renderRemoteAuxView(viewHolder.userIdTv.getText().toString().substring(4));
                    }else{
                        mContext.renderRemoteUser(viewHolder.userIdTv.getText().toString(), HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_HD);
                    }

                    mContext.changePlayState(viewHolder.userIdTv.getText().toString());
                    viewHolder.videoImg.setImageResource(R.drawable.video_on);
                }else {
                    viewHolder.videoImg.setTag("unSelect");
                    Toast.makeText(v.getContext(),"already closed "+viewHolder.userIdTv.getText()+" view",Toast.LENGTH_SHORT).show();
                    mContext.unSelectRemoteUser(viewHolder.userIdTv.getText().toString());
                    mContext.changePlayState(viewHolder.userIdTv.getText().toString());
                    viewHolder.videoImg.setImageResource(R.drawable.video_off);
                }

            }
        });

        viewHolder.audioImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (viewHolder.audioImg.getTag().equals("unSelect")) {
                    mContext.openAudio(viewHolder.userIdTv.getText().toString());
                    viewHolder.audioImg.setTag("select");
                    viewHolder.audioImg.setImageResource(R.drawable.mic_on);
                } else {
                    mContext.closeAudio(viewHolder.userIdTv.getText().toString());
                    viewHolder.audioImg.setTag("unSelect");
                    viewHolder.audioImg.setImageResource(R.drawable.mic_off);
                }
            }
        });
        return new RoomMemberRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BeanRoomMember entity = mEntityList.get(position);
        ((RoomMemberRecyclerHolder) holder).userIdTv.setText(entity.getUserId());
        ((RoomMemberRecyclerHolder) holder).videoImg.setImageResource(entity.getIsPlaying()?R.drawable.video_on:R.drawable.video_off);
        ((RoomMemberRecyclerHolder) holder).videoImg.setTag(entity.getIsPlaying()?"select":"unSelect");
        ((RoomMemberRecyclerHolder) holder).audioImg.setImageResource(entity.getIsAudioOPen()?R.drawable.mic_on:R.drawable.mic_off);
        ((RoomMemberRecyclerHolder) holder).audioImg.setTag(entity.getIsAudioOPen()?"select":"unSelect");
        if (mContext.IsHaveAux()) {
            ((RoomMemberRecyclerHolder) holder).videoImg.setEnabled(false);
        } else {
            ((RoomMemberRecyclerHolder) holder).videoImg.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }

    private class RoomMemberRecyclerHolder extends RecyclerView.ViewHolder {

        private TextView userIdTv;
        private ImageView videoImg;
        private ImageView audioImg;

        public RoomMemberRecyclerHolder(View itemView) {
            super(itemView);
            userIdTv = (TextView) itemView.findViewById(R.id.item_userid);
            videoImg = (ImageView) itemView.findViewById(R.id.img_video);
            audioImg = (ImageView) itemView.findViewById(R.id.img_audio);
        }
    }
}
