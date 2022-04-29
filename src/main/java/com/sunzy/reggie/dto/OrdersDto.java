package com.sunzy.reggie.dto;

import com.sunzy.reggie.domain.OrderDetail;
import com.sunzy.reggie.domain.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
