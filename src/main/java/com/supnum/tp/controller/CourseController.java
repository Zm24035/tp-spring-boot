package com.supnum.tp.controller;

import com.supnum.tp.model.Course;
import com.supnum.tp.repository.CourseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id,
                               @RequestBody Course newCourse) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(newCourse.getTitle());

        return courseRepository.save(course);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
    @PostMapping("/bulk")
    public List<Course> createManyCourses(@RequestBody List<Course> courses) {
        return courseRepository.saveAll(courses);
    }
}