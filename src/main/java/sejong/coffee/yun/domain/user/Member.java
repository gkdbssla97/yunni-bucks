package sejong.coffee.yun.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.coffee.yun.domain.DateTimeEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends DateTimeEntity {

    @Id @GeneratedValue
    private Long id;
    @Column(name = "user_name")
    private String name;
    private String password;
    @Column(name = "email")
    private String email;
    @Enumerated(value = EnumType.STRING)
    private UserRank userRank;
    private Address address;
    private Money money;
    @Column(name = "order_count")
    private Integer orderCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Builder
    public Member(String email, String name, String password,
                  UserRank userRank, Address address, Money money,
                  Integer orderCount, Coupon coupon) {

        this.name = name;
        this.password = password;
        this.email = email;
        this.userRank = userRank;
        this.address = address;
        this.money = money;
        this.coupon = coupon;
        this.orderCount = orderCount;
    }

    private Member(Long id, Member member) {
        this(member.email, member.name, member.password, member.userRank, member.address, member.money, member.orderCount, member.coupon);
        this.id = id;
    }

    public static Member from(Long id, Member member) {
        return new Member(id, member);
    }

    public void upgradeUserRank(int orderCount) {
        this.userRank = UserRank.calculateUserRank(orderCount);
    }

    public void addOrderCount() {
        this.orderCount++;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public BigDecimal fetchTotalPrice() {
        return this.money.getTotalPrice();
    }
    public boolean hasCoupon() {
        return this.coupon != null && this.coupon.hasAvailableCoupon();
    }
}
