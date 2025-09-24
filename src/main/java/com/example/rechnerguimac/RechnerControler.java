package com.example.rechnerguimac;

//connects frontend and backend

public class RechnerControler {
    public String getCalculation(String experssion) {
        RechnerModel rm = new RechnerModel();
        resultType result = rm.calculate(experssion);
        if (result.isError()) {
            return "Error";
        } else {
            //if results ends with .0 remove the .0
            if(result.getResult().endsWith(".0")){
                result.setResult(result.getResult().substring(0, result.getResult().length()-2));
            }
                return result.getResult();

        }

    }
}
