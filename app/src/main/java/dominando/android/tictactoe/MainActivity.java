package dominando.android.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TicTacToe.TicTacToeListener, View.OnClickListener{

    TicTacToe game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game = (TicTacToe) findViewById(R.id.ticTacToe);
        game.setListener(this);
        findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void endTheGame(int winner) {
        String message;

        switch (winner) {
            case TicTacToe.X:
                message = "X wins";
                break;
            case TicTacToe.O:
                message = "O wins";
                break;
            default:
                message = "No winner!";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        game.restartGame();
    }
}