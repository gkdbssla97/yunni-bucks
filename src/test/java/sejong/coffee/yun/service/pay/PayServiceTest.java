package sejong.coffee.yun.service.pay;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sejong.coffee.yun.controller.pay.CreatePaymentData;
import sejong.coffee.yun.domain.order.Order;
import sejong.coffee.yun.domain.pay.CardPayment;
import sejong.coffee.yun.domain.user.Card;
import sejong.coffee.yun.domain.user.Member;
import sejong.coffee.yun.dto.pay.CardPaymentDto;
import sejong.coffee.yun.infra.ApiService;
import sejong.coffee.yun.infra.fake.FakeApiService;
import sejong.coffee.yun.infra.fake.FakeUuidHolder;
import sejong.coffee.yun.repository.card.CardRepository;
import sejong.coffee.yun.repository.card.fake.FakeCardRepository;
import sejong.coffee.yun.repository.order.OrderRepository;
import sejong.coffee.yun.repository.order.fake.FakeOrderRepository;
import sejong.coffee.yun.repository.pay.PayRepository;
import sejong.coffee.yun.repository.pay.fake.FakePayRepository;
import sejong.coffee.yun.repository.user.UserRepository;
import sejong.coffee.yun.repository.user.fake.FakeUserRepository;
import sejong.coffee.yun.service.PayService;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static sejong.coffee.yun.domain.pay.PaymentStatus.DONE;

public class PayServiceTest extends CreatePaymentData {

    private PayService payService;
    private FakeApiService fakeApiService;
    private PayRepository payRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private CardRepository cardRepository;

    @BeforeEach
    void init() {
        fakeApiService = new FakeApiService("paypaypaypay_1234");
        payRepository = new FakePayRepository();
        orderRepository = new FakeOrderRepository();
        cardRepository = new FakeCardRepository();
        userRepository = new FakeUserRepository();

        this.payService = PayService.builder()
                .payRepository(payRepository)
                .uuidHolder(new FakeUuidHolder("qwerqewrqwer"))
                .apiService(new ApiService(fakeApiService))
                .orderRepository(orderRepository)
                .cardRepository(cardRepository)
                .build();

        Member saveMember = userRepository.save(member);

        Card buildCard = Card.builder()
                .member(saveMember)
                .number(card.getNumber())
                .cardPassword(card.getCardPassword())
                .validThru(card.getValidThru())
                .build();

        cardRepository.save(buildCard);
        Order saveOrder = Order.createOrder(saveMember, menuList, money, LocalDateTime.now());
        orderRepository.save(saveOrder);
    }

    @Test
    void findById는_DONE_상태인_결제내역_단건을_조회한다() throws IOException, InterruptedException {

        //given
        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);
        CardPaymentDto.Response response = fakeApiService.callExternalAPI(request);
        CardPayment approvalPayment = CardPayment.approvalPayment(cardPayment, response.paymentKey(), request.requestedAt());

        payRepository.save(approvalPayment);

        //when
        CardPayment byId = payService.findById(1L);

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);
    }

    @Test
    void getByOrderId는_결제내역_단건을_조회한다() throws IOException, InterruptedException {
        //given
        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);
        CardPaymentDto.Response response = fakeApiService.callExternalAPI(request);
        CardPayment approvalPayment = CardPayment.approvalPayment(cardPayment, response.paymentKey(), request.requestedAt());

        payRepository.save(approvalPayment);

        //when
        CardPayment byId = payService.getByOrderId("asdfasdf");

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);
        assertThat(byId.getOrderUuid()).isEqualTo("asdfasdf");

    }

    @Test
    void getByPaymentKey는_결제내역_단건을_조회한다() throws IOException, InterruptedException {
        //given
        CardPaymentDto.Request request = CardPaymentDto.Request.from(cardPayment);
        CardPaymentDto.Response response = fakeApiService.callExternalAPI(request);
        CardPayment approvalPayment = CardPayment.approvalPayment(cardPayment, response.paymentKey(), request.requestedAt());

        payRepository.save(approvalPayment);

        //when
        CardPayment byId = payService.getByPaymentKey("paypaypaypay_1234");

        //then
        assertThat(byId.getPaymentStatus()).isEqualTo(DONE);
        assertThat(byId.getPaymentKey()).isEqualTo("paypaypaypay_1234");
        assertThat(byId.getOrderUuid()).isEqualTo("asdfasdf");
    }

    @Test
    void initPayment는_전달받은_OrderId로_CardPayment를_만든다() {

        //given
        Long orderId = 1L;
        Long memberId = 1L;
        orderRepository.findById(orderId);

        //when
        CardPaymentDto.Request request = payService.initPayment(orderId, memberId);

        //then
        assertThat(request.orderUuid()).isEqualTo("qwerqewrqwer");
    }

    @Test
    void pay는_카드결제를_수행한다() throws IOException, InterruptedException {

        //given
        Long orderId = 1L;
        Long memberId = 1L;
        Order byId = orderRepository.findById(orderId);
        userRepository.findById(memberId);
        //when
        CardPaymentDto.Request request = payService.initPayment(orderId, memberId);
        CardPayment cardPayment = payService.pay(request);

        System.out.println(cardPayment.getOrder());
        //then
        assertThat(cardPayment.getPaymentKey()).isEqualTo("paypaypaypay_1234");
        assertThat(cardPayment.getOrderUuid()).isEqualTo("qwerqewrqwer");
        assertThat(cardPayment.getOrder().getOrderPrice().getTotalPrice().toString()).isEqualTo("3000");
        assertThat(cardPayment.getCustomerName()).isEqualTo("하윤");

        Card byMemberId = cardRepository.findByMemberId(memberId);
        assertThat(byMemberId.getMember().getName()).isEqualTo(cardPayment.getCustomerName());
    }
}
