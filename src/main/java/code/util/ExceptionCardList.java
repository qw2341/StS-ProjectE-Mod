package code.util;

import java.util.HashMap;

public class ExceptionCardList {

    public static HashMap<String, Integer> exceptionList = new HashMap<>();

    static {
        HashMap<String, Integer> cardList = ExceptionSaver.readList("AllCardsList.json");
        if (cardList != null) {
            exceptionList = cardList;
        }
    }


}
