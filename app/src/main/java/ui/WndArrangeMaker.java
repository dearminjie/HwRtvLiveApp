package com.huawei.rtcdemo.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.rtcdemo.R;
import com.huawei.rtcdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WndArrangeMaker extends RelativeLayout {
    private static final String TAG = "WndArrangeMaker";
    private static final int MAX_USER = 4;
    private static final int STAT_LEFT_MARGIN = 50;
    private static final int STAT_TEXT_SIZE = 20;

    private List<String> mUserIdList = new ArrayList<String>(MAX_USER);
    private Map<String, ViewGroup> mUserViewList = new HashMap<String, ViewGroup>(MAX_USER);

    public WndArrangeMaker(Context context) {
        super(context);
        init();
    }

    public WndArrangeMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WndArrangeMaker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.live_room_bg);
    }

    public void addUserVideoSurface(String userId, SurfaceView surface, boolean isLocal) {
        LogUtil.i(TAG, "addUserVideoSurface surface : "+userId);
        String userLabel = "";
        if (surface == null) {
            LogUtil.e(TAG, "addUserVideoSurface surface == null "+userId);
            return;
        }

        if (!isLocal) {
            userLabel = userId;
        }

        LogUtil.i(TAG, "addUserVideoSurface surface != null "+userId);

        if (mUserViewList.size() >= MAX_USER) {
            LogUtil.i(TAG, "addUserVideoSurface mUserViewList>=MAX_USER " + userId);
            if (mUserViewList.containsKey(userId)) {
                mUserViewList.put(userId, createVideoView(surface, userLabel));
            } else {
                LogUtil.e(TAG,"maximum view reached:" + mUserViewList.size());
                return;
            }
        } else {
            if (!mUserIdList.contains(userId)) {
                LogUtil.i(TAG, "addUserVideoSurface mUserIdList userId: " + userId);
                mUserIdList.add(userId);
            }
            mUserViewList.put(userId, createVideoView(surface, userLabel));
        }
        requestGridLayout();
    }

    public String reAddLocalUserVideoSurface(String userId, SurfaceView surface, boolean isLocal) {
        String removeUserId = null;
        String userLabel = "";
        if (surface == null) {
            return null;
        }

        if (!isLocal) {
            userLabel = userId;
        }

        if (mUserViewList.size() >= MAX_USER) {
            if (mUserViewList.containsKey(userId)) {
                mUserViewList.put(userId, createVideoView(surface, userLabel));
            } else {
                removeUserId = mUserIdList.get(mUserIdList.size()-1);
                mUserViewList.remove(mUserIdList.get(mUserIdList.size()-1));
                mUserIdList.remove(mUserIdList.get(mUserIdList.size()-1));

                mUserIdList.add(0,userId);
                mUserViewList.put(userId, createVideoView(surface, userLabel));
            }
        } else {
            if (!mUserIdList.contains(userId)) {
                LogUtil.i(TAG, "addUserVideoSurface mUserIdList userId: " + userId);
                mUserIdList.add(0,userId);
            }
            mUserViewList.put(userId, createVideoView(surface, userLabel));
        }
        requestGridLayout();
        return removeUserId;
    }

    private ViewGroup createVideoView(SurfaceView surface, String userLabel) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setId(surface.hashCode());

        LayoutParams videoLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(surface, videoLayoutParams);

        TextView text = new TextView(getContext());
        text.setId(layout.hashCode());
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        textParams.leftMargin = STAT_LEFT_MARGIN;
        text.setTextColor(Color.DKGRAY);
        text.setTextSize(STAT_TEXT_SIZE);
        text.setText(userLabel);

        layout.addView(text, textParams);
        return layout;
    }

    public void removeUserVideo(String userId) {
        LogUtil.i(TAG, "removeUserVideo userId:" + userId);
        if (mUserIdList.contains(userId)) {
            mUserIdList.remove(userId);
            mUserViewList.remove(userId);
        }
        requestGridLayout();
    }

    public void unSelectUserVideo(String userId) {
        LogUtil.i(TAG, "unSelectUserVideo userId:" + userId);
        if (mUserIdList.contains(userId)) {
            mUserIdList.remove(userId);
            mUserViewList.remove(userId);
        }
        requestGridLayout();
    }

    private void requestGridLayout() {
        removeAllViews();
        layout(mUserIdList.size());
    }

    private void layout(int size) {
        LogUtil.i(TAG, "layout size:" + size);
        LayoutParams[] params = getParams(size);
        for (int i = 0; i < size; i++) {
            LogUtil.i(TAG, "userId:" + mUserIdList.get(i));
            addView(mUserViewList.get(mUserIdList.get(i)), params[i]);
        }
    }

    private LayoutParams[] getParams(int size) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        LayoutParams[] array =
                new LayoutParams[size];

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                array[0] = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 1) {
                array[1] = new LayoutParams(width, height / 2);
                array[0].height = array[1].height;
                array[1].width = width;//解决部分手机多人连麦时出现黑边框问题 lwx640718
                array[1].addRule(RelativeLayout.BELOW, mUserViewList.get(mUserIdList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 2) {
                array[i] = new LayoutParams(width / 2, height / 2);
                array[i - 1].width = array[i].width;
                array[i].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUserIdList.get(i - 1)).getId());
                array[i].addRule(RelativeLayout.ALIGN_TOP, mUserViewList.get(mUserIdList.get(i - 1)).getId());
            } else if (i == 3) {
                array[i] = new LayoutParams(width / 2, height / 2);
                array[0].width = width / 2;
                array[1].addRule(RelativeLayout.BELOW, 0);
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                array[1].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUserIdList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.RIGHT_OF, 0);
                array[2].addRule(RelativeLayout.ALIGN_TOP, 0);
                array[2].addRule(RelativeLayout.BELOW, mUserViewList.get(mUserIdList.get(0)).getId());
                array[3].addRule(RelativeLayout.BELOW, mUserViewList.get(mUserIdList.get(1)).getId());
                array[3].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUserIdList.get(2)).getId());
            }
        }

        return array;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllVideo();
    }

    private void clearAllVideo() {
        removeAllViews();
        mUserViewList.clear();
        mUserIdList.clear();
    }
}
