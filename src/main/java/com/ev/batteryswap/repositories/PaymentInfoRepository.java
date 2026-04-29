package com.ev.batteryswap.repositories;

import com.ev.batteryswap.pojo.PaymentInfo;
import com.ev.batteryswap.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface PaymentInfoRepository  extends JpaRepository<PaymentInfo, Integer>  {

    List<PaymentInfo> findByUser(User user);

    Page<PaymentInfo> findAll(Specification<PaymentInfo> paymentInfoSpecification, Pageable pageable);


    @Transactional
    @Modifying
    @Query("update PaymentInfo p set p.paymentMethod = :payMethod where p.note = :note")
    void updatePayMethodByNote(@Param("note") String note, @Param("payMethod") String payMethod);

}
