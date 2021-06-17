/*
 * 
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This Spring controller demonstrates how Spring MVC can be used to
 * handle HTTP GET requests via Java object-oriented programming.
 * These requests are mapped to endpoint methods that synchronously
 * upload and download videos to a cloud service and manage video
 * metadata.
 *
 * In Spring's approach to building RESTful web services, HTTP
 * requests are handled by a controller (identified by the
 * {@code @RestController} annotation) that defines the endpoints (aka
 * routes) for each supported operation, i.e., {@code @GetMapping},
 * {@code @PostMapping}, {@code @PutMapping}, and
 * {@code @DeleteMapping}, which correspond to the HTTP GET, POST,
 * PUT, and DELETE calls, respectively.
 *
 * Spring uses the {@code @GetMapping} and {@code @PostMapping}
 * annotations to map HTTP GET and POST requests onto methods in the
 * {@link VideoController}, respectively.  These GET and POST requests
 * can be invoked from any HTTP web client (e.g., a web browser or
 * Android app) or command-line utility (e.g., Curl or Postman).
 */
@RestController
public class VideoController {
    /**
     * The name of the multipart form parameter that data is sent in.
     */
    public static final String DATA_PARAMETER = "data";

    /**
     * The path variable for the ID of a video.
     */
    public static final String ID_PARAMETER = "id";

    /**
     * The path where we expect the VideoSvc to live.
     */
    public static final String VIDEO_SVC_PATH = "/video";

    /**
     * The path where we expect the video metadata lives.
     */
    public static final String INDIVIDUAL_VIDEO_PATH = 
        VIDEO_SVC_PATH + "/{"+ID_PARAMETER+"}";

    /**
     * The path where we expect the VideoSvc to live.
     */
    public static final String VIDEO_DATA_PATH =
        INDIVIDUAL_VIDEO_PATH + "/data";

    /**
     * Connect the {@link VideoController} with the {@link
     * VideoService}.
     */
    @Autowired
    private VideoService mService;

    /**
     * This endpoint method handles downloading of binary data. It
     * shows how to use Spring to marshall/unmarshall content types
     * other than JSON.
     *
     * @param id The video id
     * @param response The response returned to the client to indicate
     *                 success or failure
     * @throws IOException Indicates that an I/O error occurred
     */
    @GetMapping(VIDEO_DATA_PATH)
    public void downloadVideo
        (@PathVariable(ID_PARAMETER) long id,
         HttpServletResponse response) throws IOException {
        mService
            // Forward to the VideoService.
            .downloadVideo(id, response);
    }

    /**
     * This endpoint method handles uploading of binary data.  It
     * shows how to use Spring to marshall/unmarshall content types
     * other than JSON.
     *
     * @param id The video id
     * @param videoData The video contents
     * @param response The response returned to the client to indicate
     *                 success or failure
     * @return The status of the video.
     * @throws IOException Indicates that an I/O error occurred
     */
    @PostMapping(VIDEO_DATA_PATH)
    public VideoStatus uploadVideo
        (@PathVariable(ID_PARAMETER) long id,
         @RequestParam(DATA_PARAMETER) MultipartFile videoData,
         HttpServletResponse response) throws IOException {
        return mService
            // Forward to the VideoService.
            .uploadVideo(id, videoData, response);
    }

    /**
     * Add metadata about a video, which sets its id if it's not
     * already set.
     *
     * @param video The video metadata
     * @return The updated video metadata
     */
    @PostMapping(VIDEO_SVC_PATH)
    public Video addVideo(@RequestBody Video video) {
        return mService
            // Forward to the VideoService.
            .addVideo(video);
    }

    /**
     * Get metadata about the video associated with {@code id}.
     *
     * @param id The video id
     * @return Metadata associated with the video having {@code id}
     */
    @GetMapping(INDIVIDUAL_VIDEO_PATH)
    public Video getVideo(@PathVariable(ID_PARAMETER) long id){
        return mService
            // Forward to the VideoService.
            .getVideo(id);
    }

    /**
     * @return All the known videos
     */
    @GetMapping(VIDEO_SVC_PATH)
    public Collection<Video> getVideos(){
        return mService
            // Forward to the VideoService.
            .getVideos();
    }
}
