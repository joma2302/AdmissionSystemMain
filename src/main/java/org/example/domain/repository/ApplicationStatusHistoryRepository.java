package org.example.domain.repository;

import org.example.domain.model.ApplicationStatusHistory;
import java.util.List;

public interface ApplicationStatusHistoryRepository {
    void save(ApplicationStatusHistory history);
    List<ApplicationStatusHistory> findByApplication(String applicantId, String facultyName);
}
