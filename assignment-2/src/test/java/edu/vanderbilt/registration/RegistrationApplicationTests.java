package edu.vanderbilt.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	public <T> Function<byte[], Collection<T>> collection(Class<T> t){
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

	public <T> T apiCall(RequestBuilder builder, Function<byte[],T> unmarshaller) {
		byte[] rawResult = new byte[0];
		try {
			rawResult = mockMvc.perform(builder)
					.andExpect(status().isOk())
					.andReturn()
					.getResponse()
					.getContentAsByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		T result = unmarshaller.apply(rawResult);

		return result;
	}

	public byte[] writeBytes(Object o){
		try {
			return objectMapper.writeValueAsBytes(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public Course addCourse(Course video) {
		return apiCall(
				MockMvcRequestBuilders
						.post(RegistrationController.COURSE_PATH)
						.content(writeBytes(video))
						.contentType(MediaType.APPLICATION_JSON),
				object(Course.class));
	}

	public Collection<Course> getCourses() {
		return apiCall(
				MockMvcRequestBuilders
						.get(RegistrationController.COURSE_PATH),
				collection(Course.class)
		);
	}

	public Student addStudent(Student student) {
		return apiCall(
				MockMvcRequestBuilders
						.post(RegistrationController.STUDENT_PATH)
						.content(writeBytes(student))
						.contentType(MediaType.APPLICATION_JSON),
				object(Student.class));
	}

	public Collection<Student> getStudents() {
		return apiCall(
				MockMvcRequestBuilders
						.get(RegistrationController.STUDENT_PATH),
				collection(Student.class)
		);
	}

	public Course registerStudent(Course course, Student student) {
		return apiCall(
				MockMvcRequestBuilders
						.post(RegistrationController.COURSE_PATH
								+ "/" + course.getId()
								+ "?studentId=" + student.getId())
						.contentType(MediaType.APPLICATION_JSON),
				object(Course.class));
	}

	public void unregisterStudent(Course course, Student student) {
		try {
			mockMvc.perform(
							MockMvcRequestBuilders
								.delete(RegistrationController.COURSE_PATH
										+ "/" + course.getId()
										+ "/" + student.getId())
								.contentType(MediaType.APPLICATION_JSON))
							.andExpect(status().isOk())
							.andReturn();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Collection<Student> getStudentsInCourse(Course course) {
		return apiCall(
				MockMvcRequestBuilders
						.get(RegistrationController.COURSE_PATH + "/" + course.getId()),
				collection(Student.class)
		);
	}


	@Test
	public void testCourseSimpleCrud() throws Exception {
		Course c = new Course();
		c.setTitle(UUID.randomUUID().toString());
		c.setDescription(UUID.randomUUID().toString());

		Course received = addCourse(c);

		assertEquals(c.getTitle(), received.getTitle());
		assertEquals(c.getDescription(), received.getDescription());

		Course fetched = getCourses().stream().filter(c2 -> c2.getId().equals(received.getId())).findFirst().get();

		assertEquals(c.getTitle(), fetched.getTitle());
		assertEquals(c.getDescription(), fetched.getDescription());

		fetched.setTitle(UUID.randomUUID().toString());
		Course received2 = addCourse(fetched);
		assertEquals(fetched.getTitle(), received2.getTitle());
		assertEquals(fetched.getDescription(), received2.getDescription());

		fetched = getCourses().stream().filter(c2 -> c2.getId().equals(received2.getId())).findFirst().get();
		assertEquals(received2.getTitle(), fetched.getTitle());
		assertEquals(received2.getDescription(), fetched.getDescription());
	}

	@Test
	public void testStudentSimpleCrud() throws Exception {
		Student c = new Student();
		c.setFirstName(UUID.randomUUID().toString());
		c.setLastName(UUID.randomUUID().toString());
		c.setProfilePhotoUrl(UUID.randomUUID().toString());

		Student received = addStudent(c);

		assertEquals(c.getFirstName(), received.getFirstName());
		assertEquals(c.getLastName(), received.getLastName());
		assertEquals(c.getProfilePhotoUrl(), received.getProfilePhotoUrl());
		
		Student fetched = getStudents().stream().filter(c2 -> c2.getId().equals(received.getId())).findFirst().get();

		assertEquals(c.getFirstName(), fetched.getFirstName());
		assertEquals(c.getLastName(), fetched.getLastName());
		assertEquals(c.getProfilePhotoUrl(), fetched.getProfilePhotoUrl());

		fetched.setFirstName(UUID.randomUUID().toString());
		Student received2 = addStudent(fetched);
		assertEquals(fetched.getFirstName(), received2.getFirstName());
		assertEquals(fetched.getLastName(), received2.getLastName());
		assertEquals(fetched.getProfilePhotoUrl(), received2.getProfilePhotoUrl());

		fetched = getStudents().stream().filter(c2 -> c2.getId().equals(received2.getId())).findFirst().get();
		assertEquals(received2.getFirstName(), fetched.getFirstName());
		assertEquals(received2.getLastName(), fetched.getLastName());
		assertEquals(received2.getProfilePhotoUrl(), fetched.getProfilePhotoUrl());
	}

	public <T> List<T> randomSample(List<T> all){
		return all.stream().filter(s -> Math.random() > 0.5).collect(Collectors.toList());
	}

	@Test
	public void testRegistration() throws Exception {

		List<Course> courses = IntStream.range(0, 10).mapToObj(i -> {
			Course course = new Course();
			course.setTitle(UUID.randomUUID().toString());
			course.setDescription(UUID.randomUUID().toString());
			return course;
		}).collect(Collectors.toList());

		courses.forEach(c -> {
			c.setId(addCourse(c).getId());
		});

		List<Student> students = IntStream.range(0, 50).mapToObj(i -> {
			Student student = new Student();
			student.setFirstName(UUID.randomUUID().toString());
			student.setLastName(UUID.randomUUID().toString());
			return student;
		}).collect(Collectors.toList());

		Map<Course,List<Student>> enrolledStudentsByCourse = new HashMap<>();
		courses.forEach(c -> {
			enrolledStudentsByCourse.put(c, randomSample(students));
		});

		students.forEach(s -> {
			s.setId(addStudent(s).getId());
		});

		courses.forEach(c -> {
			List<Student> studentsInCourse = enrolledStudentsByCourse.get(c);
			studentsInCourse.forEach(s -> {
				try {
					registerStudent(c, s);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		});

		courses.forEach(c -> {
			List<Student> studentsInCourse = enrolledStudentsByCourse.get(c);
			Collection<Student> inCourse = getStudentsInCourse(c);

			Map<Long,Student> byId = studentsInCourse.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));

			inCourse.forEach(s -> {
				assertTrue(byId.containsKey(s.getId()));
			});

		});

		courses.forEach(c -> {
			List<Student> studentsInCourse = enrolledStudentsByCourse.get(c);
			studentsInCourse.forEach(s -> {
				try {
					unregisterStudent(c, s);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		});

		courses.forEach(c -> {
			Collection<Student> inCourse = getStudentsInCourse(c);
			assertEquals(0, inCourse.size());
		});
	}

}
