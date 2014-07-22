package com.quadrupapps.motorbiketelemetrty.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class AngleIndicator extends View {

	private int SPESSORE_BARRA = 0;
	
	private RectF areaBarraColorata   = new RectF();
	private Paint trattoBarraColorata = new Paint();
	
	TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
	
	private float motoAngle = 0;

	public AngleIndicator(Context context) {
		super(context);	
	}
	
	public AngleIndicator(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);	
	}
	
	
	int larg, alt;
	int color = Color.GRAY;
	int r,g,b;
	
	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		
		larg = getWidth();
		alt = getHeight();
		
		//Log.i("","larg " + larg + "  alt " + alt );
		
		canvas.drawColor(Color.TRANSPARENT);

		textPaint.setTextAlign(Paint.Align.CENTER); //Draw text from center
		textPaint.setTextSize(100);
		textPaint.setColor(Color.WHITE);
		
		SPESSORE_BARRA = larg / 12;
		
		areaBarraColorata.set(3*(larg/16), alt - (alt / 2), 14 * (larg  / 16), (alt /8) * 16);

		trattoBarraColorata.setAntiAlias(true);
		trattoBarraColorata.setColor(Color.GRAY);
		trattoBarraColorata.setStrokeWidth(SPESSORE_BARRA);
		trattoBarraColorata.setStyle(Paint.Style.STROKE);
		
		for(int i=-180; i<-90; i++){
			r = (int) map(i,-180,-90,255,0);
			g = (int) map(i,-180,-90,0,255);
			b = 0;
			color = Color.rgb(r, g, b);
			trattoBarraColorata.setColor(color);
			canvas.drawArc(areaBarraColorata, i, 1, false, trattoBarraColorata);
		}
		
		for(int i=-90; i<0; i++){
			r = (int) map(i, -90,0,0,255);
			g = (int) map(i,-90 ,0,255,0);
			b = 0;
			color = Color.rgb(r, g, b);
			trattoBarraColorata.setColor(color);
			canvas.drawArc(areaBarraColorata, i, 1, false, trattoBarraColorata);
		}
		
		trattoBarraColorata.setColor(Color.WHITE);
		canvas.drawArc(areaBarraColorata, motoAngle-85, -10, false, trattoBarraColorata);
		
		canvas.drawText((int)Math.abs(motoAngle) + "", larg / 2, alt - 50, textPaint);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setMotoAngle(float angle){
		motoAngle = angle;	
		invalidate();
	}
	
	private float map(float x, float fromMin, float fromMax, float toMin, float toMax) {
		float res = (((toMax - toMin)*(x - fromMin)) / (fromMax - fromMin)) + toMin;
		return res;
	}
	
}
