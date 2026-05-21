package org.example.application.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.*;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.FacultyRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервіс зарахування абітурієнтів.
 * Реалізує бізнес-логіку ранжування та визначення зарахованих/відхилених.
 * Використовує Strategy pattern через AdmissionSheet для алгоритму зарахування.
 */
@Service
public interface AdmissionService {
    AdmissionSheet processAdmission(String facultyName);
    List<Application> getRankedApplications(String facultyName);
    List<Application> getAllApplications();
    void updateApplicationStatus(String applicantId, String facultyName, ApplicationStatus status);
    List<ApplicationStatusHistory> getStatusHistory(String applicantId, String facultyName);
    AdminDashboardStats getDashboardStats();
    List<FacultyDemand> getFacultyDemand();
    int getTotalScore(String applicantId);

    record AdminDashboardStats(int applicants, int applications, int pending, int admitted,
                                      int rejected, int seats, int freeSeats) {
    }

    record FacultyDemand(String facultyName, int seats, int applications, int pending,
                                int admitted, int rejected, int freeSeats, double competition) {
    }
}
