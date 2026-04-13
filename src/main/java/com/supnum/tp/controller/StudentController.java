package com.supnum.tp.controller;

import com.supnum.tp.model.Student;
import com.supnum.tp.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.supnum.tp.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.supnum.tp.model.Course;
import com.supnum.tp.repository.CourseRepository;


import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @PostMapping
    public Student create(@RequestBody @Valid Student student) {
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
    public Student assignCourseToStudent(
            @PathVariable Long studentId,
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
        return studentRepository.saveAll(students);
    }
    @PostMapping("/{studentId}/courses/bulk")
    public Student addCoursesToStudent(
            @PathVariable Long studentId,
            @RequestBody List<Long> courseIds) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Course> courses = courseRepository.findAllById(courseIds);

        student.getCourses().addAll(courses);

        return studentRepository.save(student);
    }

}

