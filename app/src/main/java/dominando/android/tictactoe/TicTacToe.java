package dominando.android.tictactoe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TicTacToe extends View {

    public static final int X = 1;
    public static final int O = 2;
    private static final int EMPTY = 0;
    public static final int DRAW = 3;

    int size;
    int turn;
    int[][] mesh;

    Paint paint;
    Bitmap imageX;
    Bitmap imageO;

    GestureDetector detector;

    TicTacToeListener listener;

    public TicTacToe(Context context, AttributeSet attrs) {
        super(context, attrs);
        turn = X;
        mesh = new int[3][3];

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        detector = new GestureDetector(this.getContext(), new TicTacToeTouchListener());

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        //references the images from the drawable folder
        imageX = BitmapFactory.decodeResource(getResources(), R.drawable.x_mark);
        imageO = BitmapFactory.decodeResource(getResources(), R.drawable.o_mark);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mesh = null;
        paint = null;
        imageX = null;
        imageO = null;
        detector = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            Resources resources = getResources();
            float squaredCell = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, resources.getDisplayMetrics());
            size = (int) squaredCell * 3;
        } else if(getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
            size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        } else {
            size = getLayoutParams().width;
        }

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int squaredCell = size / 3;

        //drawing the lines;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        //vertical
        canvas.drawLine(squaredCell, 0, squaredCell, size, paint);
        canvas.drawLine(squaredCell * 2, 0, squaredCell * 2, size, paint);

        //horizontal
        canvas.drawLine(0, squaredCell, size, squaredCell, paint);
        canvas.drawLine(0, squaredCell * 2, size, squaredCell * 2, paint);

        for(int line = 0; line < 3; line++) {
            for(int column = 0; column < 3; column++) {
                int x = column * squaredCell;
                int y = line * squaredCell;

                Rect rect = new Rect(x, y, x + squaredCell, y + squaredCell);
                if(mesh[line][column] == X) {
                    canvas.drawBitmap(imageX, null, rect, null);
                } else if (mesh[line][column] == O){
                    canvas.drawBitmap(imageO, null, rect, null);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public void restartGame() {
        mesh = new int[3][3];
        invalidate();
    }

    public void setListener(TicTacToeListener listener) {
        this.listener = listener;
    }

    private int gameOver() {
        //horizontals
        if(won(mesh[0][0], mesh[0][1], mesh[0][2])) {
            return mesh[0][0];
        }
        if(won(mesh[1][0], mesh[1][1], mesh[1][2])) {
            return mesh[1][0];
        }
        if(won(mesh[2][0], mesh[2][1], mesh[2][2])) {
            return mesh[2][0];
        }

        //verticals
        if(won(mesh[0][0], mesh[1][0], mesh[2][0])) {
            return mesh[0][0];
        }
        if(won(mesh[0][1], mesh[1][1], mesh[2][1])) {
            return mesh[0][1];
        }
        if(won(mesh[0][2], mesh[1][2], mesh[2][2])) {
            return mesh[0][2];
        }

        //diagonals
        if(won(mesh[0][0], mesh[1][1], mesh[2][2])) {
            return mesh[0][0];
        }
        if(won(mesh[0][2], mesh[1][1], mesh[2][0])) {
            return mesh[0][2];
        }

        //are there empty spaces
        for(int line = 0; line < mesh.length; line++) {
            for(int column = 0; column < mesh[line].length; column++) {
                if(mesh[line][column] == EMPTY) {
                    return EMPTY;
                }
            }
        }
        return DRAW;
    }

    private boolean won(int a, int b, int c) {
        return (a == b && b == c && a != EMPTY);
    }

    public interface TicTacToeListener {
        void endTheGame(int winner);
    }

    class TicTacToeTouchListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            int winner = gameOver();

            if(event.getAction() == MotionEvent.ACTION_UP && winner == EMPTY) {
                int squaredCell = size / 3;

                int line = (int) (event.getY() / squaredCell);
                int column = (int) (event.getX() / squaredCell);

                if(mesh[line][column] == EMPTY) {
                    mesh[line][column] = turn;
                    turn = (turn == X)? O:X;
                    invalidate();

                    winner = gameOver();

                    if(winner != EMPTY) {
                        if(listener != null) {
                            listener.endTheGame(winner);
                        }
                    }

                    return true;
                }
            }

            return super.onSingleTapUp(event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}

