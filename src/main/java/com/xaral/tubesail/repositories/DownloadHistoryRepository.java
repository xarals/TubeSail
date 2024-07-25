package com.xaral.tubesail.repositories;

import com.xaral.tubesail.domain.DownloadHistory;
import com.xaral.tubesail.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadHistoryRepository extends JpaRepository<DownloadHistory, Long> {
    List<DownloadHistory> findByUser(User user);
}
