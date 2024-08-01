package com.emerson.rs.proact.appmonitor.repository;

import com.emerson.rs.proact.appmonitor.domain.AllSchedulerJobsBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AllSchedulerJobsRepository extends JpaRepository<AllSchedulerJobsBean, String> {
    @Query("select jobs from AllSchedulerJobsBean jobs where jobs.jobName = :job_Name")
    public List<AllSchedulerJobsBean> findJobByName(@Param("job_Name") String jobName);
}
