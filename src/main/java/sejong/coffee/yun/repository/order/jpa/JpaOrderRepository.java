package sejong.coffee.yun.repository.order.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.coffee.yun.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByMemberId(Long memberId);
    Optional<Order> findByMemberId(Long memberId);
}
