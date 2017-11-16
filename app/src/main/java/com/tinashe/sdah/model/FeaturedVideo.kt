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

package com.tinashe.sdah.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by tinashe on 2015/09/11.
 */
@IgnoreExtraProperties
class FeaturedVideo {

    var key: String? = null

    var title: String? = null

    var owner: String? = null

    var imgUrl: String? = null

    var link: String? = null

    var isSuggestion: Boolean = false
}
