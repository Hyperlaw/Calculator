package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private StringBuilder inputExpression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
    }

    public void onNumberClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String value = button.getText().toString();

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


    public void onOperationClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String value = button.getText().toString();

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
        } else if (value.equals("%")) {
            calculateLastNumberPercentage();
        } else {
            inputExpression.append(value);
            tvResult.setText(inputExpression.toString());
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
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
        } catch (Exception e) {
            tvResult.setText("0");
            Toast.makeText(this, "Ошибка в вычислении", Toast.LENGTH_SHORT).show();
            inputExpression.setLength(0);
        }
    }

    public void onBackspaceClick(View view) {
        if (inputExpression.length() > 0) {
            inputExpression.deleteCharAt(inputExpression.length() - 1);
            tvResult.setText(inputExpression.length() > 0 ? inputExpression.toString() : "0");
        }
    }


}
