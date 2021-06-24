package edu.vanderbilt.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
public class RegistrationController {

    public static final String API_VERSION = "1.0";

    public static final String STUDENT_PATH = "/api/" + API_VERSION +"/student";
    public static final String INDIVIDUAL_STUDENT_PATH = STUDENT_PATH +"/{studentId}";

    public static final String COURSE_PATH = "/api/" + API_VERSION +"/course";
    public static final String INDIVIDUAL_COURSE_PATH = COURSE_PATH +"/{courseId}";
    public static final String INDIVIDUAL_COURSE_STUDENT_PATH = INDIVIDUAL_COURSE_PATH +"/{studentId}";


    // You can use this controller or split things up however you want.

}
