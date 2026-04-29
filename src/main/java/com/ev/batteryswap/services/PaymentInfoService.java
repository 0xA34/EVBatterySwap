package com.ev.batteryswap.services;
import com.ev.batteryswap.pojo.PaymentInfo;
import com.ev.batteryswap.repositories.PaymentInfoRepository;
import com.ev.batteryswap.services.interfaces.IPaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PaymentInfoService implements IPaymentInfoService {

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;


    @Override
    public Page<PaymentInfo> filterPaymentInfo(Integer userId, Pageable pageable) {
        return paymentInfoRepository.findAll((root, query, cb) -> {

            // Luôn tạo 1 predicate "true"
            var predicate = cb.conjunction();

            // Nếu có truyền userId thì thêm điều kiện
            if (userId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("user").get("id"), userId)
                );
            }
            return predicate;
        }, pageable);
    }


    // thêm lịch sử giao dịch mới
    public PaymentInfo save(PaymentInfo paymentInfo){
        return paymentInfoRepository.save(paymentInfo);
    }

}
