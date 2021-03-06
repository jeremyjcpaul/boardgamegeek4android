package com.boardgamegeek.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.boardgamegeek.R;
import com.boardgamegeek.util.MathUtils;

import java.text.DecimalFormat;

public class ScoreGraphView extends View {
	private static final DecimalFormat SCORE_FORMAT = new DecimalFormat("0");
	private static final int SCORE_STROKE_WIDTH = 2;

	private Paint barPaint;
	private Paint scorePaint;
	private Paint textPaint;
	private float scoreRadius;
	private float smallTickHeight;
	private float largeTickHeight;
	private int lowScoreColor;
	private int averageScoreColor;
	private int averageWinScoreColor;
	private int highScoreColor;

	private double lowScore;
	private double averageScore;
	private double averageWinScore;
	private double highScore;
	private double personalLowScore;
	private double personalAverageScore;
	private double personalAverageWinScore;
	private double personalHighScore;
	private boolean hasPersonalScores;

	public ScoreGraphView(Context context) {
		super(context);
		init(context);
	}

	public ScoreGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		barPaint.setColor(Color.BLACK);
		barPaint.setStrokeWidth(1);

		scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		scorePaint.setStrokeWidth(SCORE_STROKE_WIDTH);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(ContextCompat.getColor(getContext(), R.color.secondary_text));
		textPaint.setTextSize(8f * getContext().getResources().getDisplayMetrics().scaledDensity);

		scoreRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
		smallTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
		largeTickHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());

		lowScoreColor = ContextCompat.getColor(getContext(), R.color.score_low);
		averageScoreColor = ContextCompat.getColor(getContext(), R.color.score_average);
		averageWinScoreColor = ContextCompat.getColor(getContext(), R.color.score_average_win);
		highScoreColor = ContextCompat.getColor(getContext(), R.color.score_high);
	}

	public void setLowScore(double lowScore) {
		this.lowScore = lowScore;
	}

	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}

	public void setAverageWinScore(double averageWinScore) {
		this.averageWinScore = averageWinScore;
	}

	public void setHighScore(double highScore) {
		this.highScore = highScore;
	}

	public void setPersonalLowScore(double lowScore) {
		this.personalLowScore = lowScore;
		this.hasPersonalScores = true;
	}

	public void setPersonalAverageScore(double averageScore) {
		this.personalAverageScore = averageScore;
		this.hasPersonalScores = true;
	}

	public void setPersonalAverageWinScore(double averageWinScore) {
		this.personalAverageWinScore = averageWinScore;
		this.hasPersonalScores = true;
	}

	public void setPersonalHighScore(double highScore) {
		this.personalHighScore = highScore;
		this.hasPersonalScores = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final float y = canvas.getHeight() / 2;
		final float left = scoreRadius + SCORE_STROKE_WIDTH;
		final float right = canvas.getWidth() - scoreRadius - SCORE_STROKE_WIDTH;

		canvas.drawLine(left, y, right, y, barPaint);

		// ticks
		int tickSpacing;
		final double scoreSpread = highScore - lowScore;
		if (scoreSpread <= 20) {
			tickSpacing = 1;
		} else if (scoreSpread <= 50) {
			tickSpacing = 5;
		} else if (scoreSpread <= 200) {
			tickSpacing = 10;
		} else if (scoreSpread <= 500) {
			tickSpacing = 20;
		} else {
			tickSpacing = MathUtils.significantDigits((int) (Math.ceil(scoreSpread / 100) * 10), 2);
		}
		double tickScore = Math.ceil(lowScore / tickSpacing) * tickSpacing;
		while (tickScore <= highScore) {
			float x = (float) ((tickScore - lowScore) / (scoreSpread) * (right - left) + left);
			final float tickHeight;
			if ((tickScore % (5 * tickSpacing)) == 0) {
				tickHeight = largeTickHeight;
				final String label = SCORE_FORMAT.format(tickScore);
				float labelWidth = textPaint.measureText(label);
				float labelLeft = MathUtils.constrain(x - labelWidth / 2, 0, canvas.getWidth() - labelWidth);
				canvas.drawText(label, labelLeft, canvas.getHeight(), textPaint);
			} else {
				tickHeight = smallTickHeight;
			}
			canvas.drawLine(x, y - tickHeight, x, y + tickHeight, barPaint);
			tickScore += tickSpacing;
		}

		// score dots
		scorePaint.setStyle(hasPersonalScores() ? Style.STROKE : Style.FILL);
		drawScore(canvas, y, left, right, lowScore, lowScoreColor);
		drawScore(canvas, y, left, right, highScore, highScoreColor);
		drawScore(canvas, y, left, right, averageScore, averageScoreColor);
		drawScore(canvas, y, left, right, averageWinScore, averageWinScoreColor);

		if (hasPersonalScores()) {
			scorePaint.setStyle(Style.FILL);
			drawScore(canvas, y, left, right, personalLowScore, lowScoreColor);
			drawScore(canvas, y, left, right, personalHighScore, highScoreColor);
			drawScore(canvas, y, left, right, personalAverageScore, averageScoreColor);
			drawScore(canvas, y, left, right, personalAverageWinScore, averageWinScoreColor);
		}
	}

	private void drawScore(Canvas canvas, float y, float left, float right, double score, @ColorInt int color) {
		scorePaint.setColor(color);
		float x = (float) ((score - lowScore) / (highScore - lowScore) * (right - left) + left);
		canvas.drawCircle(x, y, scoreRadius, scorePaint);
	}

	private boolean hasPersonalScores() {
		return hasPersonalScores;
	}
}
