package com.yongyida.robot.photos.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by tianchunming on 2017/7/25.
 */

public class KCornerDialog extends Dialog {
    private static int default_width = 160; // 默认宽度
    private static int default_height = 120;// 默认高度

    public KCornerDialog(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public KCornerDialog(Context context, int width, int height, View layout, int style) {
        super(context, style);
        // 加载布局
        setContentView(layout);
        // 设置Dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
