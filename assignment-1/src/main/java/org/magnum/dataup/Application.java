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

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

//This annotation tells Spring that this class contains configuration
//information
//for the application.
@SpringBootApplication
public class Application {
    private static final int MAX_REQUEST_SIZE_IN_MB = 150;

    // The entry point to the application.
    public static void main(String[] args) {
        // This call tells Spring to launch the application and use
        // the configuration specified in LocalApplication to
        // configure the application's components.
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public VideoFileManager videoFileManager(// An example of using a configuration property in the creation of a bean.
                                             // This property is defined in src/main/resources/application.properties
                                             @Value("video.file.manager.directory") String dir) throws Exception{
        return new VideoFileManager(dir);
    }

    // This configuration element adds the ability to accept multipart
    // requests to the web container.
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        // Setup the application container to be accept multipart requests
        final MultipartConfigFactory factory = new MultipartConfigFactory();

        // Place upper bounds on the size of the requests to ensure that
        // clients don't abuse the web container by sending huge requests
        factory.setMaxFileSize(DataSize.ofMegabytes(MAX_REQUEST_SIZE_IN_MB));
        factory.setMaxRequestSize(DataSize.ofMegabytes(MAX_REQUEST_SIZE_IN_MB));

        // Return the configuration to setup multipart in the container
        return factory.createMultipartConfig();
    }
}
