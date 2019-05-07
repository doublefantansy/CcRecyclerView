package hzkj.cc.ccrecyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public static int HEADERTYPE = -1;
    public static int FOOTTYPE = 1;
    RecyclerView.Adapter adapter;
    FootHolder footholder;
    HeaderHolder headerHolder;
    ObjectAnimator footerAnimator;
    ObjectAnimator headAnimator;
    ValueAnimator animator;
    int headerHeight;
    int footerHeight;

    public MyAdapter(RecyclerView.Adapter adapter, Context context) {
        // 初始化变量
        this.adapter = adapter;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADERTYPE;
        } else if (position == getItemCount() - 1) {
            return FOOTTYPE;
        } else {
            return adapter.getItemViewType(position);
        }
    }

    void smoothDown(String text) {
        footholder.tips.setVisibility(View.GONE);
        footholder.loadingText.setText(text);
        footholder.loadingText.setTextColor(context.getResources()
                .getColor(R.color.red));
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(footholder.itemView.getHeight(), 0);
                animator.setDuration(200);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams layoutParams = footholder.itemView.getLayoutParams();
                        layoutParams.height = (int) animation.getAnimatedValue();
                        footholder.itemView.requestLayout();
                    }
                });
                animator.start();
            }
        }, 1000);
    }

    //
    public void smoothUp(final boolean toTop, String text) {
        int delay = 0;
        if (text != null
                ) {
            headerHolder.loading.setVisibility(View.GONE);
            headerHolder.text.setText(text);
            if (text.equals("刷新失败")) {
                headerHolder.text.setTextColor(context.getResources()
                        .getColor(R.color.myGray));
            } else {
                headerHolder.text.setTextColor(context.getResources()
                        .getColor(R.color.myBlue));
            }
            delay = 1000;
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(headerHolder.itemView.getHeight(), toTop ? 0 : headerHeight);
                animator.setDuration(200);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams layoutParams = headerHolder.itemView.getLayoutParams();
                        layoutParams.height = (int) animation.getAnimatedValue();
                        headerHolder.itemView.requestLayout();
                    }
                });
                animator.start();
            }
        }, delay);
    }

    public void showHeader(boolean showLoading, boolean isSuccess) {
        if (showLoading) {
            smoothUp(false, null);
            headerHolder.loading.setVisibility(View.VISIBLE);
            headerHolder.text.setText("正在刷新中");
            ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            smoothUp(true, isSuccess ? "刷新成功" : "刷新失败");
        }
    }

    public void showFooter(boolean b) {
        footholder.loadingText.setTextColor(context.getResources()
                .getColor(R.color.myGray));
        footholder.tips.setVisibility(View.VISIBLE);
        footholder.loadingText.setText("正在加载中");
        ViewGroup.LayoutParams layoutParams = footholder.itemView.getLayoutParams();
        if (b) {
            layoutParams.height = (int) footerHeight;
        } else {
            layoutParams.height = 0;
        }
        footholder.itemView.setLayoutParams(layoutParams);
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        private ImageView loading;
        private RelativeLayout layout;
        private TextView text;

        public HeaderHolder(final View itemView) {
            super(itemView);
            loading = itemView.findViewById(R.id.loading);
            layout = itemView.findViewById(R.id.pullToRefreshPart);
            text = itemView.findViewById(R.id.pullToRefreshText);
            headerHeight = itemView.getLayoutParams().height;
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = 0;
            itemView.setLayoutParams(layoutParams);
        }
    }

    public boolean move(float distance) {
        headerHolder.text.setTextColor(context.getResources()
                .getColor(R.color.myGray));
        headerHolder.loading.setVisibility(View.GONE);
        ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams()).removeRule(RelativeLayout.CENTER_IN_PARENT);
        ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        headerHolder.text.setText("下拉可刷新");
        if (distance < 0) {
            return false;
        }
        ViewGroup.LayoutParams layoutParams = headerHolder.itemView.getLayoutParams();
        layoutParams.height = (int) distance;
        if (headerHolder.itemView.getHeight() >= headerHeight) {
            headerHolder.text.setText("释放可刷新");
            headerHolder.itemView.setLayoutParams(layoutParams);
            return true;
        } else {
            headerHolder.text.setText("下拉可刷新");
            headerHolder.itemView.setLayoutParams(layoutParams);
            return false;
        }
    }

    class FootHolder extends RecyclerView.ViewHolder {
        private ImageView tips;
        TextView loadingText;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.loading);
            loadingText = itemView.findViewById(R.id.loadingText);
            footerHeight = itemView.getLayoutParams().height;
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = 0;
            Log.d("ccnb111", footerHeight + "");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTTYPE) {
            footholder = new FootHolder(LayoutInflater.from(context)
                    .inflate(R.layout.footview, parent, false));
            footerAnimator = ObjectAnimator.ofFloat(((FootHolder) footholder).tips, "rotation", 360f);
            footerAnimator.setDuration(2000);
            footerAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            footerAnimator.start();
            return footholder;
        } else if (viewType == HEADERTYPE) {
            headerHolder = new HeaderHolder(LayoutInflater.from(context)
                    .inflate(R.layout.headview, parent, false));
            headAnimator = ObjectAnimator.ofFloat(headerHolder.loading, "rotation", 360f);
            headAnimator.setDuration(2000);
            headAnimator.setRepeatCount(ValueAnimator.INFINITE);
            return headerHolder;
        } else {
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FootHolder) {
            footerAnimator.start();
        } else if (holder instanceof HeaderHolder) {
            headAnimator.start();
        } else {
            adapter.onBindViewHolder(holder, position - 1);
        }
    }
}

