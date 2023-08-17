package sejong.coffee.yun.repository.delivery.fake;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.domain.delivery.*;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.order.menu.Beverage;
import sejong.coffee.yun.domain.order.menu.Menu;
import sejong.coffee.yun.domain.order.menu.MenuSize;
import sejong.coffee.yun.domain.order.menu.Nutrients;
import sejong.coffee.yun.domain.user.Address;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.domain.user.Money;
import sejong.coffee.yun.domain.user.UserRank;
import sejong.coffee.yun.mock.repository.FakeDeliveryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FakeDeliveryRepositoryTest {

    private final FakeDeliveryRepository deliveryRepository;

    public FakeDeliveryRepositoryTest() {
        this.deliveryRepository = new FakeDeliveryRepository();
    }

    static Order order;
    static Menu menu;
    static List<Menu> menuList = new ArrayList<>();
    static Member member;
    static ReserveDelivery reserveDelivery;
    static NormalDelivery normalDelivery;
    static List<Delivery> list;

    @BeforeAll
    static void init() {
        member = Member.builder()
                .address(new Address("서울시", "광진구", "화양동", "123-432"))
                .userRank(UserRank.BRONZE)
                .name("홍길동")
                .password("qwer1234@A")
                .money(Money.ZERO)
                .email("qwer123@naver.com")
                .orderCount(0)
                .build();

        Nutrients nutrients = new Nutrients(80, 80, 80, 80);
        menu = new Beverage("커피", "에티오피아산 커피",
                Money.initialPrice(new BigDecimal(1000)), nutrients, MenuSize.M, LocalDateTime.now());

        menuList.add(menu);
        order = Order.createOrder(member, menuList, Money.initialPrice(new BigDecimal("10000")), LocalDateTime.now());

        reserveDelivery = ReserveDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY,
                LocalDateTime.now()
        );

        normalDelivery = NormalDelivery.create(
                order,
                LocalDateTime.now(),
                member.getAddress(),
                DeliveryType.RESERVE,
                DeliveryStatus.READY
        );

        list = List.of(reserveDelivery, normalDelivery);
    }

    @Test
    void findAll() {
        // given
        list.forEach(deliveryRepository::save);

        // when
        List<Delivery> deliveries = deliveryRepository.findAll();

        // then
        assertThat(deliveries.size()).isEqualTo(1);
    }
}