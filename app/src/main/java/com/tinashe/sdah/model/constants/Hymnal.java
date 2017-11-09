package com.tinashe.sdah.model.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tinashe on 2017/07/02.
 */

@IntDef({
        Hymnal.ENGLISH,
        Hymnal.SPANISH
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hymnal {

    int ENGLISH = 1;

    int SPANISH = 2;
}
