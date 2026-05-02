package com.supnum.tp.controller;

import com.supnum.tp.model.Student;
import com.supnum.tp.repository.StudentRepository;
import com.supnum.tp.repository.CourseRepository;
import com.supnum.tp.dto.StudentDTO;
import com.supnum.tp.model.Course;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentRepository studentRepository,
                             CourseRepository courseRepository,
                             PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @PostMapping
    public Student create(@RequestBody @Valid Student student) {
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    @GetMapping("/search")
    public List<Student> searchByName(@RequestParam String name) {
        return studentRepository.findByName(name);
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setLogin(updatedStudent.getLogin());

        if (updatedStudent.getPassword() != null && !updatedStudent.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(updatedStudent.getPassword()));
        }

        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }

    @GetMapping("/dto")
    public List<StudentDTO> getAllDTO() {
        return studentRepository.findAll().stream()
                .map(student -> new StudentDTO(
                        student.getName(),
                        student.getEmail()
                ))
                .toList();
    }

    @GetMapping("/page")
    public Page<Student> getPaginated(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public Student assignCourseToStudent(@PathVariable Long studentId,
                                         @PathVariable Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().add(course);

        return studentRepository.save(student);
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    public Student removeCourseFromStudent(@PathVariable Long studentId,
                                           @PathVariable Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().remove(course);

        return studentRepository.save(student);
    }

    @PostMapping("/bulk")
    public List<Student> createManyStudents(@RequestBody List<Student> students) {
        students.forEach(s -> s.setPassword(passwordEncoder.encode(s.getPassword())));
        return studentRepository.saveAll(students);
    }

    @PostMapping("/{studentId}/courses/bulk")
    public Student addCoursesToStudent(@PathVariable Long studentId,
                                       @RequestBody List<Long> courseIds) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Course> courses = courseRepository.findAllById(courseIds);

        student.getCourses().addAll(courses);

        return studentRepository.save(student);
    }
}