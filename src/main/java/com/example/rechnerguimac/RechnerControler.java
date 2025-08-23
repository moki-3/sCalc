package com.example.rechnerguimac;

//connects frontend and backend

public class RechnerControler {
    public String getCalculation(String experssion) {
        RechnerModel rm = new RechnerModel();
        resultType result = rm.calculate(experssion);
        if (result.isError()) {
            return "Error";
        } else {
            return result.getResult();
        }

    }
}
