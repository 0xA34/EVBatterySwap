package com.ev.batteryswap.services.interfaces;
import com.ev.batteryswap.pojo.PaymentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IPaymentInfoService {

    Page<PaymentInfo> filterPaymentInfo(Integer userId, Pageable pageable);
}
