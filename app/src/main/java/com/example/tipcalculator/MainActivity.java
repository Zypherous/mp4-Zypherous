package com.example.tipcalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, OnSeekBarChangeListener{
    //declare your variables for the widgets
    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView progressView;
    private TextView tipView;
    private TextView textTotalPerPerson;
    private RadioGroup radioGroup;
    //declare the variables for the calculations
    private double billAmount = 0.00;
    private double percent = .15;
    private double total = 0.00;
    private double totalPerPerson = 0.00;
    private double tip = 0.00;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private int numOfPeopleSplitting;
    private String billDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        //add Listeners to Widgets
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(15);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                percent = progress / 100.0; //calculate percent based on seeker value
                progressView.setText(percentFormat.format(percent));
                seekBar.setProgress(progress);
                Log.d("MainActivty", "percent is: " + percent);
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        radioGroup = findViewById(R.id.rb_group_rounding_options);
        radioGroup.check(R.id.rb_no_round);
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        editTextBillAmount.setText(Double.toString(billAmount));
        progressView = (TextView)findViewById(R.id.progressView);
        progressView.setText(percentFormat.format(percent));
        tipView = (TextView)findViewById(R.id.tipView);
        tipView.setText(currencyFormat.format(0));
        textTotalPerPerson = findViewById(R.id.per_person_total);
        textTotalPerPerson.setText(currencyFormat.format(0));
        editTextBillAmount.addTextChangedListener((TextWatcher) this);
        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numPeople, R.layout.my_spinner);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        numOfPeopleSplitting = Integer.parseInt(spinner.getSelectedItem().toString());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                numOfPeopleSplitting = Integer.parseInt(spinner.getSelectedItem().toString());
                calculateTotalPerPerson(spinnerCalculate());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        billDetails = String.format("Bill Amount: %.2f, Tip Amount: %.2f, Total: %.2f, Total Per Person: %.2f",
        billAmount, tip, total, totalPerPerson);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    /* Note:   int i, int i1, and int i2
        represent start, before, count respectively
        The charSequence is converted to a String and parsed to a double for you  */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);
        //surround risky calculations with try catch (what if billAmount is 0 ?  --> Then that means the total is 0!
        //charSequence is converted to a String and parsed to a double for you
        try{
            billAmount = Double.parseDouble(charSequence.toString());
        }catch(Exception e){
            billAmount = 0;
        }
        Log.d("MainActivity", "Bill Amount = "+billAmount);
        //setText on the textView
        textViewBillAmount.setText(currencyFormat.format(billAmount));


        //perform tip and total calculation and update UI by calling calculate
        calculate();//uncomment this line
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.0; //calculate percent based on seeker value
        progressView.setText(percentFormat.format(percent));
        seekBar.setProgress(progress);
        Log.d("MainActivty", "percent is: " + percent);
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
        @Override
    public void afterTextChanged(Editable editable) {
//        findViewById()
    }



    // calculate and display tip and total amounts
    private void calculate() {
        Log.d("MainActivity", "inside calculate method");
        //uncomment below
       // format percent and display in percentTextView
       progressView.setText(percentFormat.format(percent));
       // calculate the tip and total
        tip = billAmount * percent;
        Log.d("MainActivity", "tip = " + tip );
      //use the tip example to do the same for the Total
        tipView.setText(currencyFormat.format(tip));
        total = billAmount + tip;
        textViewBillAmount.setText(currencyFormat.format(total));
        calculateTotalPerPerson(total);
    }
    public void calculateTotalPerPerson(double value){
        totalPerPerson = value/numOfPeopleSplitting;
        textTotalPerPerson.setText(currencyFormat.format(totalPerPerson));

    }

    public void onRadioButtonClicked(View view) {
        switch(view.getId()){
            case R.id.rb_no_round:
                calculateTotalPerPerson(total);
                break;
            case R.id.rb_tip_round:
               calculateTotalPerPerson(billAmount+ Math.ceil(tip));
                break;
            case R.id.rb_total_round:
                calculateTotalPerPerson(Math.ceil(total));
                break;
            default:
                break;
        }
    }
    private double spinnerCalculate(){
       int selectedRb = radioGroup.getCheckedRadioButtonId();
       double totalToCalculate = 0.00;
       switch (selectedRb){
           case R.id.rb_tip_round: return billAmount+ Math.ceil(tip);
           case R.id.rb_total_round: return Math.ceil(total);
           case R.id.rb_no_round: default: return total;
       }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void shareMessage(){
        billDetails = String.format("Bill Amount: %.2f, Tip Amount: %.2f, Total: %.2f, Total Per Person: %.2f",
                billAmount, tip, total, totalPerPerson);
        Intent intent = ShareCompat.IntentBuilder.from(this).setType("text/plain")
                .setText(billDetails)
                .createChooserIntent();
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_share:
                shareMessage();
                break;
            case R.id.action_info:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("How To Use:");
                alertBuilder.setMessage("Select number of people splitting bill. Select option to" +
                        "round the bill amount. No rounding, bill is exact. Round tip, tip amount is rounded up" +
                        "round total, total is rounded up.");
                alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                alertBuilder.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
