/*
 * Copyright 2014-2021 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You
 * may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class defines implementation methods that are called by the
 * {@link VideoController}.  These methods upload and download videos
 * to a cloud service and manage video metadata.
 *
 * This class is annotated as a Spring {@code @Service}, which enables
 * the autodetection and wiring of dependent implementation classes
 * via classpath scanning.
 */
@Service
public class VideoService {
    /**
     * Error code for "file no found".
     */
    private final int sFILE_NOT_FOUND = 404;

    /**
     * Connect the {@link VideoService} with the {@link
     * VideoFileManager}.
     */
    @Autowired
    private VideoFileManager mVideoFileManager;

    /**
     * A Map that associates video ids with video metadata.
     */
    // TODO -- you fill in here.

    /**
     * Used to provide unique ids for videos.
     */
    // TODO -- you fill in here.

    /**
     * This method handles downloading of binary data. It shows how to
     * use Spring to marshall/unmarshall content types other than
     * JSON.
     *
     * @param id The video id
     * @param response The response returned to the client to indicate
     *                 success or failure
     * @throws IOException Indicates that an I/O error occurred
     */
    public void downloadVideo
        (long id,
         HttpServletResponse response) throws IOException {
        // TODO -- you fill in here.

        // Rewrite this line to find the video if it exists on the
        // server and assign it to the video variable.
        Video video = null;

        if (video != null) {
            // Indicate the content type.
            response
                .setContentType(video.getContentType());

            // Copy the video contents to the output stream.
            mVideoFileManager
                .copyVideoData(video, response.getOutputStream());
        }
        else {
            response.sendError(sFILE_NOT_FOUND, "Video not found");
        }
    }

    /**
     * This method handles uploading of binary data.  It shows how to
     * use Spring to marshall/unmarshall content types other than
     * JSON.
     *
     * @param id The video id
     * @param videoData The video contents
     * @param response The response returned to the client to indicate
     *                 success or failure
     * @return The status of the video.
     * @throws IOException Indicates that an I/O error occurred
     */
    public VideoStatus uploadVideo
        (long id,
         MultipartFile videoData,
         HttpServletResponse response) throws IOException {
        // TODO -- you fill in here.
        
        // Rewrite this line to find the video if it exists on the
        // server and assign it to the video variable.
        Video video = null;

        VideoStatus status = null;

        if (video != null) {
            // Sample to show you how to handle binary data in uploads.
            mVideoFileManager
                .saveVideoData(video,
                               videoData.getInputStream());

            // Transition into the READY state.
            status = new VideoStatus(VideoStatus.VideoState.READY);
        }
        else {
            response.sendError(sFILE_NOT_FOUND, "Video not found");
        }

        return status;
    }

    /**
     * Add metadata about a video, which sets its id if it's not
     * already set.
     *
     * @param video The video metadata
     * @return The updated video metadata
     */
    public Video addVideo(Video video) {
        // TODO -- you fill in here.
    }

    /**
     * Get metadata about the video associated with {@code id}.
     *
     * @param id The video id
     * @return Metadata associated with the video having {@code id}
     */
    public Video getVideo(long id){
        // TODO -- you fill in here.
    }

    /**
     * @return All the known videos
     */
    public Collection<Video> getVideos(){
        // TODO -- you fill in here.
    }
}
