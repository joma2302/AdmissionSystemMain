package org.example.infrastructure.web.dto;

import java.util.List;

/**
 * DTO результату зарахування з переліком зарахованих та відхилених.
 */
public record AdmissionResultResponse(String facultyName, List<ApplicationResponse> admitted,
                                       List<ApplicationResponse> rejected) {
}
