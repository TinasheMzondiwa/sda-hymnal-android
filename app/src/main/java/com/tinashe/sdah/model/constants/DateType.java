/*
 * Copyright (c) 2017. Tinashe Mzondiwa (www.tinashemzondiwa.co.za)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tinashe.sdah.model.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tinashe on 2015/06/03.
 */
@StringDef({
        DateType.DATE,
        DateType.DATE_SHORT,
        DateType.TIME
})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateType {

    String DATE = "EEEE MMMM d";
    String DATE_SHORT = "MMM d, yyyy";
    String TIME = "hh:mm a";
}