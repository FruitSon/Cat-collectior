package com.example.carlos.assignment_one;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 17/10/18.
 */

public class CatList {
    List<CatInfo> cats;

    public CatInfo get(int i){
        return cats.get(i);
    }

    // public constructor is necessary for collections
    public CatList() {
        cats = new ArrayList<CatInfo>();
    }
}
