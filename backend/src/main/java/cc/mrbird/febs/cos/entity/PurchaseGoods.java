package cc.mrbird.febs.cos.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 采购物品
 *
 * @author FanK
 */
@Data
public class PurchaseGoods {

    /**
     * 采购编号
     */
    private String purchaseCode;

    /**
     * 商品ID
     */
    private Integer commodityId;

    /**
     * 购买数量
     */
    private Integer outNum;

    /**
     * 价格
     */
    private BigDecimal itemPrice;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品型号
     */
    private String model;

    /**
     * 图册
     */
    private String images;
}
