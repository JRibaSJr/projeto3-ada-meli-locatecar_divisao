package team3.domain.functional;

import java.util.List;

@FunctionalInterface
public interface ReportGenerator<T> {
    void generateReport(List<T> data, String fileName);
}