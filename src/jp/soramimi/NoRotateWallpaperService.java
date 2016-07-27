package jp.soramimi;

import jp.soramimi.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class NoRotateWallpaperService extends WallpaperService {

    private final Handler mHandler = new Handler();

	@Override
	public Engine onCreateEngine()
	{
		return new NoRotateEngine();
	}

    public class NoRotateEngine extends Engine {
        
        private Bitmap image;
        private int width;
        private int height;

        private final Runnable drawRunnable = new Runnable() {
            public void run(){
                drawFrame();
            }
        };

        private boolean visible = false;
        
        public NoRotateEngine(){
			int r = R.drawable.wallpaper;
            image = BitmapFactory.decodeResource(getResources(), r);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
        	super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();
            mHandler.removeCallbacks(drawRunnable);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            this.visible = visible;
            if(this.visible){
                drawFrame();
            }else{
                mHandler.removeCallbacks(drawRunnable);
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder)
        {
        	super.onSurfaceCreated(surfaceHolder);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width , int height){
            super.onSurfaceChanged(holder, format, width, height);
            this.width = width;
            this.height = height;
            drawFrame();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceCreated(holder);
            visible = false;
            mHandler .removeCallbacks(drawRunnable);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels)
        {
        	drawFrame();
        }

        private void drawFrame()
        {
            final SurfaceHolder holder = getSurfaceHolder();
            
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                	boolean landscape = height < width;
                    int sw = image.getWidth();
                    int sh = image.getHeight();
                    int dw = width;
                    int dh = height;
                    if (landscape) {
                    	dw = height;
                    	dh = width;
                        c.save(Canvas.MATRIX_SAVE_FLAG);
                        int n = height / 2;
                    	c.rotate(-90, n, n);
                    }
                    c.drawBitmap(image, new Rect(0, 0, sw, sh), new Rect(0, 0, dw, dh), null);
                    if (landscape) {
                    	c.restore();
                    }
                }
            } finally {
                if (c != null){
                    holder.unlockCanvasAndPost(c);
                }
            }
        }
    }

}
