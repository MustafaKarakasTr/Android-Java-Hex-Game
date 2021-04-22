package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button[] buttons;
    public static final String SIZE = "com.example.myapplication.SIZE";
    public static final String AGAINST_CPU = "com.example.myapplication.AGAINST_CPU";

    int size=0;
    int level = 1;
    boolean is_against_cpu;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch mySwitch = (Switch) findViewById(R.id.against_cpu);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                RadioButton[] radios = new RadioButton[4];

                RadioButton easy = (RadioButton)findViewById(R.id.easy);
                RadioButton normal = (RadioButton)findViewById(R.id.normal);
                RadioButton hard= (RadioButton)findViewById(R.id.hard);
                RadioButton expert = (RadioButton)findViewById(R.id.expert);
                radios[0] = easy;
                radios[1] = normal;
                radios[2] = hard;
                radios[3] = expert;



                if(isChecked == true){
                    for(RadioButton r:radios){
                        r.setVisibility(View.VISIBLE);
                        r.setOnClickListener(first_radio_listener);
                    }

                    easy.setChecked(true);
                }
                else{
                    for(RadioButton r:radios){
                        r.setChecked(false);
                        r.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        //linearLayout = findViewById(R.id.ll_main);
        //linearLayout.setWeightSum();
    }
    View.OnClickListener first_radio_listener = new View.OnClickListener(){
        public void onClick(View v) {
            RadioButton[] radios = new RadioButton[4];

            RadioButton easy = (RadioButton)findViewById(R.id.easy);
            RadioButton normal = (RadioButton)findViewById(R.id.normal);
            RadioButton hard= (RadioButton)findViewById(R.id.hard);
            RadioButton expert = (RadioButton)findViewById(R.id.expert);
            radios[0] = easy;
            radios[1] = normal;
            radios[2] = hard;
            radios[3] = expert;
            RadioButton clicked = (RadioButton) v;
            /*for(RadioButton r:radios){
                if(clicked != r){
                    r.setChecked(false);
                }
                else
                    r.setChecked(true);
            }*/
            for(int i=0;i<radios.length;i++){
                if(clicked != radios[i]){
                    radios[i].setChecked(false);
                }
                else {
                    radios[i].setChecked(true);
                    level = i+1;

                }
            }
        }
    };
    public void StartGame(View v)
    {
        /*Intent i = new Intent(this,SettingsActivity.class);
        startActivity(i);
        */

        String size = ((EditText)findViewById(R.id.sizeInput)).getText().toString();
        if(size.isEmpty()){
            Toast toast = Toast.makeText(this,"You should enter a size",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        /*EditText temp = (EditText) ((EditText)findViewById(R.id.sizeInput)).getText(); //

        if(temp == null) return;

        String size = temp.toString();
         */

        int _size = Integer.parseInt(size);
        if(_size<6){
            Toast toast = Toast.makeText(this,"Size must be greater than 5",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        this.size = _size;

        setAgaintsCpu();

        openGame();
        /*
        if(this.size > 5){
            createTable();
        }
        */

    }
    public void openGame(){
        int _size = size;
        boolean _against_cpu= is_against_cpu;
        Intent intent = new Intent(this,gameTable.class);
        intent.putExtra(SIZE,_size);
        intent.putExtra(AGAINST_CPU,_against_cpu);
        if(_against_cpu)
            intent.putExtra("LEVEL",level);
        startActivity(intent);
    }
    private void setAgaintsCpu(){
        Switch s = (Switch) findViewById(R.id.against_cpu);
        if(s.isChecked())
        {
            is_against_cpu = true;
        }
        else is_against_cpu = false;
    }
    private void createTable()
    {
        int x = 50;
        int y = 50;
        linearLayout.setWeightSum(size*size);
        buttons = new Button[size * size];
        for(int i=0;i<size*size;i++)
        {

                buttons[i] = new Button(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                buttons[i].setText("A");
                buttons[i].setId(i);// 0 to (size^2)-1
                buttons[i].setOnClickListener(getOnClick(i));
                linearLayout.addView(buttons[i],layoutParams);

                //buttons[i][j].setBackgroundColor(getResources().getColor(R.color.white));
                buttons[i].setX(50+i*50);
                buttons[i].setY(50+i*50);

        }
    }
    private View.OnClickListener getOnClick(final int i){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(),"clicked"+i,Toast.LENGTH_SHORT).show();
            }
        };
    }
    /*public void disable(View v){

        Log.d("success","Button is disabled");
        Button button = (Button) v;
        button.setText("BASILMIS BUTTON");

        v.setEnabled(false);
        Button newB =(Button) findViewById(R.id.button3);
        newB.setText("new setText");
        //findViewById(R.id.hello).setAlpha((float)0.01);

    }*/

    /*public void takeInput(View v){
        EditText input =findViewById(R.id.source);
        String s = input.getText().toString();
        Log.d("Input ::",s);
        TextView output = findViewById(R.id.output);
        output.setText(s);
        //Toast.makeText(this,s,Toast.LENGTH_LONG).show();
        Toast.makeText(this,"You Win",Toast.LENGTH_LONG).show();

    }*/
    public void LevelSelection(View v){
        Toast.makeText(this,"You Win",Toast.LENGTH_LONG).show();

    }
    public void checked(View v){

    }

}
