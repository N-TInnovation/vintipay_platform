package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "conta_pagamento")
@Getter
@Setter
public class ContaPagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "acct_info_id")
    private InfoContaPagamentoEntity acctInfo;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "addr_match")
    private String addrMatch;
    
    @Column(name = "bill_addr_country")
    private String billAddrCountry;
    
    @Column(name = "bill_addr_city")
    private String billAddrCity;
    
    @Column(name = "bill_addr_line1")
    private String billAddrLine1;
    
    @Column(name = "bill_addr_line2")
    private String billAddrLine2;
    
    @Column(name = "bill_addr_line3")
    private String billAddrLine3;
    
    @Column(name = "bill_addr_post_code")
    private String billAddrPostCode;
    
    @Column(name = "bill_addr_state")
    private String billAddrState;
    
    @Column(name = "ship_addr_country")
    private String shipAddrCountry;
    
    @Column(name = "ship_addr_city")
    private String shipAddrCity;
    
    @Column(name = "ship_addr_line1")
    private String shipAddrLine1;
    
    @Column(name = "ship_addr_post_code")
    private String shipAddrPostCode;
    
    @Column(name = "ship_addr_state")
    private String shipAddrState;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_phone_id")
    private ContatoContaPagamentoEntity workPhone;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mobile_phone_id")
    private ContatoContaPagamentoEntity mobilePhone;
}
