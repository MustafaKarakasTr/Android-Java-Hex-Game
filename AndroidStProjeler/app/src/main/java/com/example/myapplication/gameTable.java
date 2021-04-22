package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class gameTable extends AppCompatActivity {
    private int size, width, height;
    private boolean against_cpu;
    private int counter = 0;
    private Button[][] buttons;
    private Button undo;
    private Button reset;
    private Button save;
    private Button load;
    private int level = 1;
    int MaximizingPlayer = 1;
    int MinimizingPlayer = -1;
    int[][] indexHelper={{-1,0},{-1,+1},{0,-1},{1,-1},{1,0},{0,1}};

    int[][] trB;
    int[][] trW;

    private char[][] hexCells;
    private RelativeLayout relativeLayout;
    private boolean[][] check_table = null;
    private boolean game_continue = true;
    private char currentPlayer;
    private ArrayList<int[]> movements; // holds movements for undo function

    private int getSize() {
        return size;
    }

    private char getCurrentPlayer() {
        return (counter % 2 == 0) ? 'x' : 'o';
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_table);
        setTitle("Hex Game");

        Intent intent = getIntent();

        size = intent.getIntExtra(MainActivity.SIZE, 0);
        against_cpu = intent.getBooleanExtra(MainActivity.AGAINST_CPU, false);
        if(against_cpu){
            level =intent.getIntExtra("LEVEL",-1);
        }
        // temporary information
        Toast toast = Toast.makeText(this, "Size :" + size + " You are playing against " + ((against_cpu)?"cpu":"user"), Toast.LENGTH_SHORT);
        toast.show();
        /*if(against_cpu){
            Toast t = Toast.makeText(this, "Level :" + level, Toast.LENGTH_SHORT);
            t.show();
        }*/
        showButtons();
        setContentView(relativeLayout);


    }

    private void showButtons() {
        buttons = new Button[size][size];

        hexCells = new char[size][size];
        trB = new int[size][size];
        trW = new int[size][size];
        check_table = new boolean[size][size];
        movements = new ArrayList<int[]>();

        relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        //Toast.makeText(getApplicationContext(), "width , height" + relativeLayout.getWidth() + relativeLayout.getHeight(), Toast.LENGTH_SHORT).show();

        //----------
        setWidthHeight();
        int xStart, yStart;
        int xCurrent, yCurrent;
        //int WidthOfButtons = (int) (((double)width) / ((3.0 * size) / 2.0));
        int HeightOfButtons = height / size;
        int WidthOfButtons = (int) (((double) width) / (size + (size - 1) * 1.0 / 2));
        yStart = (height - (WidthOfButtons * size)) / 3;
        int shift = (int) (WidthOfButtons / 2.0);

        for (int i = 0; i < size; i++) {
            //  buttons[i] = new Button[size];
            for (int j = 0; j < size; j++) {
                check_table[i][j] = new Boolean(false);
                buttons[i][j] = new Button(this);
                //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                buttons[i][j].setLayoutParams(new RelativeLayout.LayoutParams(WidthOfButtons - 5, WidthOfButtons - 5));
                buttons[i][j].setX(j * WidthOfButtons + i * shift);
                buttons[i][j].setY(i * WidthOfButtons + yStart);

                //buttons[i][j].setText(".");
                buttons[i][j].setBackgroundColor(Color.GRAY);
                hexCells[i][j] = '.';
                relativeLayout.addView(buttons[i][j]);

                buttons[i][j].setId(i * size + j);
                buttons[i][j].setOnClickListener(getOnClick(i, j));

                //buttons[i][j].setWidth(5);
                //buttons[i][j].setHeight(5);


            }
        }

        //Toast.makeText(getApplicationContext(), "width,height " + width + " " + height, Toast.LENGTH_SHORT).show();
        addUndoButton();
        addResetButton();
        addSaveButton();
        addLoadButton();
    }

    private void addUndoButton() {
        undo = new Button(this);
        undo.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        undo.setX(0);
        undo.setY(height - 500);

        undo.setText("Undo");
        //undo.setBackgroundColor(Color.GRAY);
        relativeLayout.addView(undo);

        undo.setId(1);
        undo.setOnClickListener(getOnClick('u'));
    }

    private void addResetButton() {
        reset = new Button(this);
        reset.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        reset.setX(200);
        reset.setY(height - 500);

        reset.setText("Reset");
        //undo.setBackgroundColor(Color.GRAY);
        relativeLayout.addView(reset);

        reset.setId(2);
        reset.setOnClickListener(getOnClick('r'));
    }

    private void addSaveButton() {
        save = new Button(this);
        save.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        save.setX(400);
        save.setY(height - 500);

        save.setText("Save");
        //undo.setBackgroundColor(Color.GRAY);
        relativeLayout.addView(save);

        save.setId(3);
        save.setOnClickListener(getOnClick('s'));
    }

    private void addLoadButton() {
        load = new Button(this);
        load.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        load.setX(600);
        load.setY(height - 500);

        load.setText("Load");
        //undo.setBackgroundColor(Color.GRAY);
        relativeLayout.addView(load);

        load.setId(3);
        load.setOnClickListener(getOnClick('l'));
    }

    private void setWidthHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    private View.OnClickListener getOnClick(final int i, final int j) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game_continue) {
                    Button tempButton = buttons[i][j];

                    ColorDrawable buttonColor = (ColorDrawable) buttons[i][j].getBackground();
                    int colorId = buttonColor.getColor();
                    if (colorId == Color.GRAY) {
                        if (counter % 2 == 0) {
                            tempButton.setBackgroundColor(Color.RED);
                            hexCells[i][j] = 'o';
                        } else {
                            tempButton.setBackgroundColor(Color.BLUE);
                            hexCells[i][j] = 'x';

                        }
                        char check = check();

                        if (check != '\0') {
                            Toast.makeText(getApplicationContext(), "Winner " + getCurrentPlayer(), Toast.LENGTH_SHORT).show();
                            showWinner(check);
                            game_continue = false;
                        }
                        counter++; // if the game ends do not increase counter
                        int[] temp = {i, j};
                        movements.add(temp);

                        if (against_cpu && game_continue) {
                            playAI();
                            if (check() != '\0') {
                                Toast.makeText(getApplicationContext(), "Winner " + getCurrentPlayer(), Toast.LENGTH_SHORT).show();
                                showWinner(check);
                                game_continue = false;
                            }
                            counter++;
                        }

                    }
                }
            }

        };
    }

    private void showWinner(char winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (against_cpu && winner == 'o') builder.setTitle("Nice Try");
        else {
            char playerNum = (winner == 'x') ? '1' : '2';
            builder.setTitle("Congratulations! Winner : Player " + playerNum);
        }
        builder.setMessage("Do you want to restart the game ? ");
        builder.setNegativeButton("No", null);
        //builder.setPositiveButton("Evet",null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                //Uri uri = Uri.fromParts("package", getPackageName(), null);
                //intent.setData(uri);
                startActivity(intent);*/
                reset(null);

            }
        });

        builder.show();
    }

    //-----------------
    private View.OnClickListener getOnClick(final char key) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (key == 'u') {
                    if(movements.size() %2 == 0 || against_cpu == false) // if against_cpu and player started the game and the player is won ,then # player's moves are 1 more then CPU's
                        undo(v);
                    undo(v);
                    if (game_continue == false) {
                            /*for(int i=0;i<movements.size();i++)
                                Toast.makeText(getApplicationContext(), movements.get(i)[0] +" "+ movements.get(i)[1], Toast.LENGTH_SHORT).show();
                            */
                        game_continue = true;

                        int x, y;
                        for (int i = 0; i < movements.size(); i++) {
                            x = movements.get(i)[0];
                            y = movements.get(i)[1];
                            if (hexCells[x][y] == 'o') {
                                buttons[x][y].setBackgroundColor(Color.RED);
                                //Toast.makeText(getApplicationContext(), movements.get(movements.size()-1)[0] +" "+ movements.get(movements.size()-1)[1], Toast.LENGTH_SHORT).show();

                            } else if (hexCells[x][y] == 'x') {
                                buttons[x][y].setBackgroundColor(Color.BLUE);
                                //Toast.makeText(getApplicationContext(), movements.get(movements.size()-1)[0] +" "+ movements.get(movements.size()-1)[1], Toast.LENGTH_SHORT).show();

                            }
                        }
                    }


                } else if (key == 'r') {
                    reset(v);
                    game_continue = true;
                } else if (key == 's') {
                    AlertDialog.Builder builder = new AlertDialog.Builder(gameTable.this);

                    builder.setTitle("Save Game");

                    builder.setMessage("Please enter the file name: ");
                    final EditText input = new EditText(gameTable.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setNegativeButton("Dismiss", null);
                    builder.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String file_name = input.getText().toString();
                            saveGame(file_name);

                        }
                    });

                    builder.show();

                } else if (key == 'l') {
                    AlertDialog.Builder builder = new AlertDialog.Builder(gameTable.this);

                    builder.setTitle("Load Game");

                    builder.setMessage("Please enter the file name: ");
                    final EditText input = new EditText(gameTable.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setNegativeButton("Dismiss", null);
                    builder.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String file_name = input.getText().toString();
                            loadGame(file_name);

                        }
                    });

                    builder.show();
                }

            }

        };
    }

    //---------------
    private void saveGame(String fileName) {
        if(fileName.isEmpty()){
            Toast.makeText(gameTable.this, "Please enter a game's name", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream file = openFileOutput(fileName, MODE_PRIVATE);
            int against_cpu_int = (against_cpu) ? 1 : 0;

            OutputStreamWriter outputFile = new OutputStreamWriter(file);
            outputFile.write(against_cpu_int + "\n" + getSize() + "\n" + getCurrentPlayer() + "\n" + counter + "\n");
            for (int i = 0; i < getSize(); i++) {
                for (int j = 0; j < getSize(); j++) {
                    outputFile.write(hexCells[i][j] + "\n");
                }
                //outputFile.write("\n");
            }
            outputFile.write("-1\n");
            for (int i = 0; i < counter; i++) {
                //fp<<movements[i].getPosition()<<" "<<movements[i].getRow()<<movements[i].getVariable()<<endl;
                outputFile.write(movements.get(i)[0] + "\n" + movements.get(i)[1] + "\n" + hexCells[movements.get(i)[0]][movements.get(i)[1]] + "\n");
            }
            outputFile.write("-1\n");
            outputFile.flush();
            outputFile.close();
            Toast.makeText(gameTable.this, "Game Saved Succesfully", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(gameTable.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void loadGame(String fileName) {
        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(gameTable.this, "The game is not found. Be sure that you have saved the game before try to load it", Toast.LENGTH_SHORT).show();

            return;
        }
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            line = bReader.readLine();
            if (line.charAt(0) == '1') {
                against_cpu = true;
            } else {
                against_cpu = false;
            }

            line = bReader.readLine();
            size = Integer.parseInt(line);

            line = bReader.readLine();
            currentPlayer = line.charAt(0);
            line = bReader.readLine();
            counter = Integer.parseInt(line);
            Toast.makeText(gameTable.this, size + " " + against_cpu + " " + currentPlayer + " " + counter, Toast.LENGTH_SHORT).show();
            buttons = null;
            hexCells = null;
            check_table = null;
            movements = null;
            relativeLayout = null;
            showButtons();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    line = bReader.readLine();
                    hexCells[i][j] = line.charAt(0);
                    if (hexCells[i][j] == 'o') {
                        buttons[i][j].setBackgroundColor(Color.RED);
                        //Toast.makeText(getApplicationContext(), movements.get(movements.size()-1)[0] +" "+ movements.get(movements.size()-1)[1], Toast.LENGTH_SHORT).show();
                    } else if (hexCells[i][j] == 'x') {
                        buttons[i][j].setBackgroundColor(Color.BLUE);
                        //Toast.makeText(getApplicationContext(), movements.get(movements.size()-1)[0] +" "+ movements.get(movements.size()-1)[1], Toast.LENGTH_SHORT).show();
                    } else {
                        buttons[i][j].setBackgroundColor(Color.GRAY);

                    }

                }
            }
            bReader.readLine(); //reads -1
            //counter should be 1 less to check the last move

            counter--;
            char check = check();
            counter++;
            //Toast.makeText(getApplicationContext(), "Winner " + getCurrentPlayer() + "check : -"+ check+"-" , Toast.LENGTH_SHORT).show();

            if (check != '\0') {
                //Toast.makeText(getApplicationContext(), "Winner " + ((counter %2 == 0) ? "Red" : "Blue"), Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(), "Winner " + check, Toast.LENGTH_SHORT).show();
                showWinner(check);
                game_continue = false;
            } else
                game_continue = true;
            String xCoor;
            String yCoor;
            String val;
            for (int i = 0; i < counter; i++) {
                xCoor = bReader.readLine();
                yCoor = bReader.readLine();
                val = bReader.readLine();
                int[] temp = new int[2];
                temp[0] = Integer.parseInt(xCoor);
                temp[1] = Integer.parseInt(yCoor);
                //int [] temp = {i,j};
                movements.add(temp);


            }
            bReader.readLine(); //reads -1

            setContentView(relativeLayout);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private char buttonHolds(int i, int j) {
        Button tempButton = buttons[i][j];

        ColorDrawable buttonColor = (ColorDrawable) buttons[i][j].getBackground();
        int colorId = buttonColor.getColor();
        if (colorId == Color.GRAY) return '\0';
        if (colorId == Color.RED) return 'x';
        if (colorId == Color.BLUE) return 'o';
        return '\0';
    }

    private char check() {
        boolean reach_to_end = false;
        int Size = getSize();
        char player = getCurrentPlayer();
        if (player == 'x') {

            // If it goes A to end then playerX wins, x value should be A to end that means we should go table[y][0] to table[y][size-1] consistently
            for (int i = 0; i < Size; i++) {
                if (buttonHolds(i, 0) == 'x') {
                    // if(buttons[i][0].getBackground().equals(Color.RED)){

                    reach_to_end = check_neighbors(i, 0);
                    if (reach_to_end == true) {
                        showtheWinnerPath(i, 0);
                        return player;
                    }
                }
            }

        } else if (player == 'o') {
            for (int i = 0; i < Size; i++) {
                if (buttonHolds(0, i) == 'o') {
                    //if(buttons[0][i].getBackground().equals(Color.BLUE)){
                    reach_to_end = check_neighbors(0, i);
                    if (reach_to_end == true) {
                        showtheWinnerPath(0, i);
                        //makeUpperCase(0,i);
                        return player;
                    }
                }


            }
        }
        return '\0';
    }

    boolean check_neighbors(int y, int x) {
        int Size = getSize();
        char player = getCurrentPlayer();
        int temp = (player == 'x') ? Color.RED : Color.BLUE;
        //!(buttons[y][x].getBackground().equals(temp))
        if (y < 0 || x < 0 || x >= Size || y >= Size || buttonHolds(y, x) != player || check_table[y][x] == true)
            return false;
        if ((player == 'x' && x == Size - 1) || (player == 'o' && y == Size - 1)) //we reach to destination
            return true;


        check_table[y][x] = true; /* to protect infinite loop we put 'true' to our initial position so that these recursive calls cannot call it again because of the first if statement:*/
        boolean b1 = check_neighbors(y - 1, x);
        boolean b2 = check_neighbors(y - 1, x + 1);
        boolean b3 = check_neighbors(y, x - 1);
        boolean b4 = check_neighbors(y + 1, x - 1);
        boolean b5 = check_neighbors(y + 1, x);
        boolean b6 = check_neighbors(y, x + 1);
        check_table[y][x] = false; // clearing table
        boolean won = b1 || b2 || b3 || b4 || b5 || b6;//if one of them is true,then won is true
        return (won); //if one of them true return true else return false
    }

    void showtheWinnerPath(int y, int x) {
        int Size = getSize();
        char player = getCurrentPlayer();
        if (y < 0 || x < 0 || x >= Size || y >= Size || buttonHolds(y, x) != player) // hexCells[y][x]!=player checks if its . or other player or this position already was made uppercase
            return;
        //buttonHolds[y][x].showWinnerPath();
        changeWinnerColor(y, x);
        //hexCells[y][x]+=('A'-'a');
        showtheWinnerPath(y - 1, x);
        showtheWinnerPath(y - 1, x + 1);
        showtheWinnerPath(y, x - 1);
        showtheWinnerPath(y + 1, x - 1);
        showtheWinnerPath(y + 1, x);
        showtheWinnerPath(y, x + 1);
    }

    void changeWinnerColor(int y, int x) {
        if (buttonHolds(y, x) == 'x') {
            buttons[y][x].setBackgroundColor(Color.MAGENTA);
        } else {
            buttons[y][x].setBackgroundColor(Color.CYAN);

        }
    }

    public void undo(View v) {
        if (counter == 0)
            return;
        hexCells[movements.get(movements.size() - 1)[0]][movements.get(movements.size() - 1)[1]] = '.';
        buttons[movements.get(movements.size() - 1)[0]][movements.get(movements.size() - 1)[1]].setBackgroundColor(Color.GRAY);

        removeLast();
        counter--;
        //movements.get(counter-1).setVariable(Empty);
        /*if(counter>1)
            removeLast();
            //movements.get(counter-2).setVariable(Empty);

        //hexCells[movements[counter-1].getRow()][movements[counter-1].getPosition()].setVariable(Empty);
        if(counter>1){
            hexCells[movements.get(counter-2)[0]][movements.get(counter-2)[1]] = '.';
            //hexCells[movements[counter-2].getRow()][movements[counter-2].getPosition()].setVariable(Empty);

        }
        if(counter>1){
            counter-=2;
        }
        else if(counter==1){
            counter--;
        }
        else{
            return;
        }*/
    }

    public void reset(View v) {
        game_continue = true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setBackgroundColor(Color.GRAY);
                hexCells[i][j] = '.';

            }
        }
        movements.clear();
        counter = 0;
    }

    void removeLast() {
        //Log.d("AAAAA",movements.get(movements.size()-1)[0]);
        //+toString(movements.get(movements.size()-1)[1]));
        movements.remove(movements.size() - 1);
    }

    private void showTable() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(hexCells[i][j] + " ");
            }
            System.out.print("\n");

        }
    }
    // ---------------------------------------- AI
    private void playAI(){
        int [] move = BestMove( );
        hexCells[move[0]][move[1]] = 'x';
        buttons[move[0]][move[1]].setBackgroundColor(Color.BLUE);
        movements.add(move);
    }
        private int [] BestMove(){
            int minval = Integer.MAX_VALUE; // INF
            int maxval = Integer.MIN_VALUE; // -INF

            int[] bestMove = new int[2];
            int i, j;
            int moveVal;
            bestMove[0] = -1;
            bestMove[1] = -1;
            int [] temp;

            for (int x = 0; x < movements.size(); x++){
                for (int y = 0; y < indexHelper.length; y++) {
                    temp = movements.get(x);
                    i = indexHelper[y][0] + temp[0];
                    j = indexHelper[y][1] + temp[1];
                    if (valid(i, j, size) == 1 && hexCells[i][j] == '.') {
                        hexCells[i][j] = 'o'; // it was 'b'
                        movements.add(new int[]{i, j});
                        moveVal = minimax(level,Integer.MIN_VALUE,Integer.MAX_VALUE, true);
                        movements.remove(movements.size()-1);

                        hexCells[i][j] = '.';
                        if (moveVal < minval) {

                            bestMove[0] = i;
                            bestMove[1] = j;
                            minval = moveVal;
                        }
                    }
                }
            }
            return bestMove;
        }
    int minimax(int depth, int alpha,int beta,boolean maxTurn) {
        if (depth == 0 || winnerFound()!=0)
            return staticEvaluation((maxTurn) ? 1: 0);

        if (BoardNotFull() == 0)
            return 0;
        int i,j;
        int [] temp;
        if (maxTurn) {
            Log.d("X",depth+" X");

            int maxEval = -Integer.MAX_VALUE;
            for (int x = 0; x < movements.size(); x++){
                for (int y = 0; y < indexHelper.length; y++) {
                    temp = movements.get(x);
                    i = indexHelper[y][0] + temp[0];
                    j = indexHelper[y][1] + temp[1];
                    if (valid(i, j, size) == 1 && hexCells[i][j] == '.') {

                        hexCells[i][j] = 'x';
                        movements.add(new int[]{i, j});
                        int eval =minimax(depth - 1,alpha,beta, false);
                        maxEval = maximum(maxEval, eval);
                        alpha = maximum(alpha,eval);
                        movements.remove(movements.size()-1);

                        hexCells[i][j] = '.';
                        if(beta <= alpha)
                            break;
                    }
                }
                if(beta<=alpha)
                        break;
            }
            return maxEval;
        } else {/* minimizing player */
            int minEval = Integer.MAX_VALUE;
            Log.d("O",depth+" O");

            for (int x = 0; x < movements.size(); x++){
                for (int y = 0; y < indexHelper.length; y++) {
                    temp = movements.get(x);
                    i = indexHelper[y][0] + temp[0];
                    j = indexHelper[y][1] + temp[1];

                    if (valid(i, j, size) == 1 && hexCells[i][j] == '.') {

                        hexCells[i][j] = 'o';
                        movements.add(new int[]{i, j});
                        int eval = minimax(depth - 1,alpha,beta, true);
                        minEval = minimum(minEval, eval);
                        beta = minimum(beta,eval);
                        movements.remove(movements.size()-1);

                        hexCells[i][j] = '.';
                        if(beta<=alpha) break;
                    }

                }
                if(beta<=alpha) break;

            }
            return minEval;
        }
    }
    private int winnerFound(){
        int[] s = new int[1];
        transformBlackMatrix();                    /* update trB matrix */
        transformWhiteMatrix();                    /* update trW matrix */
        if (WhiteWon(size,trW,s)!=0)
            return MaximizingPlayer;

        if (BlackWon(size, trB, s)!=0)
            return MinimizingPlayer;

        return 0;
    }
    private void transformBlackMatrix(){ /* for Black */
        /* returns a matrix of integers where (1) is the source node */
        /* and the rest of the elements with 'b' are initialized with 3 */
        /* if the case is none of the above then A[i][j] = 0 */
        /* free the memory when check for winner is completed */
        int i, j;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (j == 0 && hexCells[i][j] == 'o')
                    trB[i][j] = 1;    /* source nodes are j = 0 with 'b' */
                else if (hexCells[i][j] == 'o')
                    trB[i][j] = 3;
                else
                    trB[i][j] = 0;
            }

    }
    void transformWhiteMatrix() /* for  White */
        /* input array B is the array of chars with 'b', 'w' and '.' */
        /* returns a matrix of integers where (1) is the source node */
        /* and the rest of the elements with 'w' are initialized with 3 */
        /* if the case is none of the above then A[i][j] = 0 */
        /* free the memory when check for winner is completed */
    { int i, j;

        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (i == 0 && hexCells[i][j] == 'x')
                    trW[i][j] = 1;    /* source nodes are i = 0 with 'w' */
                else if (hexCells[i][j] == 'x')
                    trW[i][j] = 3;
                else
                    trW[i][j] = 0;
            }

    }
    int WhiteWon(int n, int[][] matrix, int[] seq)
        /* returns 1 if path was found else reutns 0 */
        /* seq is the max sequence of white pawns */
    {
        int i, j;
        int flag = 0;
        int[][] visited= new int [n][n];          /* keep track of already visited indexes */


        for (i = 0; i < n; i++) /* initialize visited (array) with false */
            for (j = 0; j < n; j++)      /* since we don't know the paths */
                visited[i][j] = 0;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (matrix[i][j] == 1 && visited[i][j] == 0) /* if source cell */
                    if (PathSearchWhite(i, j, n, matrix, visited) == 1) {
                        /* search */
                        flag = 1;                          /* path was found */
                    }

            }

        int diff = maxi(visited, n) - mini(visited, n);
        if (diff <= 0)
            seq[0] = 0;
        else
            seq[0] = diff + 1;


        visited = null;

        if (flag == 1)
            return 1;
        else
            return 0;  /* path was not found */

    }
    int PathSearchWhite(int i, int j, int n, int[][] matrix, int[][] visited)
    {
        if (valid(i, j, n)==1 && matrix[i][j] != 0 && visited[i][j] == 0) {
            /* terminal cases */
            visited[i][j] = 1;                            /* cell visited */
            if (matrix[i][j] == 3 && i == n-1)          /* if cell is the */
                return 1;        /* destination(the other end) return true */

            // traverse north
            /* if path is found in this direction */
            if (PathSearchWhite(i-1, j, n, matrix, visited) == 1)
                return 1;                                   /* return true */

            // traverse west
            /* if path is found in this direction */
            if (PathSearchWhite(i, j-1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse south
            /* if path is found in this direction */
            if (PathSearchWhite(i+1, j, n, matrix, visited)== 1)
                return 1;                                  /* return true */

            // traverse east
            /* if path is found in this direction */
            if (PathSearchWhite(i, j+1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse northeast
            /* if path is found in this direction */
            if (PathSearchWhite(i-1, j+1, n, matrix, visited)== 1)
                return 1;                                   /* return true */

            // traverse southwest
            /* if path is found in this direction */
            if (PathSearchWhite(i+1, j-1, n, matrix, visited)== 1)
                return 1;                                  /* return true */


        }

        return 0;                                   /* no path was found */
    }
    int valid(int i, int j, int n)
    { if ((i >= 0) && (i < n) &&
            (j >= 0) && (j < n) )
        return 1;
    else
        return 0;
    }
    int maxi(int[][] visited, int n)
    { int i, j, max = -1;
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (visited[i][j] == 1)
                    max = i;

        return max;
    }
    int mini(int[][] visited, int n)
    { int i, j, min;
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (visited[i][j] == 1)
                    return min = i;

        return -1;

    }
    int BlackWon(int n, int[][] matrix, int[] seq)
        /* returns 1 if path was found else reutns 0 */
    { int i, j;
        int[][] visited;          /* keep track of already visited indexes */
        int flag = 0;
        visited = new int[n][n];

        for (i = 0; i < n; i++) /* initialize visited (array) with false */
            for (j = 0; j < n; j++)      /* since we don't know the paths */
                visited[i][j] = 0;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (matrix[i][j] == 1 && visited[i][j] == 0) /* if source cell */
                    if (PathSearchBlack(i, j, n, matrix, visited) == 1 ) {
                        flag = 1; /* path was found */
                    }

            }

        int diff = maxj(visited, n) - minj(visited, n);
        if (diff <= 0)
            seq[0] = 0;
        else
            seq[0] = diff + 1;


        visited = null;
        if (flag == 1)
            return 1;   /* path was found */
        else
            return 0;   /* path was not found */

    }
    int PathSearchBlack(int i, int j, int n, int[][] matrix, int[][] visited)
        /* searches for path from right to left */
    {
        if (valid(i, j, n)!=0 && matrix[i][j] != 0 && visited[i][j] == 0) {
            /* terminal cases */
            visited[i][j] = 1;                            /* cell visited */
            if (matrix[i][j] == 3 && j == n-1)          /* if cell is the */
                return 1;        /* destination(the other end) return true */

            // traverse north
            /* if path is found in this direction */
            if (PathSearchBlack(i-1, j, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse west
            /* if path is found in this direction */
            if (PathSearchBlack(i, j-1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse south
            /* if path is found in this direction */
            if (PathSearchBlack(i+1, j, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse east
            /* if path is found in this direction */
            if (PathSearchBlack(i, j+1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse northeast
            /* if path is found in this direction */
            if (PathSearchBlack(i-1, j+1, n, matrix, visited)==1)
                return 1;                                   /* return true */

            // traverse southwest
            /* if path is found in this direction */
            if (PathSearchBlack(i+1, j-1, n, matrix, visited)==1)
                return 1;                                   /* return true */


        }

        return 0;                                   /* no path was found */
    }
    int maxj(int[][] visited, int n)
        /* returns the greater j co-ordinate where last 1 was found */
        /* in "visited"  array */
    { int i, j, max = -1;
        for (j = 0; j < n; j++)
            for (i = 0; i < n; i++)
                if (visited[i][j] == 1)
                    max = j;

        return max;
    }
    int minj(int[][] visited, int n)
    { int i, j, min;
        for (j = 0; j < n; j++)
            for (i = 0; i < n; i++)
                if (visited[i][j] == 1)
                    return min = j;

        return -1;


    }
    int staticEvaluation( int maxTurn)
        /* evaluates a board-state */
    { int winner = 0;
        int seqW = 0;
        int seqB = 0;
        int[] s = new int[1];
        int k;
        transformWhiteMatrix();
        transformBlackMatrix();

        for (k = 0; k < size; k++) {
            changeW(size, trW, k);
            if (WhiteWon(size, trW, s) != 0) {
                /* if the two sides are connected */
                if (k == 0) {
                    winner = MaximizingPlayer;
                    break; }

            }
            else                      /* if White in not the winner, then */
                seqW = maximum(seqW, s[0]);       /* save the maximum sequence */
        }




        for (k = 0; k < size; k++) {
            changeB(size, trB, k);
            if (BlackWon(size, trB, s) != 0) {
                /* if the two sides are connected */
                if (k == 0) {
                    winner = MinimizingPlayer;
                    break; }

            }
            else                      /* if Black in not the winner, then */
                seqB = maximum(seqB, s[0]);       /* save the maximum sequence */
        }



        int scoreW = seqW* size / 2;
        int scoreB = seqB* size / 2;



        if (winner == MaximizingPlayer)
            return 100;


        if (winner == MinimizingPlayer)
            return -100;

        if (maxTurn != 0)
            return scoreW - scoreB  + CenterVal( maxTurn) + DirVal( maxTurn)/2 +2* BridgeVal( maxTurn);
        else return scoreW - scoreB  - CenterVal(maxTurn) - DirVal( maxTurn)/2 - 2*BridgeVal( maxTurn);


    }
    int CenterVal( int maxTurn)
    { int i, j, counter = 0;
        int val = 0;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++)
                if (hexCells[i][j] != '.')         /* number of filled cells */
                    counter++;

        if (counter < size/2 ) {
            for (i = size/2 - 1; i < size/2; i++)
                for (j = size/2 - 1; j < size/2; j++)
                    if (hexCells[i][j] == 'o' && maxTurn==0)
                        val++;
                    else if (hexCells[i][j] == 'x' && maxTurn!=0)
                        val++;

            return val;
        }
        else
            return 0;


    }

    void changeW(int n, int[][] matr, int k)                    /* White */
        /* updates transformed matrix with new sources for different k, */
        /* where k is a source row */
    { int i, j;
        if (k == 0)      /* when k = 0 we dont need to change the matrix */
            return;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (i == k && matr[i][j] == 3) /* if was (non-source) cell */
                    matr[i][j] = 1;                           /* new source */
                else if (matr[i][j] == 3)
                    continue;
                else {                            /* old sources must be 0 */
                    matr[i][j] = 0; }
            }
    }
    int maximum(int a, int b)
    { return (a >= b) ? a : b;
    }
    void changeB(int n, int[][] matr, int k)                    /* Black */
        /* updates transformed matrix with new sources for different k, */
        /* where k is a source column */
    { int i, j;

        if (k == 0)     /* when k = 0, we dont need to change the matrix */
            return;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                if (j == k && matr[i][j] == 3) /* if was (non-source) cell */
                    matr[i][j] = 1;                           /* new source */
                else if (matr[i][j] == 3)
                    continue;
                else {                            /* old sources must be 0 */
                    matr[i][j] = 0;  }
            }
    }
    int DirVal( int maxTurn)
    { int i, j, val = 0;
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (maxTurn!=0 && hexCells[i][j] == 'x') {
                    if (valid(i+1, j, size)==1 && valid(i-1, j, size)==1)
                        if (hexCells[i+1][j] == 'x' || hexCells[i-1][j] == 'x')
                            val++;
                }
                else if (maxTurn==0 && hexCells[i][j] == 'o') {
                    if (valid(i, j+1, size)!=0 && valid(i, j-1, size)!=0)
                        if (hexCells[i][j+1] == 'o' || hexCells[i][j-1] == 'o')
                            val++;
                }
            }
        }
        return val;
    }

    int BridgeVal( int maxTurn)
    { int i, j, bridge = 0;
        for (i = 0; i < size; i++)
            for (j = 0; j < size; j++) {
                if (maxTurn==0 && hexCells[i][j] == 'o' && surrounded_ratio_b(i, j) >= 1) {
                    if (valid(i-1, j+2, size)==1 && hexCells[i-1][j+2] == 'o') bridge++;
                    if (valid(i+1, j-2, size)==1 && hexCells[i+1][j-2] == 'o') bridge++;
                    if (valid(i-1, j-1, size)==1 && hexCells[i-1][j-1] == 'o') bridge++;
                    if (valid(i+2, j-1, size)==1 && hexCells[i+2][j-1] == 'o') bridge++;
                    if (valid(i+1, j+1, size)==1 && hexCells[i+1][j+1] == 'o') bridge++;
                }
                else if (hexCells[i][j] == 'x' && surrounded_ratio_w(i, j) >= 2) {
                    if (valid(i-1, j+2, size)==1 && hexCells[i-1][j+2] == 'x') bridge++;
                    if (valid(i+1, j-2, size)==1 && hexCells[i+1][j-2] == 'x') bridge++;
                    if (valid(i-1, j-1, size)==1 && hexCells[i-1][j-1] == 'x') bridge++;
                    if (valid(i+2, j-1, size)==1 && hexCells[i+2][j-1] == 'x') bridge++;
                    if (valid(i+1, j+1, size)==1 && hexCells[i+1][j+1] == 'x') bridge++;
                }

            }

        return bridge;


    }
    int surrounded_ratio_b(int i, int j)
    { int r = 0;
        if (valid(i, j+1,size)==1 && hexCells[i][j+1] == 'x') r++;
        if (valid(i, j-1,size)==1 && hexCells[i][j-1] == 'x') r++;
        if (valid(i-1, j+1,size) == 1 && hexCells[i-1][j+1] == 'x') r++;
        return r;
    }
    int surrounded_ratio_w(int i, int j)
    { int r = 0;
        if (valid(i-1, j,size)==1 && hexCells[i-1][j] == 'o') r++;
        if (valid(i+1, j,size)==1 && hexCells[i+1][j] == 'o') r++;
        if (valid(i+1, j-1,size)==1 && hexCells[i+1][j-1] == 'o') r++;
        return r;
    }
    int BoardNotFull()
    { for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
            if (hexCells[i][j] == '.')
                return 1; /* there is at least one cell available */

        return 0; /* board is full */
    }
    int minimum(int a, int b)
    { return (a <= b) ? a : b;
    }
}


