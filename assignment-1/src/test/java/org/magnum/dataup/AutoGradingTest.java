/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

/**
 *                       DO NOT MODIFY THIS CLASS
 *                       
                    ___                    ___           ___                            
     _____         /\  \                  /\  \         /\  \                           
    /::\  \       /::\  \                 \:\  \       /::\  \         ___              
   /:/\:\  \     /:/\:\  \                 \:\  \     /:/\:\  \       /\__\             
  /:/  \:\__\   /:/  \:\  \            _____\:\  \   /:/  \:\  \     /:/  /             
 /:/__/ \:|__| /:/__/ \:\__\          /::::::::\__\ /:/__/ \:\__\   /:/__/              
 \:\  \ /:/  / \:\  \ /:/  /          \:\~~\~~\/__/ \:\  \ /:/  /  /::\  \              
  \:\  /:/  /   \:\  /:/  /            \:\  \        \:\  /:/  /  /:/\:\  \             
   \:\/:/  /     \:\/:/  /              \:\  \        \:\/:/  /   \/__\:\  \            
    \::/  /       \::/  /                \:\__\        \::/  /         \:\__\           
     \/__/         \/__/                  \/__/         \/__/           \/__/           
      ___           ___                                     ___                         
     /\  \         /\  \         _____                     /\__\                        
    |::\  \       /::\  \       /::\  \       ___         /:/ _/_         ___           
    |:|:\  \     /:/\:\  \     /:/\:\  \     /\__\       /:/ /\__\       /|  |          
  __|:|\:\  \   /:/  \:\  \   /:/  \:\__\   /:/__/      /:/ /:/  /      |:|  |          
 /::::|_\:\__\ /:/__/ \:\__\ /:/__/ \:|__| /::\  \     /:/_/:/  /       |:|  |          
 \:\~~\  \/__/ \:\  \ /:/  / \:\  \ /:/  / \/\:\  \__  \:\/:/  /      __|:|__|          
  \:\  \        \:\  /:/  /   \:\  /:/  /   ~~\:\/\__\  \::/__/      /::::\  \          
   \:\  \        \:\/:/  /     \:\/:/  /       \::/  /   \:\  \      ~~~~\:\  \         
    \:\__\        \::/  /       \::/  /        /:/  /     \:\__\          \:\__\        
     \/__/         \/__/         \/__/         \/__/       \/__/           \/__/        
 */


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class AutoGradingTest {

	private File testVideoData = new File(
			"src/test/resources/test.mp4");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


	private Video video() {
		return Video.create().withContentType("video/mp4")
				.withDuration((int)Math.rint(Math.random() * Integer.MAX_VALUE))
				.withSubject(UUID.randomUUID().toString())
				.withTitle(UUID.randomUUID().toString()).build();
	}


	public <T> Function<byte[],Collection<T>> collection(Class<T> t){
		return (byte[] data) -> {
			try {
				return objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(List.class, t));
			} catch (Exception e){throw new RuntimeException(e);}
		};
	}

	public <T> Function<byte[],T> object(Class<T> t){
		return (byte[] data) -> {
			try {
				return objectMapper.readValue(data, t);
			} catch (Exception e){throw new RuntimeException(e);}
		};
	}

	public <T> T apiCall(RequestBuilder builder, Function<byte[],T> unmarshaller) throws Exception {
		byte[] rawResult = mockMvc.perform(builder)
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsByteArray();

		T result = unmarshaller.apply(rawResult);

		return result;
	}

	public Video addVideo(Video video) throws Exception {
		return apiCall(
				MockMvcRequestBuilders
						.post("/video")
						.content(objectMapper.writeValueAsBytes(video))
						.contentType(MediaType.APPLICATION_JSON),
				 object(Video.class));
	}

	public Video getVideo(long id) throws Exception {
		return apiCall(
				MockMvcRequestBuilders
						.get("/video/" + id),
				object(Video.class)
		);
	}

	public Collection<Video> getVideoList() throws Exception {
		return apiCall(
				MockMvcRequestBuilders
						.get("/video"),
				   collection(Video.class)
		);
	}

	public VideoStatus setVideoData(long id, File data) throws Exception {
		return apiCall(
				MockMvcRequestBuilders
						.multipart("/video/" + id + "/data")
						.file("data", Files.toByteArray(data))
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE),
				object(VideoStatus.class));
	}

	public InputStream getData(long id) throws Exception {
		return new ByteArrayInputStream(
				mockMvc.perform(
					MockMvcRequestBuilders
						.get("/video/" + id + "/data"))
					.andExpect(status().isOk())
					.andReturn()
					.getResponse()
					.getContentAsByteArray());
	}

	@Test
	public void testAddVideoMetadata() throws Exception {
		Video video = video();
		Video received = addVideo(video);
		assertEquals(video.getTitle(), received.getTitle());
		assertEquals(video.getDuration(), received.getDuration());
		assertEquals(video.getContentType(), received.getContentType());
		assertEquals(video.getLocation(), received.getLocation());
		assertEquals(video.getSubject(), received.getSubject());
		assertTrue(received.getId() > 0);

	}

	@Test
	public void testAddGetVideo() throws Exception {
		Video video = video();
		video = addVideo(video);
		Collection<Video> stored = getVideoList();
		assertTrue(stored.contains(video));

		Video received = getVideo(video.getId());
		assertEquals(video, received);
		assertEquals(video.getTitle(), received.getTitle());
		assertEquals(video.getDuration(), received.getDuration());
		assertEquals(video.getContentType(), received.getContentType());
		assertEquals(video.getLocation(), received.getLocation());
		assertEquals(video.getSubject(), received.getSubject());
	}

	@Test
	public void testAddVideoData() throws Exception {
		Video video = video();
		Video received = addVideo(video);
		VideoStatus status = setVideoData(received.getId(),
				testVideoData);
		assertEquals(VideoState.READY, status.getState());

		InputStream videoData = getData(received.getId());

		byte[] originalFile = IOUtils.toByteArray(new FileInputStream(testVideoData));
		byte[] retrievedFile = IOUtils.toByteArray(videoData);
		assertTrue(Arrays.equals(originalFile, retrievedFile));
	}

	@Test
	public void testGetNonExistantVideosData() throws Exception {
		
		long nonExistantId = getInvalidVideoId();

		try {
			InputStream in = getData(nonExistantId);
			fail("The video should not have been found and a 404 should have been returned.");
		}catch (AssertionError e){
			// We expect it to fail, so this is OK
		}

	}

	@Test
	public void testAddNonExistantVideosData() throws Exception {
		long nonExistantId = getInvalidVideoId();

		try{
			setVideoData(nonExistantId, testVideoData);
			fail("The client should receive a 404 error code and throw an exception if an invalid"
					+ " video ID is provided in setVideoData()");
		}catch(AssertionError e){
			// We expect it to fail, so this is OK
		}
	}

	private long getInvalidVideoId() throws Exception {
		Set<Long> ids = new HashSet<Long>();
		Collection<Video> stored = getVideoList();
		for(Video v : stored){
			ids.add(v.getId());
		}
		
		long nonExistantId = (long)Math.rint(Math.random() * Integer.MAX_VALUE);
		while(ids.contains(nonExistantId)){
			nonExistantId++;
		}

		return nonExistantId;
	}

}
