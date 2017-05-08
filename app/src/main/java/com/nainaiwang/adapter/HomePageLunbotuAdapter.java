package com.nainaiwang.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nainaiwang.bean.Lunbotu;
import com.nainaiwang.zixun.DetailsActivity;
import com.nainaiwang.zixun.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */
public class HomePageLunbotuAdapter extends PagerAdapter {

    private List<Lunbotu> picUrlList;// 图片的网址
    private Context context;// 上下文
    private ImageOptions options;

    public HomePageLunbotuAdapter(List<Lunbotu> picUrlList, Context context) {
        this.picUrlList = picUrlList;
        this.context = context;

        options = new ImageOptions.Builder().setIgnoreGif(true)
                // 忽略gif图片
                .setUseMemCache(true)
                .setLoadingDrawableId(R.mipmap.jiazai)// 下载中显示的图片
                .setFailureDrawableId(R.mipmap.jiazai)// 下载失败显示图片
                .setImageScaleType(ImageView.ScaleType.FIT_XY).build();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(context, R.layout.item_lunbotu, null);
        ImageView pic = (ImageView) view
                .findViewById(R.id.imageview_lunbotuitem_pic);
        if (picUrlList.size() != 0) {
            x.image().bind(pic, picUrlList.get(position % picUrlList.size()).getCover(), options);
        }

        container.addView(view);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Toast.makeText(context,""+position % picUrlList.size(),Toast.LENGTH_SHORT).show();
//                Intent lunbotuToDetails = new Intent(context, DetailsActivity.class);
//                lunbotuToDetails.putExtra("id", position % picUrlList.size());
//                context.startActivity(lunbotuToDetails);
//            }
//        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
