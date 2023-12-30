package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.cos.dao.*;
import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.service.ICommodityInfoService;
import cc.mrbird.febs.cos.service.ICommodityTypeService;
import cc.mrbird.febs.cos.service.IOrderInfoService;
import cc.mrbird.febs.system.service.IMailService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    private final UserInfoMapper userInfoMapper;

    private final CommodityTypeMapper commodityTypeMapper;

    private final ICommodityInfoService commodityInfoService;

    private final OrderInfoMapper orderInfoMapper;

    private final BulletinInfoMapper bulletinInfoMapper;

    private final TemplateEngine templateEngine;

    private final IMailService mailService;

    /**
     * 分页获取订单信息
     *
     * @param page          分页对象
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectOrderPage(Page<OrderInfo> page, OrderInfo orderInfo) {
        return baseMapper.selectOrderPage(page, orderInfo);
    }

    /**
     * 添加订单
     *
     * @param orderInfo 订单信息
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public boolean orderSave(OrderInfo orderInfo) throws Exception {
        if (orderInfo.getUserId() == null) {
            throw new FebsException("所属用户不能为空！");
        }
        UserInfo user = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, orderInfo.getUserId()));
        orderInfo.setUserId(user.getId());

        // 发送邮件
        if (StrUtil.isNotEmpty(user.getMail())) {
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", user.getName() + "，您好。您的订单已生成，请注意查看");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(user.getMail(), DateUtil.formatDate(new Date()), emailContent);
        }
        return this.save(orderInfo);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectDetailById(Integer id) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("user", null);
                put("order", null);
                put("room", null);
                put("goodsList", Collections.emptyList());
            }
        };
        // 订单信息
        OrderInfo order = this.getById(id);
        result.put("order", order);
        // 所属用户
        UserInfo user = userInfoMapper.selectById(order.getUserId());
        result.put("user", user);
        // 购买内容
        List<PurchaseGoods> goodsList = JSONUtil.toList(order.getGoodsList(), PurchaseGoods.class);
        result.put("goodsList", StrUtil.isEmpty(order.getContent()) ? Collections.emptyList() : JSONUtil.toList(order.getContent(), PurchaseGoods.class));
        return result;
    }

    /**
     * 数据统计
     *
     * @param checkDate 选择日期
     * @param typeId    房间类型
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectRoomStatistics(String checkDate, Integer typeId) {
        if (StrUtil.isEmpty(checkDate)) {
            checkDate = DateUtil.formatDate(new Date());
        }

        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        // 获取当前月份及当前月份
        String year = StrUtil.toString(DateUtil.year(DateUtil.parseDate(checkDate)));
        String month = StrUtil.toString(DateUtil.month(DateUtil.parseDate(checkDate)) + 1);

        // 本月收益
        List<LinkedHashMap<String, Object>> priceByMonth = baseMapper.selectPriceByMonth(year, month, typeId, checkDate);
        result.put("priceByMonth", priceByMonth);

        // 本月订单
        List<LinkedHashMap<String, Object>> orderNumByMonth = baseMapper.selectOrderNumByMonth(year, month, typeId, checkDate);
        result.put("orderNumByMonth", orderNumByMonth);

        return result;
    }

    /**
     * 获取主页统计数据
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectHomeData() {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("staffNum", 0);
                put("totalRevenue", 0);
                put("totalOrderNum", 0);
                put("roomNum", 0);
                put("monthOrderNum", 0);
                put("monthOrderTotal", 0);
                put("yearOrderNum", 0);
                put("yearOrderTotal", 0);
            }
        };

        List<OrderInfo> allOrderList = orderInfoMapper.selectList(lambdaQuery().getWrapper());

        // 总收益
        BigDecimal orderTotal = allOrderList.stream().map(OrderInfo::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalRevenue", orderTotal);

        // 总订单量
        int orderCount = allOrderList.size();
        result.put("totalOrderNum", orderCount);


        // 获取当前月份及当前月份
        String year = StrUtil.toString(DateUtil.year(new Date()));
        String month = StrUtil.toString(DateUtil.month(new Date()) + 1);

        // 本年订单
        List<OrderInfo> orderList = baseMapper.selectOrderByDate(year, null);
        Map<String, List<OrderInfo>> orderMap = orderList.stream().collect(Collectors.groupingBy(OrderInfo::getMonth));

        // 本月订单量
        int orderMonthCount = CollectionUtil.isEmpty(orderMap.get(month)) ? 0 : orderMap.get(month).size();
        result.put("monthOrderNum", orderMonthCount);

        // 本月收益
        BigDecimal orderMonthTotal = CollectionUtil.isEmpty(orderMap.get(month)) ? BigDecimal.ZERO : orderMap.get(month).stream().map(OrderInfo::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("monthOrderTotal", orderMonthTotal);

        // 本年订单量
        Integer orderYearCount = CollectionUtil.isEmpty(orderList) ? 0 : orderList.size();
        result.put("yearOrderNum", orderYearCount);

        // 本年收益
        BigDecimal orderYearTotal = CollectionUtil.isEmpty(orderList) ? BigDecimal.ZERO : orderList.stream().map(OrderInfo::getOrderPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("yearOrderTotal", orderYearTotal);

        // 近十天内收入统计
        List<LinkedHashMap<String, Object>> priceDay = baseMapper.selectPriceByDay();
        result.put("priceDay", priceDay);

        // 近十天内工单统计
        List<LinkedHashMap<String, Object>> orderNumDay = baseMapper.selectOrderNumByDay();
        result.put("orderNumDay", orderNumDay);

        // 公告信息
        result.put("bulletin", bulletinInfoMapper.selectList(Wrappers.<BulletinInfo>lambdaQuery()));
        return result;
    }

    /**
     * 添加订单
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean purchaseSave(OrderInfo orderInfo) {
        orderInfo.setCode("ORD-" + System.currentTimeMillis());
        // 设置用户信息
        UserInfo user = userInfoMapper.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, orderInfo.getUserId()));
        orderInfo.setUserId(user.getId());

        List<PurchaseGoods> goodsList = JSONUtil.toList(orderInfo.getGoodsList(), PurchaseGoods.class);
        List<Integer> commodityIdList = goodsList.stream().map(PurchaseGoods::getCommodityId).collect(Collectors.toList());

        // 获取商品信息
        List<CommodityInfo> commodityList = (List<CommodityInfo>) commodityInfoService.listByIds(commodityIdList);
        Map<Integer, CommodityInfo> commodityMap = commodityList.stream().collect(Collectors.toMap(CommodityInfo::getId, e -> e));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PurchaseGoods goods : goodsList) {
            goods.setPurchaseCode(orderInfo.getCode());
            CommodityInfo commodity = commodityMap.get(goods.getCommodityId());
            // 减少库存
            commodity.setStockNum(commodity.getStockNum() - goods.getOutNum());
            // 计算价格
            BigDecimal itemPrice = BigDecimal.valueOf(goods.getOutNum()).multiply(commodity.getPrice());
            goods.setName(commodity.getName());
            goods.setModel(commodity.getModel());
            goods.setImages(commodity.getImages());
            goods.setItemPrice(itemPrice);
            totalPrice = totalPrice.add(itemPrice);
        }

        orderInfo.setOrderPrice(totalPrice);
        // 更新库存
        commodityInfoService.updateBatchById(commodityList);
        // 添加详情
        orderInfo.setContent(JSONUtil.toJsonStr(goodsList));
        // 发送邮件
        if (StrUtil.isNotEmpty(user.getMail())) {
            Context context = new Context();
            context.setVariable("today", DateUtil.formatDate(new Date()));
            context.setVariable("custom", user.getName() + "，您好。您购买的物品已下单【"+ orderInfo.getCode() +"】共花费"+ orderInfo.getOrderPrice() +"元，如有疑问请联系管理员");
            String emailContent = templateEngine.process("registerEmail", context);
            mailService.sendHtmlMail(user.getMail(), orderInfo.getCode() + "：订单通知", emailContent);
        }
        return this.save(orderInfo);
    }
}
