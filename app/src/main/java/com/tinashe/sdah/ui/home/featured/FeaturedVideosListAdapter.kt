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

package com.tinashe.sdah.ui.home.featured

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.sdah.R
import com.tinashe.sdah.model.FeaturedVideo
import com.tinashe.sdah.ui.custom.extensions.inflateView
import com.tinashe.sdah.ui.custom.extensions.loadFromUrl
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.featured_video.*

/**
 * Created by tinashe on 2017/11/15.
 */
class FeaturedVideosListAdapter : RecyclerView.Adapter<FeaturedVideosListAdapter.VideoHolder>() {

    var videosList: List<FeaturedVideo> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder =
            VideoHolder(inflateView(R.layout.featured_video, parent, false))

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.bindVideo(videosList[position])
    }

    override fun getItemCount(): Int = videosList.size

    class VideoHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindVideo(video: FeaturedVideo) {
            videoTitle?.text = video.title
            videoOwner?.text = video.owner

            videoImg.loadFromUrl(video.imgUrl, R.color.windowBackgroundDark)

            itemView?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(video.link)
                val context = it.context
                intent.resolveActivity(context.packageManager)?.let {
                    context.startActivity(intent)
                }
            }
        }

    }
}