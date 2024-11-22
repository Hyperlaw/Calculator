package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private TextView tvResult;
    private Button specialButton;
    private StringBuilder inputExpression = new StringBuilder();
    private boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        tvResult = findViewById(R.id.tvResult);



        specialButton = findViewById(R.id.buttonToNewActivity);

        specialButton.setVisibility(View.GONE);

        specialButton.setOnClickListener(v -> {
            String resultText = tvResult.getText().toString();
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("key1", resultText);
            startActivity(intent);
        });
    }



    public void onNumberClick(View view) {
        hideSpecialButton();
        MaterialButton button = (MaterialButton) view;
        String value = button.getText().toString();

        if (isResultShown) {
            inputExpression.setLength(0);
            isResultShown = false;
        }

        if (value.equals("AC")) {
            inputExpression.setLength(0);
            tvResult.setText("0");
        } else if (value.equals("+/-")) {
            if (inputExpression.length() > 0 && !inputExpression.toString().startsWith("-")) {
                inputExpression.insert(0, "-");
            } else if (inputExpression.length() > 0) {
                inputExpression.deleteCharAt(0);
            }
        } else if (value.equals(".")) {
            if (inputExpression.length() > 0) {
                String currentNumber = getCurrentNumber(inputExpression.toString());
                if (currentNumber.contains(".")) {
                    return;
                }
            }
            inputExpression.append(value);
        } else {
            if (tvResult.getText().toString().equals("0")) {
                inputExpression.setLength(0);
            }
            inputExpression.append(value);
        }

        tvResult.setText(inputExpression.length() > 0 ? inputExpression.toString() : "0");
    }

    public void onOperationClick(View view) {
        hideSpecialButton();
        MaterialButton button = (MaterialButton) view;
        String value = button.getText().toString();

        if (isResultShown && !value.equals("=")) {
            isResultShown = false;
            inputExpression.setLength(0);
        }

        if (value.equals("X")) {
            value = "*";
        }

        if (inputExpression.length() > 0) {
            char lastChar = inputExpression.charAt(inputExpression.length() - 1);

            if (isOperator(lastChar)) {
                inputExpression.deleteCharAt(inputExpression.length() - 1);
            }
        }

        if (value.equals("=")) {
            calculateResult();
            specialButton.setVisibility(View.VISIBLE);
            isResultShown = true;
        } else if (value.equals("%")) {
            calculateLastNumberPercentage();
        } else {
            inputExpression.append(value);
            tvResult.setText(inputExpression.toString());
        }
    }

    private void hideSpecialButton() {
        specialButton.setVisibility(View.GONE);
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private String getCurrentNumber(String expression) {
        int lastOperatorIndex = Math.max(
                Math.max(expression.lastIndexOf('+'), expression.lastIndexOf('-')),
                Math.max(expression.lastIndexOf('*'), expression.lastIndexOf('/'))
        );

        if (lastOperatorIndex == -1) {
            return expression;
        }

        return expression.substring(lastOperatorIndex + 1);
    }

    private void calculateResult() {
        try {
            if (inputExpression.toString().matches(".*/0(\\.0*)?$")) {
                Toast.makeText(this, "На ноль делить нельзя", Toast.LENGTH_SHORT).show();
                tvResult.setText("0");
                inputExpression.setLength(0);
                return;
            }

            Expression expression = new ExpressionBuilder(inputExpression.toString()).build();
            double result = expression.evaluate();

            String output = (result == (long) result) ? String.valueOf((long) result) : String.valueOf(result);

            tvResult.setText(output);
            inputExpression.setLength(0);
            inputExpression.append(output);
            isResultShown = true;
        } catch (Exception e) {
            tvResult.setText("0");
            Toast.makeText(this, "Ошибка в вычислении", Toast.LENGTH_SHORT).show();
            inputExpression.setLength(0);
        }
    }

    private void calculateLastNumberPercentage() {
        try {
            String currentExpression = inputExpression.toString();

            int lastOperatorIndex = Math.max(
                    Math.max(currentExpression.lastIndexOf('+'), currentExpression.lastIndexOf('-')),
                    Math.max(currentExpression.lastIndexOf('*'), currentExpression.lastIndexOf('/'))
            );

            if (lastOperatorIndex == -1) {
                double number = Double.parseDouble(currentExpression);
                double percentage = number / 100;

                String output = (percentage == (long) percentage)
                        ? String.valueOf((long) percentage)
                        : String.valueOf(percentage);

                inputExpression.setLength(0);
                inputExpression.append(output);
                tvResult.setText(output);
                return;
            }

            String lastNumber = currentExpression.substring(lastOperatorIndex + 1);
            double number = Double.parseDouble(lastNumber);

            String baseExpression = currentExpression.substring(0, lastOperatorIndex);
            Expression baseEval = new ExpressionBuilder(baseExpression).build();
            double baseValue = baseEval.evaluate();

            double percentage = baseValue * (number / 100);

            String formattedPercentage = (percentage == (long) percentage)
                    ? String.valueOf((long) percentage)
                    : String.valueOf(percentage);

            String updatedExpression = currentExpression.substring(0, lastOperatorIndex + 1) + formattedPercentage;
            inputExpression.setLength(0);
            inputExpression.append(updatedExpression);

            tvResult.setText(inputExpression.toString());
        } catch (Exception e) {
            tvResult.setText("0");
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            inputExpression.setLength(0);
        }
    }

    public void onBackspaceClick(View view) {
        hideSpecialButton();
        if (inputExpression.length() > 0) {
            inputExpression.deleteCharAt(inputExpression.length() - 1);
            tvResult.setText(inputExpression.length() > 0 ? inputExpression.toString() : "0");
        } else {
            tvResult.setText("0");
        }
    }

}
